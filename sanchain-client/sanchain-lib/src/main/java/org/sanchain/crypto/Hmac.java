package org.sanchain.crypto;


import org.ripple.bouncycastle.util.encoders.Hex;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by A
 * since 14/11/26.
 */
public class Hmac {
    public static String getSalt(){
        byte[] b = new byte[128];
        new Random().nextBytes(b);
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        digest.update(b);
        byte[] res = digest.digest();
//        System.out.println(new String(Hex.encode(res)));
        return new String(Hex.encode(res));
    }

    public static String getSaltPassword(String salt, String password){
        try {
            Mac mac = Mac.getInstance("HMACSHA256");
            //get the bytes of the hmac key and data string
            byte[] secretByte = salt.getBytes("utf-8");
            byte[] dataBytes = password.getBytes("utf-8");
            SecretKey secret = new SecretKeySpec(secretByte, "SHA256");
            mac.init(secret);
            mac.update(dataBytes);
            byte[] doFinal = mac.doFinal();
            byte[] hexB = new Hex().encode(doFinal);
            String checksum = new String(hexB);
            return checksum;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 576d709011d65d71fa83de1d631aeaac1937855e258cd1e5182933c68deb8fee
     * @param args
     */
    public static void main(String[] args){
       String content = Hmac.getSaltPassword("a688ae5d70a34a6503e6a0ee6bba9ad2bfe8cbf4cae995a2f7b66ff8766cf4fe,", "jinru1977@1976@");
        System.out.println(content);
    }
}
