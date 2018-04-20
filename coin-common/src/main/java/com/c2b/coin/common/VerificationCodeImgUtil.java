package com.c2b.coin.common;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author MMM
 * @desc 图潘验证码
 * @project delicacy-cloud
 * @date 2017-03-14 9:50
 **/
@SuppressWarnings("AliControlFlowStatementWithoutBraces")
public class VerificationCodeImgUtil {
  // 图像
  private BufferedImage image;
  // 验证码
  private String str;
  // 图像宽度
  private int width = 91;
  // 图像高度
  private int height = 32;

  private static final Font FONT = new Font("Consolas", Font.BOLD, 20);

  /**
   * 功能：获取一个验证码类的实例
   *
   * @return
   */
  public static VerificationCodeImgUtil getInstance() {
    return new VerificationCodeImgUtil();
  }

  public static VerificationCodeImgUtil getInstance(int width, int height) {
    return new VerificationCodeImgUtil(width, height);
  }

  private VerificationCodeImgUtil() {
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
  }

  private VerificationCodeImgUtil(int width, int height) {
    this.width = width;
    this.height = height;
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
  }

  /**
   * 生成图片验证码
   *
   * @param code
   */
  public void initVerificationCode(String code) {
    Random random = new Random(); // 生成随机类
    Graphics g = initImage(image, random);
    char[] chars = code.toCharArray();
    for (int i = 0; i < chars.length; ++i) {
      g.setColor(new Color(20 + random.nextInt(10), 20 + random.nextInt(110), 20 + random.nextInt(110)));
      g.drawString(String.valueOf(chars[i]), 13 * i + 16, 26);
    }
    this.setStr(code);/* 赋值验证码 */
    g.dispose(); // 图象生效
  }

  private Graphics initImage(BufferedImage image, Random random) {
    Graphics g = image.getGraphics(); // 获取图形上下文
    g.setColor(getRandColor(200, 250));// 设定背景色
    g.fillRect(0, 0, width, width);
    g.setFont(FONT);// 设定字体
    g.setColor(getRandColor(160, 200)); // 随机产生165条干扰线，使图象中的认证码不易被其它程序探测到
    for (int i = 0; i < 165; i++) {
      int x = random.nextInt(width);
      int y = random.nextInt(width);
      int xl = random.nextInt(12);
      int yl = random.nextInt(12);
      g.drawLine(x, y, x + xl, y + yl);
    }
    return g;
  }

  /*
   * 功能：给定范围获得随机颜色
   */
  private Color getRandColor(int fc, int bc) {
    Random random = new Random();
    if (fc > 255) {
      fc = 255;
    }
    if (bc > 255) {
      bc = 255;
    }
    int r = fc + random.nextInt(bc - fc);
    int g = fc + random.nextInt(bc - fc);
    int b = fc + random.nextInt(bc - fc);
    return new Color(r, g, b);
  }

  /**
   * 功能：获取验证码的字符串值
   *
   * @return
   */
  public String getVerificationCodeValue() {
    return this.getStr();
  }

  /**
   * 功能：取得验证码图片
   *
   * @return
   */
  public BufferedImage getImage() {
    return this.image;
  }

  public String getStr() {
    return str;
  }

  private void setStr(String str) {
    this.str = str;
  }
}
