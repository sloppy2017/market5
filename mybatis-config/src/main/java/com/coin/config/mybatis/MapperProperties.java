package com.coin.config.mybatis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author <a href="mailto:guo_xp@163.com">guoxinpeng</a>
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ConfigurationProperties(prefix = MapperProperties.MAPPER_PREFIX)
public class MapperProperties {
  public static final String MAPPER_PREFIX = "spring.mapper";

  /**
   * mapper 扫描包
   */
  private String baseScanBasePackage;
  private boolean checkConfigLocation = false;
}
