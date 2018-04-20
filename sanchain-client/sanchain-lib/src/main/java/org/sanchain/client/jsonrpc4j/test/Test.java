package org.sanchain.client.jsonrpc4j.test;

import org.sanchain.client.jsonrpc4j.JsonRpcHttpClient;
import org.sanchain.client.jsonrpc4j.dto.BtcClientinfo;
import org.sanchain.client.jsonrpc4j.dto.BtcTransactionBlocks;

import java.math.BigDecimal;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

public class Test {

	/**
	 * @param args
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable {
		//分配btc地址
		getAddress();
		
		//获取转帐信息
		getBlockList("00000000000741911fd2b780f797bb63260c593aca429a6fa2bc4f8a7b0834c0");
		
		
		sendToaddress("1Nk18mXj4ze4vtQXJhCcpd5oDx7CZqe1o3",BigDecimal.valueOf(0.01));

        getAddress();
    }


    static final String rpcuser = "";
    static final String rpcpassword = "";
    static final String server = "";

    static final String passphrase = "";

	private static String getAddress() throws Throwable{
		System.out.println("--------getAddress");
		try {

			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(rpcuser, rpcpassword
							.toCharArray());
				}
			});
			JsonRpcHttpClient client = new JsonRpcHttpClient(new URL(server));
			
			if(getclientinfo().getUnlocked_until()!=null){
				System.out.println("invoke walletpassphrase");
				client.invoke("walletpassphrase", new Object[] {passphrase,100});
			}
			
			String newaddr = client.invoke("getnewaddress", new Object[] { "402882e643286d070143286ea8f00006&&224a6bb8-0abe-408d-bd61-526506a8b469"}, String.class);
			System.out.println("newaddr=" + newaddr);
			return newaddr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String getBlockList(String listsinceblock) throws Throwable{
		System.out.println("--------getBlockList");
		try {

			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(rpcuser, rpcpassword.toCharArray());
				}
			});
			JsonRpcHttpClient client = new JsonRpcHttpClient(new URL(server));
			
			BtcTransactionBlocks blocks = client.invoke("listsinceblock", new Object[] {listsinceblock}, BtcTransactionBlocks.class);
			
			System.out.println("getLastblock=" + blocks.getLastblock());
			System.out.println("Transactions().length=" + blocks.getTransactions().length);
			
			return blocks.getLastblock();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//未测试通过
	private static String sendToaddress(String address, BigDecimal money) throws Throwable{
        System.out.println("--------sendToaddress");
		try {

			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(rpcuser, rpcpassword.toCharArray());
				}
			});
			JsonRpcHttpClient client = new JsonRpcHttpClient(new URL(server));
			
            String passphrase = "";
            
            
			client.invoke("walletpassphrase", new Object[] {passphrase,2});
			
			String result = client.invoke("sendtoaddress", new Object[]{address,money}, String.class);
            System.out.println("--------sendToaddress: " + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
            System.out.println("--------sendToaddress: ERROR!");
		}
		return null;
	}
	
	private static BtcClientinfo getclientinfo() throws Throwable{
		System.out.println("--------getclientinfo");
		try {
			
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(rpcuser, rpcpassword.toCharArray());
				}
			});
			
			JsonRpcHttpClient client = new JsonRpcHttpClient(new URL(server));
			BtcClientinfo clientinfo = client.invoke("getinfo", new Object[] {}, BtcClientinfo.class);
			
			System.out.println(clientinfo.getBalance());			
			
			return clientinfo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
