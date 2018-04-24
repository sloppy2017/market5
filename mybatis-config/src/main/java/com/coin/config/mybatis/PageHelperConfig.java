package com.coin.config.mybatis;

import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author <a href="mailto:guo_xp@163.com">guoxinpeng</a>
 */
@Configuration
public class PageHelperConfig {
  private Logger logger = LoggerFactory.getLogger(PageHelperConfig.class);
  /**
   * 分页插件 *
   */
  @Bean
  public PageHelper pageHelper() {
    logger.info("注册MyBatis分页插件PageHelper");
    PageHelper pageHelper = new PageHelper();
    Properties properties = new Properties();
    properties.setProperty("offsetAsPageNum", "true");
    properties.setProperty("rowBoundsWithCount", "true");
    properties.setProperty("reasonable", "true");
    pageHelper.setProperties(properties);
    return pageHelper;
  }
}
