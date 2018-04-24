//package com.c2b.ethWallet.rest;
//
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutionException;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.c2b.coin.common.AjaxResponse;
//import com.c2b.coin.common.enumeration.ErrorMsgEnum;
//import com.c2b.coin.web.common.BaseRest;
//import com.c2b.ethWallet.entity.UserCoin;
//import com.c2b.ethWallet.entity.WithdrawLog;
//import com.c2b.ethWallet.service.DepositService;
//import com.c2b.ethWallet.service.EthTokenWalletService;
//import com.c2b.ethWallet.service.SysGencodeService;
//import com.c2b.ethWallet.service.WithDrawService;
//import com.c2b.ethWallet.util.Constant;
//import com.github.pagehelper.StringUtil;
//
///**
// * 
// * @author Anne
// *
// */
//@RestController
//@Api("以太坊ZG钱包")
//@RequestMapping("/client/zg")
//public class ZgTokenWalletRest extends BaseRest {
//
//  private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//  @Autowired
//  private EthTokenWalletService ethTokenWalletService;
//
//  @Autowired
//  private DepositService depositService;
//
//  @Autowired
//  private WithDrawService withDrawService;
//
//  @Autowired
//  private SysGencodeService sysGencodeService;
//  
//
//  @ApiOperation(value = "地址校验接口", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//  @GetMapping("/validAddress/{address}")
//  @ApiImplicitParams({ @ApiImplicitParam(paramType = "path", name = "address", value = "以太坊地址", dataType = "String", required = true) })
//  public AjaxResponse validAddress(@PathVariable("address") String address) {
//    try {
//      if (!ethTokenWalletService.isValidAddress(address))
//        return writeObj(ErrorMsgEnum.ETH_ADDRESS_ERROR);
//    } catch (Exception e) {
//      e.printStackTrace();
//      return writeObj(ErrorMsgEnum.ETH_ADDRESS_ERROR);
//    }
//    return writeObj(address);
//  }
//
//  @ApiOperation(value = "创建以太坊zg钱包", notes = "创建以太坊omg钱包")
//  @ApiImplicitParams({ 
//    @ApiImplicitParam(name = "account", value = "账号", dataType = "String", required = true, paramType = "query")
//  })
//  @PostMapping("/create")
//  public AjaxResponse createEthWallet(@RequestParam("account") String account) {
//    if (StringUtil.isEmpty(account)) {
//      logger.error("account is null!");
//      return writeObj(ErrorMsgEnum.PARAM_ERROR);
//    }
//    String address = ethTokenWalletService.createEthTokenWallet(account, Constant.zgTokenName);
//    if (address == null) {
//      return writeObj(ErrorMsgEnum.ETH_ADDRESS_FAIL);
//    }
//    return writeObj(address);
//  }
//
//  @ApiOperation(value = "根据账号和TOKEN币种名称查询地址")
//  @ApiImplicitParams({ 
//    @ApiImplicitParam(name = "account", value = "账号", dataType = "String", required = true, paramType = "query")
//  })
//  @GetMapping("/getAddress")
//  public AjaxResponse getAddressByAccountAndCurrency(@RequestParam("account") String account) {
//    if (StringUtil.isEmpty(account)) {
//      logger.error("account is null!");
//      return writeObj(ErrorMsgEnum.PARAM_ERROR);
//    }
//    String address = ethTokenWalletService.getAddressByAccountAndCurrency(account, Constant.zgTokenName);
//    if ("".equals(address) || null == address) {
//      return writeObj(ErrorMsgEnum.ETH_ADDRESS_NOT_EXITS);
//    }
//    return writeObj(address);
//  }
//
//  @ApiOperation(value = "查询以太坊代币OMG热钱包账户余额")
//  @GetMapping("/getOmgPrimaryAccountBalance")
//  public AjaxResponse getEthPrimaryAccountBalance() {
//    return writeObj(ethTokenWalletService.getTOKENHotWalletBalance(Constant.zgTokenName));
//  }
//
//  @ApiOperation(value = "根据系统内用户的TOKEN地址查询钱包账户余额")
//  @GetMapping("/getEthTokenAccountBalance/{address}")
//  public AjaxResponse getEthTokenAccountBalance(String address) {
//    return writeObj(ethTokenWalletService.getTOKENBalance(address, Constant.zgTokenName));
//  }
//  
//  @ApiOperation(value = "提币", notes = "提币,参数：userName 用户名，password 密码，money 金额，address 地址")
//  @PostMapping("/sendMoney")
//  @ApiImplicitParams({ @ApiImplicitParam(name = "withdrawLog", value = "提币参数", required = true, dataType = "WithdrawLog") })
//  public AjaxResponse sendMoney(HttpServletRequest request, @RequestBody WithdrawLog withdrawLog) {
//    logger.info("orderNo="+withdrawLog.getOrderNo()+",currency="+withdrawLog.getCurrency());
//    if (withdrawLog == null || StringUtils.isBlank(withdrawLog.getAccount())
//        || StringUtils.isBlank(withdrawLog.getToAddress())) {
//      logger.info("提币传参错误，请检查account、 toAddress、currency等各个提币参数项！");
//      return writeObj(ErrorMsgEnum.PARAM_ERROR);
//    }
//    withdrawLog.setCurrency(Constant.zgTokenName);
//    if (!ethTokenWalletService.checkExistAddress(withdrawLog.getAccount(), withdrawLog.getCurrency())) {
//      logger.info("根据account："+withdrawLog.getAccount()+"和currency："+withdrawLog.getCurrency()+"查询UserCoin表不存在对应数据！");
//      return writeObj(ErrorMsgEnum.PARAM_ERROR);
//    }
//    String address = ethTokenWalletService.getAddressByAccountAndCurrency(withdrawLog.getAccount(), withdrawLog.getCurrency());
//    logger.info("根据account："+withdrawLog.getAccount()+"和currency："+withdrawLog.getCurrency()+"查到对应地址address="+address);
//    if (address.equals(withdrawLog.getToAddress())) {// 转入转出地址是同一个
//      logger.info("不能给自己转账，转入转出地址是同一个地址："+address);
//      return writeObj(ErrorMsgEnum.PARAM_ERROR);
//    }
//    UserCoin userCoin = ethTokenWalletService.getUserCoinByAddress(withdrawLog.getToAddress(), withdrawLog.getCurrency());    
//    if (userCoin != null) {
//      logger.info("根据toAddress："+withdrawLog.getToAddress()+"查询UserCoin表存在对应数据，提币失败，不允许系统内部进行转账！");
//      return writeObj(ErrorMsgEnum.PARAM_ERROR);
//    }
//    if (!ethTokenWalletService.isValidAddress(address)) {
//      logger.info("该address："+address+"不是以太坊TOKEN地址！");
//      return writeObj(ErrorMsgEnum.ETH_ADDRESS_ERROR);
//    }
//
//    String uplimit = "";
//    String lowlimit = "";
//    List<Map<String, String>> codeList = sysGencodeService.findByGroupCode("WITHDRAW_LIMIT");
//    for (Map<String, String> mapObj : codeList) {
//      if ("ETH_LIMIT".equals(String.valueOf(mapObj.get("code_name")))) {
//        uplimit = mapObj.get("uplimit").toString();
//        lowlimit = mapObj.get("lowlimit").toString();
//      }
//    }
//    if (withdrawLog.getMoney().compareTo(new BigDecimal(lowlimit)) < 0) {
//      logger.info("交易金额："+withdrawLog.getMoney()+"低于下限值："+lowlimit+"，不能提币！");
//      return writeObj(ErrorMsgEnum.ETH_LOWER_THANLOWLIMIT);
//    }
//
//    if (!StringUtils.isEmpty(uplimit)
//        && withdrawLog.getMoney().compareTo(new BigDecimal(uplimit)) > 0) {
//      logger.info("交易金额："+withdrawLog.getMoney()+"超过上限值："+uplimit+"，不能提币！");
//      return writeObj(ErrorMsgEnum.ETH_UPER_THANUPLIMIT);
//    }
//    String ethFee = "";// 手续费（包含矿工费）
//    List<Map<String, String>> ethFeeCodeList = sysGencodeService
//        .findByGroupCode("WITHDRAW_SERVICE_CHARGE");
//    for (Map<String, String> mapObj : ethFeeCodeList) {
//      if ("ETH_FEE".equals(String.valueOf(mapObj.get("code_name")))) {
//        ethFee = mapObj.get("code_value").toString();
//      }
//    }
//    String hash = null;
//    try {
//      withdrawLog.setFree(new BigDecimal(ethFee));
//      Map<String, Object> map = ethTokenWalletService.sendTokenMoney(withdrawLog);
//      if ("success".equals(map.get("code"))) {
//        hash = (String) map.get("data");
//      }
//      if (hash == null) {
//        return writeObj(ErrorMsgEnum.ETH_WITHDRAW_FAIL);
//      }
//    } catch (ExecutionException | InterruptedException e) {
//      return writeObj(ErrorMsgEnum.ETH_WITHDRAW_EXCEPTION);
//    }
//    return writeObj(hash);
//  }
//
//}
