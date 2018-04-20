package com.c2b.coin.user.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PwdUtil {

  public static String getEncryptPwd(String password) {
    BCryptPasswordEncoder util = new BCryptPasswordEncoder(8);
    String cryptPwd = util.encode(password);
    return cryptPwd;
  }

  public static boolean matchPwd(String originalPwd, String dbPwd) {
    BCryptPasswordEncoder util = new BCryptPasswordEncoder(8);
    boolean flag = util.matches(originalPwd, dbPwd);
    return flag;
  }

}
