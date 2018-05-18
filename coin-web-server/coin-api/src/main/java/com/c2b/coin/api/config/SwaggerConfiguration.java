package com.c2b.coin.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
@Profile({"dev"})
public class SwaggerConfiguration {

  @Bean
  public UiConfiguration uiConfig() {
    return new UiConfiguration(
      "/",// url
      "none",       // docExpansion          => none | list
      "alpha",      // apiSorter             => alpha
      "schema",     // defaultModelRendering => schema
      UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS,
      false,        // enableJsonEditor      => true | false
      true,
      6000L);        // showRequestHeaders    => true | false
  }

  @Bean
  public Docket createRestApi() {
    List<Parameter> pars = new ArrayList<>();
    ParameterBuilder accessKeyId = new ParameterBuilder();
    accessKeyId.name("AccessKeyId").description("AccessKeyId").defaultValue("").modelRef(new ModelRef("string")).parameterType("query").required(false);
    pars.add(accessKeyId.build());

    ParameterBuilder timestamp = new ParameterBuilder();
    timestamp.name("Timestamp").description("时间戳").defaultValue("").modelRef(new ModelRef("string")).parameterType("query").required(false);
    pars.add(timestamp.build());

    ParameterBuilder signature = new ParameterBuilder();
    signature.name("Signature").description("签名").defaultValue("").modelRef(new ModelRef("string")).parameterType("query").required(false);
    pars.add(signature.build());

    ParameterBuilder signatureMethod = new ParameterBuilder();
    signatureMethod.name("SignatureMethod").description("签名方法").defaultValue("HmacSHA256").modelRef(new ModelRef("string")).parameterType("query").required(false);
    pars.add(signatureMethod.build());

    ParameterBuilder signatureVersion = new ParameterBuilder();
    signatureVersion.name("SignatureVersion").description("版本").defaultValue("1").modelRef(new ModelRef("string")).parameterType("query").required(false);
    pars.add(signatureVersion.build());

    return new Docket(DocumentationType.SWAGGER_2).globalOperationParameters(pars).apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage("com.c2b.coin.api")).paths(PathSelectors.any()).build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
      .title("ParkOne API 文档")
      .description("REST API Version for v1.0")
      .termsOfServiceUrl("http://localhost:8060/")
      .version("1.0")
      .build();
  }

}
