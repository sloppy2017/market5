package com.c2b.util;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;

import com.lambdaworks.crypto.SCrypt;
public class EtherWalletUtil {
	 
	 private static SecureRandom SECURE_RANDOM = new SecureRandom();
	 
	 //The password hard code.
	 
	 private static int n =4096, p = 6;
	 
	 @Data
	 @EqualsAndHashCode
	 @ToString
	 public static class EtherWallet{
	  String address;
	  byte[] privateKey;
	  String cipherText;
	  String salt;
	  String iv;
	  
	  String derivedKey;
	  String encryptKey;
	  
	  String mac;
	  
	  String publicKey;
	  String privateKeys;
	 }
	 
	 public static Credentials getCredentials(String priKeyArg){
	  return Credentials.create(priKeyArg);
	  
	 }
	 
	 public static String decryptPrivateKey(String salt,String iv,String cipherKey) 
			 throws DecoderException, CipherException{
	  
	  byte[] saltBytes = Hex.decodeHex(salt.toCharArray());
	  byte[] ivBytes = Hex.decodeHex(iv.toCharArray());
	  byte[] cipherKeyBytes = Hex.decodeHex(cipherKey.toCharArray());
	  
	  byte[] derivedKey = generateDerivedScryptKey(
	    saltBytes, saltBytes, n, 8, p, 32);
	  
	  byte[] encryptKey1 = Arrays.copyOfRange(derivedKey, 0, 16);
	  byte[] privateKey2 = performCipherOperation(2,ivBytes , encryptKey1,
	    cipherKeyBytes);
	  
	  return Hex.encodeHexString(privateKey2);
	 }
	 
	 public static EtherWallet createWallet(){
	  EtherWallet wallet = new EtherWallet();
	  try {
	   ECKeyPair ecKeyPair = Keys.createEcKeyPair();
	   
	   byte[] salt = generateRandomBytes(32);
	   byte[] iv = generateRandomBytes(16);
	   
	   byte[] password = salt;
	   
	   byte[] derivedKey = generateDerivedScryptKey(
	     password, salt, n, 8, p, 32);
	   byte[] encryptKey = Arrays.copyOfRange(derivedKey, 0, 16);
	   
	   byte[] privateKeyBytes = ToBytesPadded(ecKeyPair.getPrivateKey().toByteArray(), 32);
	   byte[] cipherText = performCipherOperation(1, iv, encryptKey,
	     privateKeyBytes);
	   byte[] mac = generateMac(derivedKey, cipherText);
	   
	   wallet.setAddress(Keys.getAddress(ecKeyPair));
	   wallet.setIv(Hex.encodeHexString(iv));
	   wallet.setPrivateKey(ecKeyPair.getPrivateKey().toByteArray());
	   wallet.setSalt(Hex.encodeHexString(salt));
	   wallet.setPrivateKeys(ecKeyPair.getPrivateKey().toString());
	   wallet.setPublicKey(ecKeyPair.getPublicKey().toString());
	   wallet.setDerivedKey(Hex.encodeHexString(derivedKey));
	   wallet.setEncryptKey(Hex.encodeHexString(encryptKey));
	   wallet.setCipherText(Hex.encodeHexString(cipherText));
	   wallet.setMac(Hex.encodeHexString(mac));
	   return wallet;
	  } catch (InvalidAlgorithmParameterException
	    | NoSuchAlgorithmException | NoSuchProviderException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	  } catch (CipherException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	  }
	  return null;
	 }
	 
	 private static byte[] generateDerivedScryptKey(byte[] password,
	   byte[] salt, int n, int r, int p, int dkLen) throws CipherException {
	  try {
	   return SCrypt.scrypt(password, salt, n, r, p, dkLen);
	  } catch (GeneralSecurityException arg6) {
	   throw new CipherException(arg6);
	  }
	 }
	 
	  static byte[] generateRandomBytes(int size) {
	  byte[] bytes = new byte[size];
	  SECURE_RANDOM.nextBytes(bytes);
	  return bytes;
	 }
	  
	  private static byte[] performCipherOperation(int mode, byte[] iv,
	    byte[] encryptKey, byte[] text) throws CipherException {
	   try {
	    IvParameterSpec e = new IvParameterSpec(iv);
	    Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
	    SecretKeySpec secretKeySpec = new SecretKeySpec(encryptKey, "AES");
	    cipher.init(mode, secretKeySpec, e);
	    return cipher.doFinal(text);
	   } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
	     | InvalidKeyException | BadPaddingException
	     | IllegalBlockSizeException | NoSuchPaddingException arg6) {
	    throw new CipherException("Error performing cipher operation", arg6);
	   }
	 }
	  
	  private static byte[] generateMac(byte[] derivedKey, byte[] cipherText) {
	   byte[] result = new byte[16 + cipherText.length];
	   System.arraycopy(derivedKey, 16, result, 0, 16);
	   System.arraycopy(cipherText, 0, result, 16, cipherText.length);
	   return Hash.sha3(result);
	  }
	  
	  
	  public static byte[] ToBytesPadded(byte[] value, int length) {
	   byte[] result = new byte[length];
	   byte[] bytes = value;
	   int bytesLength;
	   byte srcOffset;
	   if (bytes[0] == 0) {
	    bytesLength = bytes.length - 1;
	    srcOffset = 1;
	   } else {
	    bytesLength = bytes.length;
	    srcOffset = 0;
	   }

	   if (bytesLength > length) {
	    throw new RuntimeException(
	      "Input is too large to put in byte array of size " + length);
	   } else {
	    int destOffset = length - bytesLength;
	    System.arraycopy(bytes, srcOffset, result, destOffset, bytesLength);
	    return result;
	   }
	 }
	  
	  public static  boolean isValidAddress(String addressHex){
	   return WalletUtils.isValidAddress(addressHex);
	  }
	  
	  public static void main(String[] args) throws Exception {

	   EtherWallet walletOne = EtherWalletUtil.createWallet();
	   
	   String privateKey = EtherWalletUtil.decryptPrivateKey(walletOne.getSalt(),
			   walletOne.getIv(), walletOne.getCipherText());
	   
	   System.out.println(WalletUtils.isValidPrivateKey(privateKey));

	    Credentials credential = EtherWalletUtil.getCredentials(privateKey);

	    System.out.println(walletOne.getAddress()
	    		.equals(Numeric.cleanHexPrefix(credential.getAddress())));
	}
}
