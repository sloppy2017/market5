package com.c2b.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Component
public class CoreMessageSource {

    @Value(value = "${spring.messages.basename:locale/messages}")
    private String basename;

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(basename);
        return messageSource;
    }

    /**
     * 根据 key
     *
     * @param messageKey
     * @return
     */
    public String getMessage(String messageKey) {
        String message = messageSource().getMessage(messageKey, null, LocaleContextHolder.getLocale());
        return message;
    }

    /**
     * 根据 key,参数
     *
     * @param messageKey
     * @param args
     * @return
     */
    public String getMessage(String messageKey, Object... args) {
        String message = messageSource().getMessage(messageKey, args, LocaleContextHolder.getLocale());
        return message;
    }
}
