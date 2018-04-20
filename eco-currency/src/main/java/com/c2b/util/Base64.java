package com.c2b.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64.Decoder;


public class Base64 {
	// 加密  
    public static String getBase64(String str) {  
        byte[] b = null;  
        String s = null;  
        try {  
            b = str.getBytes("utf-8");  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        if (b != null) {  
            s = java.util.Base64.getEncoder().encodeToString(b);  
        }  
        return s;  
    }  
  
    // 解密  
    public static String getFromBase64(String s) {  
        byte[] b = null;  
        String result = null;  
        if (s != null) {  
            Decoder decoder = java.util.Base64.getDecoder();  
            try {  
                b = decoder.decode(s);  
                result = new String(b, "utf-8");  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        return result;  
    }  
    
    public static void main(String[] args) throws Exception     
    {     
    	String jm = Base64.getBase64("http://aub.iteye.com/");
    	System.out.println(jm);
    	String mm = Base64.getFromBase64(jm);
    	System.out.println(mm);
    }  
}
