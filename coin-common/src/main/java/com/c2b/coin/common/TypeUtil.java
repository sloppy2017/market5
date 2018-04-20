package com.c2b.coin.common;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * @author <a href="mailto:guo_xp@163.com">guoxinpeng</a>
 * @version 1.0 2016/11/21 13:16
 * @projectname repayment
 * @packname com.yingu.service.repayment.util
 */
public class TypeUtil {
	/**
	 * 判断字符串是否是数字
	 */
	public static boolean isNumber(String value) {
		return isInteger(value) || isDouble(value);
	}

	/**
	 * 判断字符串是否是整数
	 */
	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 判断字符串是否是浮点数
	 */
	@SuppressWarnings("AliControlFlowStatementWithoutBraces")
  public static boolean isDouble(String value) {
		try {
			Double.parseDouble(value);
			if (value.contains(".")) {
        return true;
      }
			return false;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 判断是否是json结构
	 */
	public static boolean isJson(String value) {
		try {
			JSONObject.parseObject(value);
		} catch (JSONException e) {
			return false;
		}
		return true;
	}

	/**
	 * 判断是否是xml结构
	 */
	public static boolean isXML(String value) {
//    try {
//      if (value.contains("<html>") && value.contains("<title>"))
//        return false;
//      DocumentHelper.parseText(value);
//    } catch (DocumentException e) {
//      return false;
//    }
		return true;
	}
}
