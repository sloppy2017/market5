package com.dynamo.message.service;

import com.dynamo.message.vo.MessageVo;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class MailService {
  @Autowired
  private JavaMailSender mailSender; //自动注入的Bean

  @Value("${spring.mail.from}")
  private String Sender; //读取配置文件中的参数

  /**
   * 发送简单的文本邮件
   * @param toUser
   * @param content
   * @param subject
   */
  public void sendTxtEmail(String toUser, String content, String subject){
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(Sender);
    message.setTo(toUser); //自己给自己发送邮件
    message.setSubject(subject);
    message.setText(content);
    mailSender.send(message);
  }

  /**
   * 发送html邮件
   * @param toUser
   * @param subject
   * @param html
   */
  public void sendHtmlEmail(String toUser, String subject, String html){
    MimeMessage message = null;
    try {
      message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setFrom(Sender,"CoinToBe");
      helper.setTo(toUser);
      helper.setSubject(subject);
      helper.setText(html, true);
    } catch (Exception e) {
      e.printStackTrace();
    }
    mailSender.send(message);
  }

  /**
   * 发送带附件的邮件
   * @param toUser
   * @param subject
   * @param content
   * @param filePath
   * @param fileName
   */
  public void sendAttachmentsMail(String toUser, String subject, String content, String filePath, String fileName) {
    MimeMessage message = null;
    try {
      message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setFrom(Sender);
      helper.setTo(toUser);
      helper.setSubject(subject);
      helper.setText(content);
      //注意项目路径问题，自动补用项目路径
      FileSystemResource file = new FileSystemResource(new File(filePath));
      //加入邮件
      helper.addAttachment(fileName, file);
    } catch (Exception e){
      e.printStackTrace();
    }
    mailSender.send(message);
  }
  @Autowired
  private FreeMarkerConfigurer freeMarkerConfigurer;  //自动注入
  public void sendTemplateMail(MessageVo vo, String subject, String templateName){
    MimeMessage message = null;
    try {
      message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setFrom(Sender,"CoinToBe");
      helper.setTo(vo.getToUser());
      helper.setSubject(subject);
      //读取 html 模板
      Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templateName);
      String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, vo.getData());
      helper.setText(html, true);
    } catch (Exception e) {
      e.printStackTrace();
    }
    mailSender.send(message);
  }

}
