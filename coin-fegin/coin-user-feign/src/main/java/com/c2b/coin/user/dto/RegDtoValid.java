package com.c2b.coin.user.dto;

import com.c2b.coin.common.Constants;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class RegDtoValid {
  public static boolean validRegParam(RegDto regDto) {
    if (StringUtils.isEmpty(regDto.getUsername())) {
      return false;
    } else if (!Pattern.matches(Constants.REGEX_EMAIL, regDto.getUsername()) && !Pattern.matches(Constants.REGEX_MOBILE, regDto.getUsername())) {
      return false;
    } else if (Pattern.matches(Constants.REGEX_EMAIL, regDto.getUsername())) {
      return validEmailReg(regDto);
    } else if (Pattern.matches(Constants.REGEX_MOBILE, regDto.getUsername())) {
      return validMobileReg(regDto);
    }
    return true;
  }

  private static boolean validEmailReg(RegDto regDto) {
    if (StringUtils.isEmpty(regDto.getPassword()) || !regDto.getPassword().equals(regDto.getConfirmPwd())) {
      return false;
    } else if (!Pattern.matches(Constants.REGEX_PASSWORD, regDto.getPassword())) {
      return false;
    }
    return true;
  }

  private static boolean validMobileReg(RegDto regDto) {
    if (StringUtils.isEmpty(regDto.getSmsCode())) {
      return false;
    } else if (!StringUtils.isEmpty(regDto.getPassword()) && !Pattern.matches(Constants.REGEX_PASSWORD, regDto.getPassword())) {
      return false;
    }
    return true;
  }
}
