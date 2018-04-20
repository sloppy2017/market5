package com.c2b.wallet.controller;

import com.c2b.config.CoreMessageSource;
import com.c2b.constant.CodeConstant;
import com.c2b.constant.Constant;
import com.c2b.util.AES;
import com.c2b.wallet.entity.UserCoin;
import com.c2b.wallet.entity.WithdrawLog;
import com.c2b.wallet.mapper.UserCoinMapper;
import com.c2b.wallet.service.DepositService;
import com.c2b.wallet.service.LiteWalletService;
import com.c2b.wallet.service.SysGencodeService;
import com.c2b.wallet.service.WithDrawService;
import com.c2b.wallet.util.LiteWalletAsync;
import com.google.litecoin.core.*;
import com.google.litecoin.crypto.KeyCrypterException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client/ltc")
public class LtcWalletController {

  Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private LiteWalletService liteWalletService;

  @Autowired
  private CoreMessageSource messageSource;

  @Autowired
  private SysGencodeService sysGencodeService;

  @Autowired
  private UserCoinMapper userCoinMapper;

  @Autowired
  private DepositService depositService;

  @Autowired
  private WithDrawService withDrawService;


  @PostMapping("/create")
  public AjaxResponse createWallet(@RequestParam("account") String account) {
    Map<String, Object> data = new HashMap<String, Object>();
    UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(account, Constant.CURRENCY_LTC);
    if (userCoin != null) {
      return AjaxResponse.falied(messageSource.getMessage("existAddress", userCoin.getAddress()), CodeConstant.PARAM_ERROR);
    }

    NetworkParameters params = LiteWalletAsync.params;
    String address = null;
    try {
      address = liteWalletService.createWallet(params, account);
      data.put("address", address);
      data.put("userName", account);
      return AjaxResponse.success("莱特币钱包创建成功", data);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return AjaxResponse.falied("莱特币钱包创建失败", CodeConstant.SYS_ERROR);
  }

  @GetMapping("/balance")
  public AjaxResponse bananceWallet() {

    try {
      Wallet wallet = LiteWalletAsync.kit.wallet();
      logger.info("---------wallet=" + wallet.toString());
      List<ECKey> ecKeys = wallet.getKeys();

      logger.info("---------ltc----导入数据库《前》所有key------------");
      logger.info("ecKeys.size()=" + ecKeys.size());
      for (ECKey key : ecKeys) {
        logger.info("key=" + key);
      }
      //导入数据库里所有key
      logger.info("---------ltc----导入数据库中的所有key------------");
      List<UserCoin> userCoinList = userCoinMapper.getAllUserCoin(Constant.CURRENCY_LTC);
      if (userCoinList != null) {
        for (UserCoin userCoin : userCoinList) {
          if (userCoin.getPrivateKey() != null && userCoin.getAddress() != null) {
            try {
              ECKey key;
              logger.info("ecoWallet.getPrivateKey()=" + userCoin.getPrivateKey());
              String privateKey = AES.decrypt(userCoin.getPrivateKey());
              logger.info("AES.decrypt(ecoWallet.getPrivateKey())=" + privateKey);
              if (privateKey == null) {
                continue;
              }
                    /*if (privateKey.length() == 51) {
            				    DumpedPrivateKey dumpedPrivateKey = new DumpedPrivateKey(LiteWalletAsync.params, privateKey);
            				    key = dumpedPrivateKey.getKey();
            				} else {
            				    BigInteger privKey = Base58.decodeToBigInteger(privateKey);
            				    key = new ECKey(privKey);
            				}*/
              DumpedPrivateKey dumpedPrivateKey = new DumpedPrivateKey(LiteWalletAsync.params, privateKey);
              key = dumpedPrivateKey.getKey();
              boolean containsFlag = LiteWalletAsync.kit.wallet().getKeys().contains(key);
              logger.info("-----userCoin.getAddress()=" + userCoin.getAddress());
              logger.info("-----key.toAddress(LiteWalletAsync.params).toString()=" + key.toAddress(LiteWalletAsync.params).toString());
              boolean addressFlag = key.toAddress(LiteWalletAsync.params).toString().equals(userCoin.getAddress());

              logger.info("contains key :{}, address : {}, data key {}", key,
                  key.toAddress(LiteWalletAsync.params).toString(), userCoin.getAddress());
              logger.info("addressFlag , {},containsFlag :{}", addressFlag, containsFlag);
              if (!containsFlag && addressFlag) {
                LiteWalletAsync.kit.wallet().addKey(key);
              }
            } catch (AddressFormatException e) {
              logger.info("数据格式异常 ", e);
            } catch (Exception e) {
              logger.info(e.getMessage(), e);
            }
          }
        }
      }
      logger.info("----------LTC批量导入《后》ecKeys.size()=" + ecKeys.size());
      for (ECKey ecKey : ecKeys) {
        logger.info("LTC address= " + ecKey.toAddress(LiteWalletAsync.params).toString());
      }

      return AjaxResponse.success("查询成功", Utils.bitcoinValueToFriendlyString(wallet.getBalance()));
    } catch (Exception e) {
      e.printStackTrace();
      return AjaxResponse.falied(e.getMessage());
    }
  }

  @PostMapping("/sendMoney")
  public AjaxResponse sendMoney(HttpServletRequest request, @RequestBody WithdrawLog withdrawLog) {
    try {
      if (withdrawLog == null || StringUtils.isBlank(withdrawLog.getAccount()) || withdrawLog.getMoney() == null ||
          StringUtils.isBlank(withdrawLog.getToAddress())) {
        return AjaxResponse.falied(messageSource.getMessage("paramError"), CodeConstant.PARAM_ERROR);
      }
      UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(withdrawLog.getAccount(), Constant.CURRENCY_LTC);
      if (userCoin == null) {
        return AjaxResponse.falied(messageSource.getMessage("accountError"), CodeConstant.PARAM_ERROR);
      }
      if (userCoin.getAddress().equals(withdrawLog.getToAddress())) {
        return AjaxResponse.falied(messageSource.getMessage("ownAddressError"), CodeConstant.PARAM_ERROR);
      }
      UserCoin userCoin1 = userCoinMapper.getUserCoinByAddress(withdrawLog.getToAddress());
      if (userCoin1 != null) {
        return AjaxResponse.falied("请勿给系统内地址提币", CodeConstant.PARAM_ERROR);
      }
      //校验地址是否有误
      try {
        new Address(LiteWalletAsync.params, withdrawLog.getToAddress());
      } catch (Exception e) {
        e.printStackTrace();
        logger.info("--------LTC address is error,value is " + withdrawLog.getToAddress());
        return AjaxResponse.falied("LTC地址有误！", CodeConstant.PARAM_ERROR);
      }
      String uplimit = "";
      String lowlimit = "";
      List<Map<String, String>> codeList = sysGencodeService.findByGroupCode("WITHDRAW_LIMIT");
      for (Map<String, String> mapObj : codeList) {
        if ("LTC_LIMIT".equals(String.valueOf(mapObj.get("code_name")))) {
          uplimit = mapObj.get("uplimit").toString();
          lowlimit = mapObj.get("lowlimit").toString();
        }
      }
      if (withdrawLog.getMoney().compareTo(new BigDecimal(lowlimit)) < 0) {
        return AjaxResponse.falied(messageSource.getMessage("coinLowLimit", Constant.CURRENCY_LTC, lowlimit), CodeConstant.PARAM_ERROR);
      }

      if (!StringUtils.isEmpty(uplimit) && withdrawLog.getMoney().compareTo(new BigDecimal(uplimit)) > 0) {
        return AjaxResponse.falied(messageSource.getMessage("coinUpLimit", Constant.CURRENCY_LTC, uplimit), CodeConstant.PARAM_ERROR);
      }

      String ltc_fee = "";//手续费（包含矿工费）
      List<Map<String, String>> codeList1 = sysGencodeService.findByGroupCode("WITHDRAW_SERVICE_CHARGE");
      for (Map<String, String> mapObj : codeList1) {
        if ("LTC_FEE".equals(String.valueOf(mapObj.get("code_name")))) {
          ltc_fee = mapObj.get("code_value").toString();
        }
      }
            
        	/*UserLogin userLogin = userLoginMapper.getUserByAccAndPass(withdrawRecord.getUserName(), withdrawRecord.getPassword());
        	if(userLogin == null){
        		logger.error("---------------用户名或密码错误！");
        		return AjaxResponse.falied("params is error", CodeConstant.PARAM_ERROR);
        	}
        	WithdrawRecord tWithdrawRecord = withdrawRecordMapper.selectByPrimaryKey(withdrawRecord.getId());
            if (!WalletConstant.APPROVE.equals(tWithdrawRecord.getStatus())){
                logger.error("----------------提币订单状态异常！");
                return AjaxResponse.falied("params is error", CodeConstant.PARAM_ERROR);
            }*/
      String hash = liteWalletService.sendMoney(LiteWalletAsync.params, withdrawLog);
      return AjaxResponse.success("提币发送成功", hash);
    } catch (KeyCrypterException e) {
      // We don't use encrypted wallets in this example - can never happen.
      e.printStackTrace();
    } catch (InsufficientMoneyException e) {
      // This should never happen - we're only trying to forward what we received!
      e.printStackTrace();
    } catch (AddressFormatException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return AjaxResponse.falied("提币失败", CodeConstant.UPDATE_FAIL);
  }

  /**
   * 校验地址是否正确
   *
   * @param request
   * @return
   * @throws Exception
   */
  @GetMapping("/isValidAddress/{address}")
  public AjaxResponse isValidAddress(@PathVariable("address") String address) throws Exception {
    try {
      new Address(LiteWalletAsync.params, address);
    } catch (Exception e) {
      e.printStackTrace();
      logger.info("--------LTC address is error,value is " + address);
      return AjaxResponse.falied("LTC地址有误！", CodeConstant.PARAM_ERROR);
    }
    return AjaxResponse.success("LTC地址正确!", address);
  }

  public static void main(String[] args) {
    System.out.println("T8qcGmGoPSX2Ji3ZnkhdUwceba9yGtacamDoFGMHRgGGxFM5zS1g".length());
  }

  @GetMapping("getAddress")
    /*@ApiImplicitParams({
        @ApiImplicitParam(paramType = "path", name = "account", value = "账号", dataType = "String", required = true)
    })*/
  public AjaxResponse getAddress(@RequestParam("account") String account) {
    UserCoin userCoin = userCoinMapper.getUserCoinByAccountAndCurrency(account, Constant.CURRENCY_LTC);
    if (userCoin == null) {
      return AjaxResponse.falied(messageSource.getMessage("accountError"), CodeConstant.PARAM_ERROR);
    }
    return AjaxResponse.success("查询成功", userCoin.getAddress());
  }
    
    /*@ApiOperation(value = "提币回调测试")
    @PostMapping("/withdraw")
    public String confirmCallback(@RequestParam("address") String address,@RequestParam("currencyType") int currencyType,
			@RequestParam("userName")String userName,@RequestParam("hxId")String hxId,@RequestParam("amount")BigDecimal amount) {
    	logger.info("--------address="+address);
    	String confirmCallback = withDrawService.confirmCallback(address, currencyType, userName, hxId, amount);
    	logger.info("---confirmCallback="+confirmCallback);
    	return confirmCallback;
    }
    
    @ApiOperation(value = "充值广播回调测试")
    @PostMapping("/depositBroadcastCallback")
    public String depositBroadcastCallback(@RequestParam("address") String address,@RequestParam("currencyType") int currencyType,
			@RequestParam("userName")String userName,@RequestParam("hxId")String hxId,@RequestParam("amount")BigDecimal amount) {
    	logger.info("--------address="+address);
    	String broadcastCallback = depositService.broadcastCallback(address, currencyType, userName, hxId, amount);
    	logger.info("---broadcastCallback="+broadcastCallback);
    	return broadcastCallback;
    }
    
    @ApiOperation(value = "充值广播回调测试")
    @PostMapping("/depositconfirmCallback")
    public String depositconfirmCallback(@RequestParam("address") String address,@RequestParam("currencyType") int currencyType,
			@RequestParam("userName")String userName,@RequestParam("hxId")String hxId,@RequestParam("amount")BigDecimal amount) {
    	logger.info("--------address="+address);
    	String depositconfirmCallback = depositService.confirmCallback(address, currencyType, userName, hxId, amount);
    	logger.info("---depositconfirmCallback="+depositconfirmCallback);
    	return depositconfirmCallback;
    }*/
}
