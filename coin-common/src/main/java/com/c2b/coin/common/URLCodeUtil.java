package com.c2b.coin.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class URLCodeUtil {

    private URLCodeUtil() {
    }

    public static String encode(String content) {
        try {
            content = URLEncoder.encode(content, Constants.ENCODING_UTF8);
            content = content.replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException ignored) {
        }
        return content;
    }

    public static String decode(String content) {
        try {
            content = URLDecoder.decode(content, Constants.ENCODING_UTF8);
        } catch (UnsupportedEncodingException ignored) {
        }
        return content;
    }

}
