package com.dynamo.message;

import com.dynamo.message.service.emay.YMSmsServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailTest {
  @Autowired
  private JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String Sender;
  @Autowired
  private FreeMarkerConfig freeMarkerConfig;
  @Autowired
  private FreeMarkerConfigurer freeMarkerConfigurer;  //自动注入
  @Autowired
  private YMSmsServiceImpl ymSmsService;

  //  @Test
//  public void sendTemplateMail() {
//    MimeMessage message = null;
//    try {
//      message = mailSender.createMimeMessage();
//      MimeMessageHelper helper = new MimeMessageHelper(message, true);
//      helper.setFrom(Sender);
//      helper.setTo("guo_xp@163.com");
//      helper.setSubject("主题：模板邮件");
//
//      Map<String, Object> model = new HashMap();
//      model.put("url", "zggdczfr");
//
//      //修改 application.properties 文件中的读取路径
////            FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
////            configurer.setTemplateLoaderPath("classpath:templates");
//      //读取 html 模板
//      Template template = freeMarkerConfigurer.getConfiguration().getTemplate("coin_active_zh.ftl");
//      String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
//      helper.setText(html, true);
//
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    mailSender.send(message);
//  }
  @Test
  public void testSms() {
//      ymSmsService.sendSms("test", "18511696693");
//    ymSmsService.setSingleSms("【Park.One】你好今天天气不错，挺风和日丽的", "18511696693", "UTF-8");
  }
}
