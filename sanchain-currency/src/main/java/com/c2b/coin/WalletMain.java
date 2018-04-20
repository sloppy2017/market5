package com.c2b.coin;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import org.sanchain.client.api.exception.APIException;
import org.sanchain.client.api.util.Utils;
import org.sanchain.core.Amount;
import org.sanchain.core.Wallet;
import org.sanchain.core.types.known.tx.signed.SignedTransaction;

import com.c2b.coin.sanchain.util.AES;


public class WalletMain {

	
	public static void main(String[] args) {
		
//		try {
//			transfer();
//		} catch (APIException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	
	private static void  encrypt(String privateKey) {
		//加密后的私钥
		String privateKeyEN;
		try {
			privateKeyEN = AES.encrypt(privateKey.getBytes("UTF-8"));
			System.out.println(privateKeyEN);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void transfer() throws APIException {
		String address = "sJAAc5u9VEdhBRaMoGbLFvfuK16UEoQzN3";
		String seed= "CF1EC4E9D0C2ADE8C44590195FDBA1D84BF68C27872992E83C71E53CB42410DD";
		
		
		BigDecimal b = new BigDecimal("7.00999782").stripTrailingZeros().multiply(new BigDecimal("1000000")).stripTrailingZeros().setScale(0, BigDecimal.ROUND_DOWN);
		
		
		SignedTransaction sign = null;
		Integer lastLedgerIndex = Utils.ledgerClosed() + 3;
		sign = Utils.payment(
				AES.decrypt(seed),
				"sJKWFFV7vZKCpghe8EXn3QGndKTeWGjNF1",
				Amount.fromString(String.valueOf(b.stripTrailingZeros())),
				Utils.getSequence(address),
				true,
				null, null, null, lastLedgerIndex
				);  //just sign local

		System.out.println(sign.hash.toHex());
		String res = Utils.sendTx(sign.tx_blob);  //broadcast
		System.out.println("res="+res);  //broadcast return
	}

	//创建钱包
	public static void createWallet() throws UnsupportedEncodingException {
		Wallet wallet = new Wallet();
		String address=wallet.account().address;
		String seed=wallet.seed();
		String privateKey = AES.encrypt(seed.getBytes("UTF-8"));
		System.out.println(address);//地址
		System.out.println(privateKey);//私钥
	}
	
	public static void getLegerHeight() {
		int ledgerCurrentIndex = Utils.ledgerClosed();
		System.out.println(ledgerCurrentIndex);
	}
}
