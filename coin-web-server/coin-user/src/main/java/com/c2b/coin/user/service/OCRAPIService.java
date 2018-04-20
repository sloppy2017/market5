package com.c2b.coin.user.service;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class OCRAPIService {

  @Value("${OCR.recog.URL}")
  private String serviceURL;

  @Value("${OCR.key}")
  private String ocrKey;

  @Value("${OCR.secret}")
  private String ocrSecret;

  /**
   * 发送图片到第三方服务进行验证
   * @param imgPath 图片路径
   * @Param typeId   验证类型 2 ： 身份证正面 3 ：身份证反面
   */
  public String doPost(String imgPath, String typeId) {
    String result = "";
    try {

      CloseableHttpClient client = HttpClients.createDefault(); 										// 1.创建httpclient对象
      HttpPost post = new HttpPost(serviceURL); 																// 2.通过url创建post方法
      post.setHeader("accept", "application/json");

      //***************************************<向post方法中封装实体>************************************//3.向post方法中封装实体
				/* post方式实现文件上传则需要使用multipart/form-data类型表单，httpclient4.3以后需要使用MultipartEntityBuilder来封装
				 * 对应的html页面表单：
					 <form name="input" action="http://netocr.com/api/recog.do" method="post" enctype="multipart/form-data">
				        	请选择要上传的文件<input  type="file" NAME="file"><br />
							key:<input type="text" name="key" value="W8Nh5AU2xsTYzaduwkzEuc" />	<br />
							secret:<input type="text" name="secret" value="9646d012210a4ba48b3ba16737d6f69f" /><br />
							typeId:<input type="text" name="typeId" value="2"/><br />
							format:<input type="text" name="format" value=""/><br />
							<input type="submit" value="提交">
					</form>
				 */

      MultipartEntityBuilder builder = MultipartEntityBuilder.create();									//实例化实体构造器
      builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);												//设置浏览器兼容模式

      builder.addPart("img",new StringBody(GetImageStr(imgPath), ContentType.create("text/plain", Consts.UTF_8)));			//添加"img"字段及其值
      builder.addPart("key", new StringBody(ocrKey, ContentType.create("text/plain", Consts.UTF_8)));		//添加"key"字段及其值
      builder.addPart("secret", new StringBody(ocrSecret, ContentType.create("text/plain", Consts.UTF_8)));	//添加"secret"字段及其值
      builder.addPart("typeId", new StringBody(typeId, ContentType.create("text/plain", Consts.UTF_8)));	//添加"typeId"字段及其值
      builder.addPart("format", new StringBody("json", ContentType.create("text/plain", Consts.UTF_8)));	//添加"format"字段及其值

      HttpEntity reqEntity = builder.setCharset(CharsetUtils.get("UTF-8")).build();						//设置请求的编码格式，并构造实体

      post.setEntity(reqEntity);
      //**************************************</向post方法中封装实体>************************************

      CloseableHttpResponse response = client.execute(post);												 // 4.执行post方法，返回HttpResponse的对象
      if (response.getStatusLine().getStatusCode() == 200) {		// 5.如果返回结果状态码为200，则读取响应实体response对象的实体内容，并封装成String对象返回
        result = EntityUtils.toString(response.getEntity(), "UTF-8");
      } else {
        System.out.println("服务器返回异常");
      }

      try {
        HttpEntity e = response.getEntity();					 // 6.关闭资源
        if (e != null) {
          InputStream instream = e.getContent();
          instream.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        response.close();
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
    }

    return result;														//7.返回识别结果

  }

  //图片转化成base64字符串
  public static String GetImageStr(String path)
  {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
    InputStream in = null;
    byte[] data = null;
    //读取图片字节数组
    try
    {
      in = new FileInputStream(path);
      data = new byte[in.available()];
      in.read(data);
      in.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    //对字节数组Base64编码
    BASE64Encoder encoder = new BASE64Encoder();
    return encoder.encode(data);//返回Base64编码过的字节数组字符串
  }

//  public static void main(String[] args) {
//    String path="C://work//zj//front.jpg";//上传的待识别图片
//    String key = "MMoqVgTG3x2viLH431zGEc"; // 用户ocrKey
//    String secret = "e39c4081fe444858a5522e0e0fa31fe2"; // 用户ocrSecret
//    String typeId = "2";								//证件类型(例如:二代证正面为"2"。详见文档说明)
////    String format = "xml";
//		String format = "json"; //(返回的格式可以是xml，也可以是json)
//    String img=GetImageStr(path);
//    String url = "http://www.netocr.com/api/recogliu.do";	//http接口调用地址
//
//    String resultback = doPost(url, img, key, secret, typeId, format);
//    List<String> list=new ArrayList<String>();
//    System.out.println(resultback);						//控制台打印输出识别结果
//
//    list= OCRUtil.printjson(resultback);						//解析json字符串
////				list=OCRUtil.printjsonfordoc(resultback);						//解析json字符串(文档)
//    for(int i=0;i<list.size();i++){
//      System.out.println(list.get(i));
//    }
//  }

}
