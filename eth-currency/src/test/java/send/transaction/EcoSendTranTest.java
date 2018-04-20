package send.transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.tx.Transfer;
import org.web3j.utils.Numeric;
import org.web3j.utils.Convert.Unit;

import com.c2b.ethWallet.EthWalletApplication;
import com.c2b.ethWallet.client.Web3JClient;
import com.c2b.ethWallet.entity.IcoGatherRecord;
import com.c2b.ethWallet.entity.WithdrawLog;
import com.c2b.ethWallet.mapper.WithdrawLogMapper;
import com.c2b.ethWallet.service.EthWalletService;
import com.c2b.ethWallet.service.IcoGatherRecordService;
import com.c2b.ethWallet.service.WithDrawService;
import com.c2b.ethWallet.task.ConfirmWithdrawTxThread;
import com.c2b.ethWallet.util.AES;
import com.c2b.ethWallet.util.Constant;
import com.c2b.ethWallet.util.EtherWalletUtil;
import com.c2b.ethWallet.util.HotWalletTool;
import com.c2b.ethWallet.util.EtherWalletUtil.EtherWallet;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EthWalletApplication.class)
@WebAppConfiguration
public class EcoSendTranTest {

  Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private EthWalletService ethWalletService;

  @Autowired
  private HotWalletTool hotWalletTool;
  private static Web3j web3j = Web3JClient.getClient();
  
  @Autowired
  private IcoGatherRecordService gatherRecordService;
  
  @Autowired
  private WithdrawLogMapper withdrawLogMapper;
  
  @Autowired
  private WithDrawService withDrawService;
  
  @Autowired
  private ConfirmWithdrawTxThread confirmWithdrawTxThread;

  /*
   * @Autowired private ColdWalletTool coldWalletTool;
   * 
   * @Autowired private UsersDetailsService usersDetailsService;
   * 
   * @Autowired private GatherTask gatherTask;
   */
  /*
   * @Test public void createHotWallet (){ try { String address =
   * hotWalletTool.createEtherHotWallet("111111");
   * System.out.println("address="+address); } catch (Exception e) { // TODO
   * Auto-generated catch block e.printStackTrace(); } }
   */

  /*
   * @Test public void createColdWallet(){ try { String coldAddress =
   * coldWalletTool.createEtherColdWallet();
   * System.out.println("coldAddress="+coldAddress); } catch (Exception e) { //
   * TODO: handle exception } }
   */

  /*
   * @Test public void createUserWallet(){ String address =
   * ethWalletService.createEthWallet("zd");
   * System.out.println("userAddress = "+address); }
   */

  /*
   * @Test public void sendToOuterUser() throws DecoderException, Exception{
   * String fromAddress = "0x81f63b559e463e29a68b04c92ae43980a3877c57";
   * BigDecimal amount =new BigDecimal("10000000000000000000"); String toAddress
   * = "0x6EE9f51a094a70861134a65E3f00e6Ae88Bd3eA3";
   * System.out.println("send before="
   * +ethWalletService.getBalanceEther(toAddress));
   * //ethWalletService.sendToOuterUser(toAddress,
   * amount);用来热钱包转账给用户，比如新建用户模拟充值/提币
   * //ethWalletService.sendTransactionFromUser(fromAddress, userId, toAddress ,
   * amount.toString() );
   * //ethWalletService.sendTransactionFromWallet(fromAddress, toAddress,
   * "111111", amount.toString()); String txhash =
   * ethWalletService.sendTransactionFromWallet(fromAddress, toAddress,
   * "111111", amount.toString()); System.out.println("txhash="+txhash);
   * BigDecimal balance = ethWalletService.getBalanceEther(toAddress);
   * System.out
   * .println("toAddress:"+toAddress+",send after balance:"+balance+Unit.ETHER);
   * }
   */

  /*
   * @Test public void getBalance(){
   * System.out.println("公链热钱包余额="+ethWalletService
   * .getBalanceEther("0xac20abf8ee8345afc0f2f5f652642d4a70133995")); }
   */

