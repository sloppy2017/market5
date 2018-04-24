//package com.c2b.config.mybatis;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.boot.bind.PropertiesConfigurationFactory;
//import org.springframework.context.EnvironmentAware;
//import org.springframework.core.env.ConfigurableEnvironment;
//import org.springframework.core.env.Environment;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class MybatisConfig implements BeanFactoryPostProcessor, EnvironmentAware {
//  @Override
//  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//    MapperProperties mapperProperties = getMapperProperties();
//    Map<String, SwaggerGroupProperties> swaggers = SwaggerProperties.();
//    List<String> pathRegexs = new ArrayList<>();
//    if (swaggers != null && !swaggers.isEmpty())
//      swaggers.forEach((name, swaggerConfig) -> {
//        beanFactory.registerSingleton(name, this.getSwaggerDocket(swaggerConfig));
//        pathRegexs.add(swaggerConfig.getPathRegex());
//      });
//    beanFactory.registerSingleton("other-api", this.getOtherSwaggerDocket(pathRegexs));
//  }
//
//  private ConfigurableEnvironment environment;
//
//  @Override
//  public void setEnvironment(Environment environment) {
//    this.environment = (ConfigurableEnvironment) environment;
//  }
//  private MapperProperties getMapperProperties() {
//    PropertiesConfigurationFactory<MapperProperties> factory = new PropertiesConfigurationFactory<>(
//      MapperProperties.class);
//    factory.setPropertySources(environment.getPropertySources());
//    factory.setConversionService(environment.getConversionService());
//    factory.setIgnoreInvalidFields(false);
//    factory.setIgnoreUnknownFields(true);
//    factory.setIgnoreNestedProperties(false);
//    factory.setTargetName(MapperProperties.MAPPER_PREFIX);
//    try {
//      factory.bindPropertiesToTarget();
//      return factory.getObject();
//    } catch (Exception e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//}
