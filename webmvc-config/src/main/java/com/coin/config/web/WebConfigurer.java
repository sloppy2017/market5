package com.coin.config.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
//@EnableWebMvc
public class WebConfigurer extends WebMvcConfigurerAdapter {

  private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);
  private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {"classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/", "/WEB-INF/pages/"};

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    log.info("初始化Bean(configureMessageConverters),用户解决消息转换问题");
    List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
    messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
    messageConverters.add(new FormHttpMessageConverter());
    messageConverters.add(new MappingJackson2HttpMessageConverter());
//    messageConverters.add(new GsonHttpMessageConverter());
    converters.addAll(messageConverters);
  }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.favorPathExtension(false);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    if (!registry.hasMappingForPattern("/webjars/**")) {
      registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
    if (!registry.hasMappingForPattern("/**")) {
      registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
    }
  }

  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    configurer.enable();
  }

//  @Override
//  public void addCorsMappings(CorsRegistry registry) {
//    registry.addMapping("/**")
//      .allowedOrigins("*")
//      .allowCredentials(true)
//      .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH")
//      .maxAge(3600);
//  }
  @Bean
  public LocaleResolver localeResolver() {
    SessionLocaleResolver slr = new SessionLocaleResolver();
    // 默认语言
    slr.setDefaultLocale(Locale.US);
    return slr;
  }

  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    log.info("========================使用参数修改用户的区域===========================");
    LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
    // 参数名
    lci.setParamName("lang");
    return lci;
  }
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(localeChangeInterceptor());
  }

}
