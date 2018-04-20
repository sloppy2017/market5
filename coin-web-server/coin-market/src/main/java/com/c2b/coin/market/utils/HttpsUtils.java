/**
 * Copyright(c) 2011-2015 by YouCredit Inc.
 * All Rights Reserved
 */
package com.c2b.coin.market.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author biqingguo 修改 https方法 增加不需要参数的https方法
 */
public class HttpsUtils {
	private String charSet = "UTF-8";
	private String contentType = "application/x-www-form-urlencoded";
	// private String accept = "text/xml; *.*";
	private String acceptEncoding = "";
	private String pragma = "no-cache";
	private final HttpClientBuilder httpClientBuilder;
	private static Logger loggerlog = LoggerFactory.getLogger(HttpsUtils.class);

	/**
	 * 商户号加密码生成的加密令牌
	 */
	private String authToken;

	/**
	 * @param merchantId
	 *            商户编号
	 * @param password
	 *            商户密码
	 * @param jks
	 *            密钥目录
	 * @param connectionRequestTimeout
	 *            超时时间
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public HttpsUtils(String merchantId, String password, String jks, Integer connectionRequestTimeout)
			throws FileNotFoundException, IOException, GeneralSecurityException {
		System.setProperty("jsse.enableSNIExtension", "false");
		this.httpClientBuilder = HttpClientBuilder.create().disableAutomaticRetries()
				.setDefaultRequestConfig(
						RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout).build())
				.setSSLHostnameVerifier(new NoopHostnameVerifier());
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(jks), password.toCharArray());
		// 创建用于管理JKS密钥库的密钥管理器
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		// 初始化证书
		kmf.init(ks, password.toCharArray());
		SSLContext ctx = SSLContext.getInstance("SSL");
		ctx.init(kmf.getKeyManagers(), new TrustManager[] { new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
			}

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
			}
		} }, null);
		this.httpClientBuilder.setSSLContext(ctx);
		this.authToken = "Basic " + Base64.encodeBase64String((merchantId + ":" + password).getBytes());
	}

	/**
	 * 无参够造方法，用于建立普通的https连接
	 *
	 * @param connectionRequestTimeout
	 * @param connectTimeout
	 * @param socketTimeout
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public HttpsUtils(int connectionRequestTimeout, int connectTimeout, int socketTimeout)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		SSLConnectionSocketFactory sslsf = null;
		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
			@Override
			public boolean isTrusted(X509Certificate[] chain, String authType) {
				return true;
			}
		}).build();
		sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}

			@Override
			public void verify(String host, SSLSocket ssl) {
			}

			@Override
			public void verify(String host, X509Certificate cert) {
			}

			@Override
			public void verify(String host, String[] cns, String[] subjectAlts) {
			}
		});
		this.httpClientBuilder = HttpClients.custom().disableAutomaticRetries()
				.setDefaultRequestConfig(
						RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout)
								.setConnectionRequestTimeout(connectionRequestTimeout).build())
				.setSSLSocketFactory(sslsf);
	}

	public String send(String url, String packet) throws ClientProtocolException, IOException {
		System.setProperty("jsse.enableSNIExtension", "false");
		HttpPost post = new HttpPost(url);
		loggerlog.debug("发送服务器: {}", packet);
		post.setEntity(new StringEntity(packet, this.charSet));
		post.setHeader(HttpHeaders.CONTENT_TYPE, this.contentType);
		/**
		 * 删除此配置，需要测试快钱支付
		 */
		// post.setHeader(HttpHeaders.ACCEPT, this.accept);
		post.setHeader(HttpHeaders.ACCEPT_ENCODING, this.acceptEncoding);
		post.setHeader(HttpHeaders.PRAGMA, this.pragma);
		post.setHeader(HttpHeaders.CONTENT_ENCODING, this.charSet);
		post.setHeader(HttpHeaders.AUTHORIZATION, this.authToken);
		try (CloseableHttpResponse resp = this.httpClientBuilder.build().execute(post);) {
			int statusCode = resp.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK == statusCode) {
				// 读取内容
				String content = EntityUtils.toString(resp.getEntity(), this.charSet);
				loggerlog.debug("服务器返回: {}", content);
				return content;
			} else {
				throw new IllegalStateException("服务器返回异常：[" + statusCode + "]");
			}
		}
	}

	public byte[] agency(String url, InputStream in) throws ClientProtocolException, IOException {
		System.setProperty("jsse.enableSNIExtension", "false");
		HttpPost post = new HttpPost(url);
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		int ch;
		while ((ch = in.read()) != -1) {
			swapStream.write(ch);
		}
		StringEntity stringEntity = new StringEntity(this.charSet);
		stringEntity.writeTo(swapStream);
		post.setEntity(stringEntity);
		post.setHeader(HttpHeaders.CONTENT_TYPE, this.contentType);
		post.setHeader(HttpHeaders.ACCEPT_ENCODING, this.acceptEncoding);
		post.setHeader(HttpHeaders.PRAGMA, this.pragma);
		post.setHeader(HttpHeaders.CONTENT_ENCODING, this.charSet);
		post.setHeader(HttpHeaders.AUTHORIZATION, this.authToken);
		try (CloseableHttpResponse resp = this.httpClientBuilder.build().execute(post);) {
			int statusCode = resp.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK == statusCode) {
				// 读取内容
				return EntityUtils.toByteArray(resp.getEntity());
			} else {
				throw new IllegalStateException("服务器返回异常：[" + statusCode + "]");
			}
		}
	}

	/**
	 * @param charSet
	 *            the charSet to set
	 */
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	/**
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	// /**
	// * @param accept
	// * the accept to set
	// */
	// public void setAccept(String accept) {
	// this.accept = accept;
	// }

	/**
	 * @param acceptEncoding
	 *            the acceptEncoding to set
	 */
	public void setAcceptEncoding(String acceptEncoding) {
		this.acceptEncoding = acceptEncoding;
	}

	/**
	 * @param pragma
	 *            the pragma to set
	 */
	public void setPragma(String pragma) {
		this.pragma = pragma;
	}

	public static void main(String[] args) {
		try {
			HttpsUtils HttpsUtils = new HttpsUtils(1000, 1000, 1000);
			String send = HttpsUtils.send("https://www.sobot.com/ws-open/ticket/ticket_api", "");
			System.out.println(send);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
