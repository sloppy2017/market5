package com.dynamo.message.service;

import com.dynamo.message.vo.MessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessageService {
  @Autowired
  protected MessageSource messageSource;
  @Autowired
  private MailService mailService;
  @Autowired
  private SmsService smsService;

  public void sendMessage(MessageVo vo) {
    switch (vo.getMessageType()) {
      case "SMS_BIND_MOBILE":
      case "SMS_UNBIND_MOBILE":
      case "SMS_LOGIN_VERFIY":
        smsService.sendSms(vo, getMessage("SMS_BIND_MOBILE", vo.getLanguage()));
        break;
      case "SMS_WITHDRAW":
        smsService.sendSms(vo, getMessage(vo.getMessageType(), vo.getLanguage()));
        break;
      case "EMAIL_ACTIVE":
      case "EMAIL_RESET_PASSWORD":
      case "EMAIL_CHANGE_LOGIN_PWD":
      case "EMAIL_BIND_GOOGLE":
      case "EMAIL_AUTH_FAILURE":
      case "EMAIL_AUTH_SUCCESS":
      case "EMAIL_CHANGE_PAY_PWD":
      case "EMAIL_CHARGE_COIN":
      case "EMAIL_UNBIND_GOOGLE":
      case "EMAIL_UNBIND_MOBILE":
      case "EMAIL_WITHDRAW_APPLY":
      case "EMAIL_WITHDRAW_FAILURE":
      case "EMAIL_WITHDRAW_SUCCESS":
        sendEmail(vo);
        break;
      default:
    }
  }

  private String getMessage(String code, String language) {
    return messageSource.getMessage(code, null, Locale.forLanguageTag(language));
  }

  private void sendEmail(MessageVo vo){
    mailService.sendTemplateMail(vo, getMessage(vo.getMessageType().concat("_SUBJECT"), vo.getLanguage()), getMessage(vo.getMessageType(), vo.getLanguage()));
  }

}
