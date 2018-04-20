package com.c2b.coin.file.rest;

import com.c2b.coin.common.AjaxResponse;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.common.enumeration.ErrorMsgEnum;
import com.c2b.coin.web.common.BaseRest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Api("文件上传")
@RestController
@RequestMapping
public class FileRest extends BaseRest {
  @Value("${coin.file.path}")
  private String filePath;
  @Value("${coin.host}")
  private String host;
  /**
   * 实现多文件上传
   */
  @ApiOperation(value = "多文件上传")
  @RequestMapping(value = "multifileUpload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse multifileUpload(HttpServletRequest request) {
    List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("fileName");
    if (files.isEmpty()) {
      return writeObj(ErrorMsgEnum.FILE_FILE_NOT_NULL);
    }
    List<String> filePaths = new ArrayList<>();
    String random = UUID.randomUUID().toString();
    String path = filePath + File.separator + DateUtil.formateDate(DateUtil.getCurrentDate(), DateUtil.DATE_SMALL_STR) + File.separator + UUID.randomUUID();
    for (MultipartFile file : files) {
      int size = (int) file.getSize();
      String lastPath = File.separator + random + file.getOriginalFilename();
      if (file.isEmpty()) {
        return writeObj(ErrorMsgEnum.FILE_FILE_NOT_NULL);
      } else {
        File dest = new File(path + lastPath);
        if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
          dest.getParentFile().mkdirs();
        }
        try {
          file.transferTo(dest);
          filePaths.add(lastPath);
        } catch (Exception e) {
          e.printStackTrace();
          return writeObj(ErrorMsgEnum.FILE_UPLOAD_FILURE);
        }
      }
    }
    return writeObj(filePaths);
  }

  /**
   * 实现文件上传
   */
  @ApiOperation(value = "单文件上传")
  @RequestMapping(value = "fileUpload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public AjaxResponse fileUpload(MultipartHttpServletRequest request) {
    MultipartFile file = request.getFile("file");
    if (file.isEmpty()) {
      return writeObj(ErrorMsgEnum.FILE_FILE_NOT_NULL);
    }
    String fileName = file.getOriginalFilename();
    int size = (int) file.getSize();
    logger.info(fileName + "-->" + size);

    if(checkFileName(fileName) == false){
      return writeObj(ErrorMsgEnum.FILE_UPLOAD_FILURE);
    }
    try {
      String prefix = fileName.substring(fileName.lastIndexOf("."));
      String date = DateUtil.formateDate(DateUtil.getCurrentDate(), DateUtil.DATE_SMALL_STR);
      String toFileName = UUID.randomUUID() + prefix;
      String afterPath = date + File.separator + toFileName;
      String writePath = filePath + File.separator + afterPath;
      File dest = new File(writePath);
      if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
        dest.getParentFile().mkdirs();
      }
      logger.info("return path 1:"+host + date + "/" + toFileName);
      resizeImg(file,dest);
      logger.info("return path 2:"+host + date + "/" + toFileName);
      return writeObj(host + date + "/" + toFileName);
    } catch (Exception e) {
      e.printStackTrace();
      return writeObj(ErrorMsgEnum.FILE_UPLOAD_FILURE);
    }

//    try {
//      file.transferTo(dest); //保存文件
//      return writeObj(host + date + "/" + toFileName);
//    } catch (IOException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//      return writeObj(ErrorMsgEnum.FILE_UPLOAD_FILURE);
//    }
  }

  private boolean checkFileName(String fileName){
    String[] fileNameArr = fileName.split("\\.");
    String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
    if(fileNameArr.length <= 1 || (!"JPG".equals(prefix.toUpperCase()) && !"PNG".equals(prefix.toUpperCase()))){
      return false;
    }else{
      return true;
    }
  }

  public void resizeImg(MultipartFile file , File destFile) throws Exception {
    // 加载目标图片
//    File file = new File(srcImg);
//    String ext = srcImg.substring(srcImg.lastIndexOf(".") + 1);
    Image image = ImageIO.read(file.getInputStream());
    int width = image.getWidth(null);
    int height = image.getHeight(null);
    logger.info("image:"+image.getAccelerationPriority() + "width:"+ width + "height:"+height);
    // 将目标图片加载到内存。
    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = bufferedImage.createGraphics();
    g.drawImage(image, 0, 0, width, height, null);
    g.dispose();
    logger.info("image:"+bufferedImage.getAccelerationPriority()+"destFile"+destFile);
    // 保存目标图片。
    String fileName = file.getOriginalFilename();
    String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
    ImageIO.write(bufferedImage, prefix, destFile);
  }
}
