package com.c2b.ethWallet.util;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;

import com.c2b.coin.common.DateUtil;
import com.c2b.ethWallet.entity.UserCoin;
import com.c2b.ethWallet.util.EtherWalletUtil.EtherWallet;

/**
 * AES加解密工具
 * 
 * @author Frank
 *
 */
public class AES {
	
	public final static String ALGORITHM_AEPP = "AES/ECB/PKCS5Padding";
	
	public final static String algorithm = "AES/ECB/PKCS5Padding";
	
	public final static byte[] password = "1qwxeASDRGJ45DFS".getBytes();
	/**
	 * AES加密
	 * 
	 * @param content
	 *            内容
	 * @return 加密后数据
	 */
	public static String encrypt(byte[] content) {
		if (content == null || password == null)
			return null;
		try {
			Cipher cipher = null;
			if (algorithm.endsWith("PKCS7Padding")) {
				cipher = Cipher.getInstance(algorithm, "BC");
			} else {
				cipher = Cipher.getInstance(algorithm);
			}
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(password, "AES"));
			return parseByte2HexStr(cipher.doFinal(content));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * AES解密
	 * 
	 * @param content
	 *            加密内容
	 * @return 解密后数据
	 */
	public static String decrypt(String content) {
		if (content == null || password == null)
			return null;
		try {
			Cipher cipher = null;
			if (algorithm.endsWith("PKCS7Padding")) {
				cipher = Cipher.getInstance(algorithm, "BC");
			} else {
				cipher = Cipher.getInstance(algorithm);
			}
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(password, "AES"));
			byte[] bytes = cipher.doFinal(parseHexStr2Byte(content));
			return new String(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将二进制转换成16进制
	 * @param buf
	 * @return
	 */
	public static String parseByte2HexStr(byte buf[]) {  
        StringBuffer sb = new StringBuffer();  
        for (int i = 0; i < buf.length; i++) {  
                String hex = Integer.toHexString(buf[i] & 0xFF);  
                if (hex.length() == 1) {  
                        hex = '0' + hex;  
                }  
                sb.append(hex.toUpperCase());  
        }  
        return sb.toString();  
    }  
    
	/**
	 * 将16进制转换为二进制
	 * @param hexStr
	 * @return
	 */
    public static byte[] parseHexStr2Byte(String hexStr) {  
        if (hexStr.length() < 1)  
                return null;  
        byte[] result = new byte[hexStr.length()/2];  
        for (int i = 0;i< hexStr.length()/2; i++) {  
                int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);  
                int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);  
                result[i] = (byte) (high * 16 + low);  
        }  
        return result;  
    }
    
    public static void main(String[] args) {
////    		String privateKey = "c5dcde9416239c697c131342aa66ac85a312755ac20f21e7db3e5e6f6ab9c90b";
////    		
////    		EtherWalletUtil.decryptPrivateKey(salt, iv, cipherKey)
//			String pk = encrypt("10148807834834672580300528225791268477424646694962654520693361135116595652119".getBytes());
//			System.out.println(pk);
//			String prk =decrypt("F6369E28EB4F82EBDB6132F0CBFF571B2690F8A40B59DCE1C84855D5231CCDC8A7743371CB48B498635C22E8B35FC1C5278E3D967E80BE1D7A960CE7886659F8ED11D15AAB6DB81B95380A45BC62515C");
//			System.out.println(prk);
//		
//    	
    	EtherWallet walletOne = EtherWalletUtil.createWallet();
//
		String privateKey;
		try {
			privateKey = EtherWalletUtil.decryptPrivateKey(walletOne.getSalt(),
					walletOne.getIv(), walletOne.getCipherText());
			String onlyPrivateKey = walletOne.getPrivateKeys();
			System.out.println("address:"+walletOne.getAddress());
			System.out.println("privateKey:"+onlyPrivateKey);
			System.out.println("dePrivateKey:"+privateKey);
			System.out.println(encrypt(onlyPrivateKey.getBytes()));
//			System.out.println(WalletUtils.isValidPrivateKey(privateKey));
//
//			Credentials credential = EtherWalletUtil.getCredentials(privateKey);
//
//			System.out.println(walletOne.getAddress()
//					.equals(Numeric.cleanHexPrefix(credential.getAddress())));
		} catch (DecoderException | CipherException e) {
			e.printStackTrace();
		}
//
//		
	}

}
