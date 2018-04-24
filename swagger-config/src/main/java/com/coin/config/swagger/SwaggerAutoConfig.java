package com.coin.config.swagger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
public class SwaggerAutoConfig {
  private static final Logger log = LoggerFactory.getLogger(SwaggerAutoConfig.class);

  @Profile("dev")
  @EnableSwagger2
  public static class swaggerDocket extends SwaggerDocket {
    {
      log.debug("启用了swagger文档");
    }
  }

  static class ConditionApi implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      if (context.getEnvironment() != null) {
        if (context.getEnvironment().acceptsProfiles(("dev"))) {
          return false;
        }
      }
      return true;
    }
  }

  @RestController
  @Conditional(ConditionApi.class)
  public static class PreventSwagger extends PreventSwaggerResourcesController {

  }
}
