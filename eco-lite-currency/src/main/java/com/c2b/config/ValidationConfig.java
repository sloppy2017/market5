package com.c2b.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
public class ValidationConfig {

    /**
     * 参考链接http://blog.csdn.net/u010454030/article/details/53009327
     *
     * @return
     * @AssertFalse 校验false
     * @AssertTrue 校验true
     * @DecimalMax(value=,inclusive=) 小于等于value，inclusive=true,是小于等于
     * @DecimalMin(value=,inclusive=) 与上类似
     * @Max(value=) 小于等于value
     * @Min(value=) 大于等于value
     * @NotNull 检查Null
     * @Past 检查日期
     * @Pattern(regex=,flag=) 正则
     * @Size(min=, max=)  字符串，集合，map限制大小
     * @Valid 对po实体类进行校验
     */

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

}