  /*
   * @Test public void aesPrivateKey(){ String privateKey =
   * "17922139767917986780876012306255480076121797034224534287656495949669525415510"
   * ; System.out.println("加密前privateKey="+privateKey); String enPrivateKey=
   * AES.encrypt(privateKey.getBytes());
   * System.out.println("加密后privateKey="+enPrivateKey); String dePrivateKey =
   * AES.decrypt(enPrivateKey);
   * System.out.println("解密后privatekey="+dePrivateKey); }
   */

  /*
   * @Test public void getBlockNum(){ BigInteger bloclNumber = new
   * BigInteger("0"); try { bloclNumber =
   * web3j.ethBlockNumber().sendAsync().get().getBlockNumber(); } catch
   * (InterruptedException | ExecutionException e) { // TODO Auto-generated
   * catch block e.printStackTrace(); }
   * System.out.println("bloclNumber="+bloclNumber); }
   */
 /* @Test
  public void sendMoneyTest(){
    String txHash = "0x5cd8911061908af4ef76af3828d35d6c8958b5f2053017f3609c1b9ce1f66299";
    WithdrawLog w = withdrawLogMapper.getWithdrawLogByHash(txHash);
    System.out.println(w.getToAddress());
  }*/
  
  /*@Test
  public void testConfirmCallBack(){
    String address="0xbef595d849ee0555db24464b1b281f87e2205eef";
    int currencyType = 2;
    String userName = "C1777400117";
    String hxId = "0x3660b6cd0fc0b95de2407bf3dc34b49cc5748c1d22bfea2d3df1076fee670d5d";
    BigDecimal amount = new BigDecimal("0.8888880");
    String orderNo = "936518180807901184";
    WithdrawLog withdrawLog = new WithdrawLog();
    withdrawLog.setAccount(userName);
    withdrawLog.setTxHash(hxId);
    withdrawLog.setMoney(amount);
    withdrawLog.setOrderNo(orderNo);
//    confirmWithdrawTxThread.confirmWithdrawTransaction(withdrawLog, new BigInteger("2312051"));
    String result = withDrawService.confirmCallback(address, currencyType, userName, hxId, amount, orderNo);
    System.out.println("result="+result);
    
    List<WithdrawLog> records = withdrawLogMapper.listETHSend();
    for(WithdrawLog withdrawLog:records){
      String orderNo = withdrawLog.getOrderNo();
      System.out.println("orderNo="+orderNo);
    }
  }*/
  
 /* @Test
  public void testCreateZGHotWallet(){
    EtherWallet etherTokenWallet = EtherWalletUtil.createWallet();
    String hotTokenAddress = etherTokenWallet.getAddress();
    String hotTokenPrivateKey = etherTokenWallet.getPrivateKeys().toString();
    String enHotTokenPrivateKey = AES.encrypt(hotTokenPrivateKey.getBytes());
    String hotTokenPublicKey = etherTokenWallet.getPublicKey();
    String deHotTokenPrivateKey = AES.decrypt(enHotTokenPrivateKey);
    System.out.println("hotTokenAddress:"+hotTokenAddress+",加密后hotTokenPrivateKey="+enHotTokenPrivateKey+",解密后私钥="+deHotTokenPrivateKey+",hotTokenPublicKey="+hotTokenPublicKey);
  }*/
  
  @Test
  public void testdeKey(){
    String key = "30594018940336724687262872831395791549114196370644652701223000667714461875664";
    String enKey = AES.encrypt(key.getBytes());
    System.out.println("enKey="+enKey);
    String deKey = AES.decrypt(enKey);
    System.out.println("deKey="+deKey);
  }
  /*@Test
  public void testKexue(){
    BigDecimal d = new BigDecimal("9E-10");
    String dStr = d.toPlainString();
    System.out.println("dStr="+dStr);
  }*/
  
  /*@Test
  public void testCreateZGTIBIWallet(){
    EtherWallet etherTokenWallet = EtherWalletUtil.createWallet();
    String tibiAddress = etherTokenWallet.getAddress();
    String tibiPrivateKey = etherTokenWallet.getPrivateKeys().toString();
    String enTibiPrivateKey = AES.encrypt(tibiPrivateKey.getBytes());
    String deHotTokenPrivateKey = AES.decrypt(enTibiPrivateKey);
    System.out.println("hotTokenAddress:"+tibiAddress+",加密后hotTokenPrivateKey="+enTibiPrivateKey+",解密后私钥="+deHotTokenPrivateKey);
  }*/
}
