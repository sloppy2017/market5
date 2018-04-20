package com.c2b.coin.market;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class WebServerConfiguration {
  @Value("${server.tomcat.max-connections}")
  private int maxConnections;

  @Value("${server.tomcat.max-threads}")
  private int maxThreads;
  @Value("${server.port}")
  private int port;
  @Value("${server.connection-timeout}")
  private int connectionTimeout;

  @Bean
  public EmbeddedServletContainerCustomizer createEmbeddedServletContainerCustomizer() {
    return new MyEmbeddedServletContainerCustomizer();
  }
  class MyEmbeddedServletContainerCustomizer implements EmbeddedServletContainerCustomizer {
    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
      TomcatEmbeddedServletContainerFactory tomcatFactory = (TomcatEmbeddedServletContainerFactory) container;
      tomcatFactory.setPort(port);
      //下面的操作可以参照上面的方法
    }
    protected Connector httpConnector() throws IOException {
      Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
      Http11NioProtocol http11NioProtocol = (Http11NioProtocol) connector.getProtocolHandler();
      connector.setPort(port);
      //设置最大线程数
      http11NioProtocol.setMaxThreads(maxThreads);
      http11NioProtocol.setMaxConnections(maxConnections);
//    http11NioProtocol.setCompression("true");
      //设置初始线程数  最小空闲线程数
      http11NioProtocol.setMinSpareThreads(20);
      //设置超时
      http11NioProtocol.setConnectionTimeout(connectionTimeout);
      return connector;
    }
  }
}


