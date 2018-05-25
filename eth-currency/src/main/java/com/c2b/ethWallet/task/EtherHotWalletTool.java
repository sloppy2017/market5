package com.c2b.ethWallet.task;

import java.math.BigInteger;

import com.c2b.coin.cache.redis.RedisUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
* @ClassName: EtherHotWalletTool
* @Description: TODO(以太坊钱包工具类，主要用于从redis存取数据)
* @author 焦博韬
* @date 2017年10月12日 上午10:50:24
*
 */
@Service
public class EtherHotWalletTool {

  private Logger logger = Logger.getLogger(EtherHotWalletTool.class);

	@Autowired
  RedisUtil redisUtil;

	private static String REDIS_LAST_BLOCK_KEY = "ETH_LAST_BLOCK_NUMBER";
	private static String REDIS_ETH_MEMBER_ADDRESS = "ETH_MEMBER_ADDRESS";
	private static String REDIS_ETH_TOKEN_MEMBER_ADDRESS = "ETH_TOKEN_MEMBER_ADDRESS";
	public static String COIN_TYPE_ETHEREUM = "ETH";
	private static String REDIS_ETH_TOKEN_LAST_BLOCK_KEY = "ETH_TOKEN_LAST_BLOCK_KEY";

//	public Set<Object> listGatherTxHash(){
//		Set<Object> set  =  redisTemplate.opsForHash().keys(EtherHotWalletTool.REDIS_GATHERING_ORDER);
//		return set;
//	}


//	public void delGatherTxHashByOrderId(String orderId){
//		Jedis jedis = null;
//		try {
//			jedis = jedisClient.get();
//			jedis.hdel(InitConst.REDIS_GATHERING_ORDER,orderId);
//
//		} catch (Exception e) {
//			log.error("exception e:"+e.getMessage());
//			e.printStackTrace();
//		}finally{
//			if(jedis!=null)jedisClient.release(jedis);
//		}
//	}
//
//	public String getGatherTxHashByOrderId(String orderId){
//		Jedis jedis = null;
//		try {
//			jedis = jedisClient.get();
//			return jedis.hget(InitConst.REDIS_GATHERING_ORDER,orderId);
//
//		} catch (Exception e) {
//			log.error("exception e:"+e.getMessage());
//			e.printStackTrace();
//		}finally{
//			if(jedis!=null)jedisClient.release(jedis);
//		}
//		return null;
//	}
//
//	public void setGatherTxHashByOrderId(String orderId,String txhash){
//		Jedis jedis = null;
//		try {
//			jedis = jedisClient.get();
//			jedis.hset(InitConst.REDIS_GATHERING_ORDER,orderId,txhash);
//		} catch (Exception e) {
//			log.error("exception e:"+e.getMessage());
//			e.printStackTrace();
//		}finally{
//			if(jedis!=null)jedisClient.release(jedis);
//		}
//	}
//
//
	public boolean isExistForDepositAddr(String addr){
	  return redisUtil.isMember(EtherHotWalletTool.REDIS_ETH_MEMBER_ADDRESS,addr);
    }

