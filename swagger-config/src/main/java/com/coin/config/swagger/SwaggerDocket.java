package com.coin.config.swagger;

import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SwaggerDocket implements EnvironmentAware {
  private Logger logger = LoggerFactory.getLogger(this.getClass());
  private ConfigurableEnvironment environment;
  public static final String HTTP_HEADER_USER_ID = "x-access-userid";
  public static final String HTTP_HEADER_USER_NAME = "x-access-username";
  @Override
  public void setEnvironment(Environment environment) {
    this.environment = (ConfigurableEnvironment) environment;
  }

  @Bean
  public Docket createRestApi() {
    Predicate<String> path = PathSelectors.any();
    // 禁用正式环境swagger
    if (Arrays.asList(environment.getActiveProfiles()).contains("prod")){
      path = PathSelectors.none();
    }
    List<Parameter> pars = new ArrayList<Parameter>();
    ParameterBuilder username = new ParameterBuilder();
    username.name(HTTP_HEADER_USER_NAME).description("用户名").defaultValue("zhangsan@163.com").modelRef(new ModelRef("string")).parameterType("header").required(false);
    pars.add(username.build());
    ParameterBuilder userid = new ParameterBuilder();
    userid.name(HTTP_HEADER_USER_ID).description("用户id").defaultValue("1").modelRef(new ModelRef("string")).parameterType("header").required(false);
    pars.add(userid.build());
    logger.info("##################################初始化createRestApi################################################");
    return new Docket(DocumentationType.SWAGGER_2).globalOperationParameters(pars).apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage("com.c2b")).paths(PathSelectors.any()).build();
  }

  private ApiInfo apiInfo() {
    logger.info("##################################初始化API信息################################################");
    return new ApiInfoBuilder().title("COINTOBE APIs").description("…………").termsOfServiceUrl("http://www.****.cn").contact("研发中心").version("1.0").build();
  }
}
