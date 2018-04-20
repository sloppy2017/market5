package com.c2b.config;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.concurrent.Callable;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

@Configuration
@EnableSwagger2
public class Swagger2 {

    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .alternateTypeRules(newRule(typeResolver.resolve(Callable.class,
                        typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                        typeResolver.resolve(WildcardType.class)))
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ecochain"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("钱包系统api接口")
                .description("Spring Boot中使用Swagger2构建RESTful APIs")
                .contact("ecochain")
                .version("1.0")
                .build();
    }

}