	public boolean isExistForTOKENDepositAddr(String tokenName, String addr){
      return redisUtil.isMember(EtherHotWalletTool.REDIS_ETH_TOKEN_MEMBER_ADDRESS+"_"+tokenName,addr);
    }
//
//
	public void saveLastBlock(BigInteger blkNum){
		logger.debug("save last Block :"+blkNum.intValue());
		try{
		  redisUtil.set(EtherHotWalletTool.REDIS_LAST_BLOCK_KEY, blkNum.toString());
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	public BigInteger getLastBlock(){
		logger.debug("go to getLastBlock function ");
		BigInteger encodeBlkNum = null;
		try{
			encodeBlkNum = new BigInteger(redisUtil.get(EtherHotWalletTool.REDIS_LAST_BLOCK_KEY));
			logger.debug("from redis lasted block  is ============================"+encodeBlkNum);
		}catch (Exception e){
			e.printStackTrace();
		}

		if(encodeBlkNum!=null) {
				return encodeBlkNum;
		}else{
			return BigInteger.ZERO;
		}
	}

	public void saveTOKENLastBlock(BigInteger blkNum){
      try{
        redisUtil.set(EtherHotWalletTool.REDIS_ETH_TOKEN_LAST_BLOCK_KEY, blkNum.toString());
      }catch(Exception e){
          e.printStackTrace();
      }
  }

	public BigInteger getTOKENLastBlock(){
      logger.debug("go to getTOKENLastBlock function ");
      BigInteger encodeBlkNum = null;
      try{
          encodeBlkNum = new BigInteger(redisUtil.get(EtherHotWalletTool.REDIS_ETH_TOKEN_LAST_BLOCK_KEY));
          logger.debug("from redis lasted block  is ============================"+encodeBlkNum);
      }catch (Exception e){
          e.printStackTrace();
      }

      if(encodeBlkNum!=null) {
          return encodeBlkNum;
      }else{
          return BigInteger.ZERO;
      }
  }

//
//
//	public void createEtherMicroHotWallet() throws APIException{
//
//			if(this.getMicroHotwallet()!=null)
//				throw new RuntimeException("Ether microhotwallet exist in db");
//			ElecoinInfo info = new ElecoinInfo();
//			EtherWallet _wallet = EtherWalletUtil.createWallet();
//
//	    	if(_wallet==null)
//	    		throw new APIException(ErrorCode.CREATE_WALLET_ERROR, "create ether account failed,pls retry later!");
//
//	    	info = new ElecoinInfo();
//	    	info.setAddress(_wallet.getAddress());
//	    	info.setCoinType(ElecoinInfo.CoinType.ETH.value());
//	    	info.setInitVector(_wallet.getIv());
//	    	info.setSalt(_wallet.getSalt());
//	    	info.setSeed(_wallet.getCipherText());
//	    	info.setStatus(ElecoinInfo.Status.VALID.value());
//	    	info.setType(ElecoinInfo.Type.MICROHOTWALLET.value());
//
//	    	info.setCreatedAt(System.currentTimeMillis());
//	        info.setUpdatedAt(System.currentTimeMillis());
//	        info.setUserId(userIdHotwallet);
//
//	        info = remoteElecoinService.saveElecoinInfo(info);
//
//	}
//
//
//	public void createEtherHotWallet() throws APIException{
//
//		int hotwalletType = coinConfig.getHotwalletType();
//
//			if(this.getHotwallet()!=null)
//				throw new RuntimeException("Ether hotwallet exist in db, hotwalletType:"+hotwalletType);
//			ElecoinInfo info = new ElecoinInfo();
//			EtherWallet _wallet = EtherWalletUtil.createWallet();
//
//	    	if(_wallet==null)
//	    		throw new APIException(ErrorCode.CREATE_WALLET_ERROR, "create ether account failed,pls retry later!");
//
//	    	info = new ElecoinInfo();
//	    	info.setAddress(_wallet.getAddress());
//	    	info.setCoinType(hotwalletType);
//	    	info.setInitVector(_wallet.getIv());
//	    	info.setSalt(_wallet.getSalt());
//	    	info.setSeed(_wallet.getCipherText());
//	    	info.setStatus(ElecoinInfo.Status.VALID.value());
//	    	info.setType(ElecoinInfo.Type.HOTWALLET.value());
//
//	    	info.setCreatedAt(System.currentTimeMillis());
//	        info.setUpdatedAt(System.currentTimeMillis());
//	        info.setUserId(userIdHotwallet);
//
//	        info = remoteElecoinService.saveElecoinInfo(info);
//
//	}
//
//
//
//
//	public ElecoinInfo getHotwallet(){
//		int hotwalletType = coinConfig.getHotwalletType();
//		ElecoinInfo gatherHotwallet = null;
//
//		gatherHotwallet = this.remoteElecoinService.getElecoinInfoByUid(userIdHotwallet, hotwalletType,
//    			ElecoinInfo.Type.HOTWALLET.value(), ElecoinInfo.Status.VALID.value());
//
//		return gatherHotwallet;
//
//
//	}
//
//	public static void main(String[] args) throws APIException {
//		final ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-eth.xml");
//		EtherHotWalletTool tool = (EtherHotWalletTool)context.getBean("etherHotWalletTool");
//		tool.coinConfig.setHotwalletType(ElecoinInfo.CoinType.CDT.value());
//
//		tool.createEtherHotWallet();
////		tool.createEtherMicroHotWallet();
//
////		System.out.println(tool.getGatherTxHashByOrderId("xxx"));
////		tool.setGatherTxHashByOrderId("xxx", "txhash");
////		System.out.println(tool.getGatherTxHashByOrderId("xxx"));
//	}

}
