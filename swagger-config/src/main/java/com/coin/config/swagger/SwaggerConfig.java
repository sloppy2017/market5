package com.coin.config.swagger;

import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

/**
 * @author <a href="mailto:guo_xp@163.com">guoxinpeng</a>
 * @version 1.0 2016/11/23 15:34
 * @projectname new-pay
 * @packname com.yingu.service.config.web
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig implements EnvironmentAware {
  private final Logger logger = LoggerFactory.getLogger(SwaggerConfig.class);
  public static final String DEFAULT_INCLUDE_PATTERN = "/";
  private RelaxedPropertyResolver propertyResolver;
  @Autowired
  private Environment env;

  @Bean
  public Docket createRestApi() {
    Predicate<String> path = PathSelectors.any();
    // 禁用正式环境swagger
    if (Arrays.asList(env.getActiveProfiles()).contains("prod")){
      path = PathSelectors.none();
    }
    logger.info("##################################初始化createRestApi################################################");
    return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage("com.unis")).paths(PathSelectors.any()).build();
  }

  private ApiInfo apiInfo() {
    logger.info("##################################初始化API信息################################################");
    return new ApiInfoBuilder().title("美食云RESTful APIs").description("…………").termsOfServiceUrl("http://www.youmeishi.cn").contact("研发中心").version("1.0").build();
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.propertyResolver = new RelaxedPropertyResolver(environment, "swagger.");
  }
}
