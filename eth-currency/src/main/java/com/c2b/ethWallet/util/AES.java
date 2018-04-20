package com.c2b.ethWallet.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

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

}
