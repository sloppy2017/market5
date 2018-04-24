package com.coin.config.mybatis;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import tk.mybatis.mapper.code.Style;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

import java.util.Properties;

/**
 * @author <a href="mailto:guo_xp@163.com">guoxinpeng</a>
 */
@Configuration
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class MyBatisMapperScannerConfig {
  private Logger logger = LoggerFactory.getLogger(MyBatisMapperScannerConfig.class);



  @Bean
  @Primary
  public MapperScannerConfigurer mapperScannerConfigurer() {
    logger.info("设置Mybatis通用Mapper");
    MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
    mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
    mapperScannerConfigurer.setBasePackage("com.c2b.**.mapper");
    MapperHelper mapperHelper = new MapperHelper();
    Config config = new Config();
    config.setStyle(Style.camelhumpAndLowercase);
    Properties properties = new Properties();
    // 这里要特别注意，不要把MyMapper放到 basePackage 中，也就是不能同其他Mapper一样被扫描到。
    properties.setProperty("mappers", BaseMapper.class.getName());
    properties.setProperty("notEmpty", "false");
    properties.setProperty("IDENTITY", "MYSQL");
    config.setOrder("BEFORE");
    config.setProperties(properties);
    mapperHelper.setConfig(config);
    mapperHelper.registerMapper(BaseMapper.class);
    mapperScannerConfigurer.setMapperHelper(mapperHelper);
    return mapperScannerConfigurer;
  }
}
