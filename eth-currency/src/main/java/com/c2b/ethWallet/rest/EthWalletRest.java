package com.c2b.ethWallet.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.web.common.BaseRest;
import com.c2b.ethWallet.entity.UserCoin;
import com.c2b.ethWallet.entity.WithdrawLog;
import com.c2b.ethWallet.service.DepositService;
import com.c2b.ethWallet.service.EthWalletService;
import com.c2b.ethWallet.service.SysGencodeService;
import com.c2b.ethWallet.service.WithDrawService;
import com.c2b.ethWallet.util.Constant;
import com.github.pagehelper.StringUtil;

/**
 * 
 * @author Anne
 *
 */
@RestController
@Api("以太坊钱包")
@RequestMapping("/client/eth")
public class EthWalletRest extends BaseRest {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private EthWalletService ethWalletService;

  @Value("${ETH.hot.address}")
  private String ethWalletHotAddress;

  @Value("${ETH.hot.privateKey}")
  private String privateKey;

  @Autowired
  private DepositService depositService;

  @Autowired
  private WithDrawService withDrawService;

  @Autowired
  private SysGencodeService sysGencodeService;
  

  @ApiOperation(value = "地址校验接口", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @GetMapping("/validAddress/{address}")
  @ApiImplicitParams({ @ApiImplicitParam(paramType = "path", name = "address", value = "以太坊地址", dataType = "String", required = true) })
  public AjaxResponse validAddress(@PathVariable("address") String address) {
    logger.info("privateKey="+privateKey);
    try {
      if (!ethWalletService.isValidAddress(address))
        return writeObj(ErrorMsgEnum.ETH_ADDRESS_ERROR);
    } catch (Exception e) {
      e.printStackTrace();
      return writeObj(ErrorMsgEnum.ETH_ADDRESS_ERROR);
    }
    return writeObj(address);
  }

  @ApiOperation(value = "创建以太坊钱包", notes = "创建以太坊钱包")
  @ApiImplicitParams({ @ApiImplicitParam(name = "account", value = "账号", dataType = "String", required = true, paramType = "query") })
  @PostMapping("/create")
  public AjaxResponse createEthWallet(@RequestParam("account") String account) {
    if (StringUtil.isEmpty(account)) {
      logger.error("account is null!");
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    String address = ethWalletService.createEthWallet(account);
    if (address == null) {
      return writeObj(ErrorMsgEnum.ETH_ADDRESS_FAIL);
    }
    return writeObj(address);
  }

  @ApiOperation(value = "根据账号查询地址")
  @ApiImplicitParams({ @ApiImplicitParam(name = "account", value = "账号", dataType = "String", required = true, paramType = "query") })
  @GetMapping("/getAddress")
  public AjaxResponse getAddressByAccount(
      @RequestParam("account") String account) {
    if (StringUtil.isEmpty(account)) {
      logger.error("account is null!");
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    String address = ethWalletService.getAddressByAccount(account);
    if ("".equals(address) || null == address) {
      return writeObj(ErrorMsgEnum.ETH_ADDRESS_NOT_EXITS);
    }
    return writeObj(address);
  }

  @ApiOperation(value = "查询以太坊钱包主账户余额")
  @GetMapping("/getEthPrimaryAccountBalance")
  public AjaxResponse getEthPrimaryAccountBalance() {
    logger.info("primaryAccountAddress: " + ethWalletHotAddress);
    return writeObj(ethWalletService.getBalanceEther(ethWalletHotAddress));
  }

  @ApiOperation(value = "提币", notes = "提币,参数：userName 用户名，password 密码，money 金额，address 地址")
  @PostMapping("/sendMoney")
  @ApiImplicitParams({ @ApiImplicitParam(name = "withdrawLog", value = "提币参数", required = true, dataType = "WithdrawLog") })
  public AjaxResponse sendMoney(HttpServletRequest request,
      @RequestBody WithdrawLog withdrawLog) {
    withdrawLog.setCurrency(Constant.CURRENCY_ETH);
    logger.info("orderNo="+withdrawLog.getOrderNo()+",currency="+withdrawLog.getCurrency());
    if (withdrawLog == null || StringUtils.isBlank(withdrawLog.getAccount())
        || StringUtils.isBlank(withdrawLog.getToAddress())) {
      logger.info("提币传参错误，请检查account、 toAddress等各个提币参数项！");
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    if (!ethWalletService.checkExistAddress(withdrawLog.getAccount())) {
      logger.info("根据account："+withdrawLog.getAccount()+"查询UserCoin表不存在对应数据！");
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    String address = ethWalletService.getAddressByAccount(withdrawLog
        .getAccount());
    logger.info("根据account查到对应地址address="+address);
    if (address.equals(withdrawLog.getToAddress())) {// 转入转出地址是同一个
      logger.info("不能给自己转账，转入转出地址是同一个地址："+address);
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    UserCoin userCoin = ethWalletService.getUserCoinByAddress(withdrawLog.getToAddress(), withdrawLog.getCurrency());
    if (userCoin != null) {
      logger.info("根据toAddress："+withdrawLog.getToAddress()+"查询UserCoin表不存在对应数据！");
      return writeObj(ErrorMsgEnum.PARAM_ERROR);
    }
    if (!ethWalletService.isValidAddress(address)) {
      logger.info("该address："+address+"不是以太坊地址！");
      return writeObj(ErrorMsgEnum.ETH_ADDRESS_ERROR);
    }

    String uplimit = "";
    String lowlimit = "";
    List<Map<String, String>> codeList = sysGencodeService
        .findByGroupCode("WITHDRAW_LIMIT");
    for (Map<String, String> mapObj : codeList) {
      if ("ETH_LIMIT".equals(String.valueOf(mapObj.get("code_name")))) {
        uplimit = mapObj.get("uplimit").toString();
        lowlimit = mapObj.get("lowlimit").toString();
      }
    }
    if (withdrawLog.getMoney().compareTo(new BigDecimal(lowlimit)) < 0) {
      logger.info("交易金额："+withdrawLog.getMoney()+"低于下限值："+lowlimit+"，不能提币！");
      return writeObj(ErrorMsgEnum.ETH_LOWER_THANLOWLIMIT);
    }

    if (!StringUtils.isEmpty(uplimit)
        && withdrawLog.getMoney().compareTo(new BigDecimal(uplimit)) > 0) {
      logger.info("交易金额："+withdrawLog.getMoney()+"超过上限值："+uplimit+"，不能提币！");
      return writeObj(ErrorMsgEnum.ETH_UPER_THANUPLIMIT);
    }
    String ethFee = "";// 手续费（包含矿工费）
    List<Map<String, String>> ethFeeCodeList = sysGencodeService
        .findByGroupCode("WITHDRAW_SERVICE_CHARGE");
    for (Map<String, String> mapObj : ethFeeCodeList) {
      if ("ETH_FEE".equals(String.valueOf(mapObj.get("code_name")))) {
        ethFee = mapObj.get("code_value").toString();
      }
    }
    String hash = null;
    try {
      withdrawLog.setFree(new BigDecimal(ethFee));
      Map<String, Object> map = ethWalletService.sendMoney(withdrawLog);
      if ("success".equals(map.get("code"))) {
        hash = (String) map.get("data");
      }
      if (hash == null) {
        return writeObj(ErrorMsgEnum.ETH_WITHDRAW_FAIL);
      }
    } catch (ExecutionException | InterruptedException e) {
      return writeObj(ErrorMsgEnum.ETH_WITHDRAW_EXCEPTION);
    }
    return writeObj(hash);
  }

  @ApiOperation(value = "提币确认回调测试")
  @PostMapping("/withdraw")
  public String confirmCallback(@RequestParam("address") String address,
      @RequestParam("currencyType") int currencyType,
      @RequestParam("userName") String userName,
      @RequestParam("hxId") String hxId,
      @RequestParam("amount") BigDecimal amount,
      @RequestParam("orderNo")String orderNo) {
    logger.info("--------address=" + address);
    String confirmCallback = withDrawService.confirmCallback(address,
        currencyType, userName, hxId, amount, orderNo);
    logger.info("---confirmCallback=" + confirmCallback);
    return confirmCallback;
  }

  @ApiOperation(value = "充值广播回调测试")
  @PostMapping("/depositBroadcastCallback")
  public String depositBroadcastCallback(
      @RequestParam("address") String address,
      @RequestParam("currencyType") int currencyType,
      @RequestParam("userName") String userName,
      @RequestParam("hxId") String hxId,
      @RequestParam("amount") BigDecimal amount) {
    logger.info("--------address=" + address);
    String broadcastCallback = depositService.broadcastCallback(address,
        currencyType, userName, hxId, amount);
    logger.info("---broadcastCallback=" + broadcastCallback);
    return broadcastCallback;
  }

  @ApiOperation(value = "充值确认完成回调测试")
  @PostMapping("/depositconfirmCallback")
  public String depositconfirmCallback(@RequestParam("address") String address,
      @RequestParam("currencyType") int currencyType,
      @RequestParam("userName") String userName,
      @RequestParam("hxId") String hxId,
      @RequestParam("amount") BigDecimal amount) {
    logger.info("--------address=" + address);
    String depositconfirmCallback = depositService.confirmCallback(address,
        currencyType, userName, hxId, amount);
    logger.info("---depositconfirmCallback=" + depositconfirmCallback);
    return depositconfirmCallback;
  }

}
