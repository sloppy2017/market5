package com.coin.config.web;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.util.Arrays;
import java.util.List;

/**
 * @author MMM
 * @desc 描述
 * @date 2017-02-27 15:39
 **/
public class UnisGsonHttpMessageConverter extends GsonHttpMessageConverter {
  public UnisGsonHttpMessageConverter() {
    List<MediaType> types = Arrays.asList(
      new MediaType("text", "html", DEFAULT_CHARSET),
      new MediaType("application", "json", DEFAULT_CHARSET),
      new MediaType("application", "*+json", DEFAULT_CHARSET)
    );
    super.setSupportedMediaTypes(types);
  }
}
