package com.dynamo.message;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

/**
 * Hello world!
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.dynamo.message" })
public class App {
	@Value("${spring.activemq.broker-url}")
	String brokerURL;
	@Value("${spring.activemq.userName}")
	String userName;
	@Value("${spring.activemq.password}")
	String password;

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

//	@Bean
//	public ConnectionFactory targetActiveMqConnectionFactory() {
//		ActiveMQConnectionFactory bean = new ActiveMQConnectionFactory();
//		bean.setBrokerURL(brokerURL);
//		bean.setUserName(userName);
//		bean.setPassword(password);
//		RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
//		redeliveryPolicy.setMaximumRedeliveries(6);
//		redeliveryPolicy.setInitialRedeliveryDelay(3000);
//		redeliveryPolicy.setQueue("*");
//		bean.setRedeliveryPolicy(redeliveryPolicy);
//		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
//		cachingConnectionFactory.setTargetConnectionFactory(bean);
//		cachingConnectionFactory.setSessionCacheSize(10);
//		cachingConnectionFactory.setReconnectOnException(true);
//		return cachingConnectionFactory;
//	}

	@Bean
	public JmsListenerContainerFactory<?> jmsListenerContainerQueue(ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
		bean.setConnectionFactory(connectionFactory);
		return bean;
	}

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setKeepAliveSeconds(60);
		threadPoolTaskExecutor.setMaxPoolSize(50);
		threadPoolTaskExecutor.setCorePoolSize(20);
		threadPoolTaskExecutor.setQueueCapacity(100);
		return threadPoolTaskExecutor;
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate;
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setReadTimeout(30000);
		requestFactory.setConnectTimeout(30000);

		// 添加转换器
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		messageConverters.add(new FormHttpMessageConverter());
		messageConverters.add(new MappingJackson2HttpMessageConverter());

		restTemplate = new RestTemplate(messageConverters);
		restTemplate.setRequestFactory(requestFactory);
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
		return restTemplate;
	}
}
