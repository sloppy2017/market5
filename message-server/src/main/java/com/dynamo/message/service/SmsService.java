package com.dynamo.message.service;

import com.dynamo.message.service.emay.YMSmsService;
import com.dynamo.message.service.nexmo.NexmoService;
import com.dynamo.message.vo.MessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SmsService {
  @Autowired
  private YMSmsService ymSmsService;
  @Autowired
  private NexmoService nexmoService;

  public void sendSms(MessageVo messageVo, String content){
    if (StringUtils.isEmpty(messageVo.getRegionCode())|| messageVo.getRegionCode().equals("86")){
      ymSmsService.sendActiveSms(messageVo, content);
    }else{
      nexmoService.sendSms(messageVo, content);
    }
  }
}
