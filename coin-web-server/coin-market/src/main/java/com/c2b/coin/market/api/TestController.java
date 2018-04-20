package com.c2b.coin.market.api;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c2b.coin.market.utils.HttpsUtils;

/**
 *
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-11-01 10:55:56
 */
@RestController
@RequestMapping("/nologin/test")
public class TestController {
	@RequestMapping("/sobot")
	public void list(String url, HttpServletRequest request, HttpServletResponse response) {
		try {
			HttpsUtils httpsUtils = new HttpsUtils(1000, 1000, 1000);
			ServletOutputStream outputStream = response.getOutputStream();
			outputStream.write(httpsUtils.agency("https://www.sobot.com" + url, request.getInputStream()));
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
