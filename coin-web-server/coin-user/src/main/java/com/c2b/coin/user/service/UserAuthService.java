package com.c2b.coin.user.service;


import com.alibaba.fastjson.JSONObject;
import com.c2b.coin.common.Constants;
import com.c2b.coin.common.DateUtil;
import com.c2b.coin.common.MessageEnum;
import com.c2b.coin.user.entity.UserAuth;
import com.c2b.coin.user.entity.UserInfo;
import com.c2b.coin.user.entity.UserNews;
import com.c2b.coin.user.mapper.UserAuthMapper;
import com.c2b.coin.user.mapper.UserInfoMapper;
import com.c2b.coin.user.mapper.UserNewsMapper;
import com.c2b.coin.user.util.OCRUtil;
import com.c2b.coin.user.vo.AuthVo;
import com.c2b.coin.web.common.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class UserAuthService  {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  protected HttpServletRequest request;

  @Autowired
  protected UserAuthMapper userAuthMapper;

  @Autowired
  protected UserInfoMapper userInfoMapper;

  @Autowired
  protected UserNewsMapper userNewsMapper;

  @Autowired
  protected OCRAPIService ocrapiService;

  @Autowired
  ThreadPoolTaskExecutor taskExecutor;
  @Autowired
  MessageService messageService;

  @Value("${img.file.path}")
  private String imgFilePath;

  @Value("${img.file.url}")
  private String imgFileUrl;

  @Value("${img.file.instr}")
  private String instr;

  @Transactional(rollbackFor = Exception.class)
  public boolean saveUserAuth(AuthVo authVo,UserInfo userInfo){
    Long userId = userInfo.getId();
    String userName = userInfo.getUsername();
    try{
      UserAuth userAuth = new UserAuth();
      userAuth.setUserId(userId);
      userAuth.setUsername(userName);
      userAuth.setFirstName(authVo.getFirstName());
      userAuth.setLastName(authVo.getLastName());
      userAuth.setCardId(authVo.getCardId());
      userAuth.setSex(authVo.getSex());
      if(authVo.getRegionType() == null || authVo.getRegionType() == 0){
        userAuth.setRegion("中国大陆");
        userAuth.setCardType(1);
      }else{
        userAuth.setRegion(authVo.getRegion());
        userAuth.setCardType(authVo.getCardType());
      }
      HashMap urlMap = new HashMap();
      urlMap.put("frontUrl",authVo.getImgFrontUrl());
      urlMap.put("behindUrl",authVo.getImgBehindURL());
      urlMap.put("imgHandheldUrl", authVo.getImgHandheldUrl());
      String imgUrls = JSONObject.toJSONString(urlMap);
      userAuth.setImgUrl(imgUrls);
      userAuth.setCreatetime(DateUtil.getCurrentTimestamp());
      userAuth.setIp(IPUtils.getIpAddr(request));
      userAuth.setAuthStatus(Constants.User_AUTH_CHECKING);

      UserAuth userAuthNew = new UserAuth();
      userAuthNew.setUserId(userId);
      List<HashMap>  list = userAuthMapper.selectByUserId(userId);
      if(list  == null || list.size() == 0){
        this.userAuthMapper.insert(userAuth);
      }else{
        Example example = new Example(userAuth.getClass());
        example.createCriteria().andEqualTo("userId",userId);
        userAuthMapper.updateByExampleSelective(userAuth,example);
      }
      userInfoMapper.updateIsAuthByUserId(new Integer(1),userId);
    }catch (Exception e){
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @Transactional(rollbackFor = Exception.class)
  public void runcheck(AuthVo authVo,Long userId,String userName, Locale locale) throws Exception{
    taskExecutor.execute(new Runnable() {
      @Override
      public void run() {
        StringBuilder error = new StringBuilder();
        URL url = null;
        try {
          url = new URL(authVo.getImgFrontUrl());
        } catch (MalformedURLException e) {
          e.printStackTrace();
        }

        String imgPathFront = authVo.getImgFrontUrl().replaceAll(instr,imgFilePath);
        imgPathFront = imgPathFront.replaceAll(url.getHost(),"");
        imgPathFront = imgPathFront.replaceAll(url.getProtocol(),"");
        imgPathFront = imgPathFront.replaceAll(url.getPort()+"","");
        imgPathFront = imgPathFront.replaceAll(":","");
        imgPathFront = imgPathFront.replaceAll("//","");
        String frontInfo = ocrapiService.doPost(imgPathFront,"2");
        logger.debug("OCR INFO:" + frontInfo);
        List<String> list = OCRUtil.printjson(frontInfo);
        Map map1 = OCRUtil.listToMap(list);
        if(map1.containsKey("识别失败!ReconClientL:-2")){
          error.append("身份证正面，识别失败!");
        }
        String imgPathBehind = authVo.getImgBehindURL().replaceAll(imgFileUrl,imgFilePath);
        Map map2 = OCRUtil.listToMap(OCRUtil.printjson(ocrapiService.doPost(imgPathBehind,"3")));
        if(map2.containsKey("识别失败!ReconClientL:-2")){
          error.append("身份证反面，识别失败!");
        }

        Map map = new HashMap();
        map.putAll(map1);
        map.putAll(map2);
        String name = (String)map1.get("姓名");
        String sex = map1.get("性别").equals("男")?"1":"2";
        String id = (String)map1.get("公民身份号码");
        if(name.equals(authVo.getFirstName()+authVo.getLastName()) == false){
          error.append("姓名不符!");
        }
        if(sex.equals(authVo.getSex().toString()) == false){
          error.append("性别不符!");
        }
        if(id.equals(authVo.getCardId()) == false){
          error.append("公民身份号码不符!");
        }

        UserAuth userAuth = new UserAuth();
        userAuth.setUserId(userId);
        userAuth.setRemark(JSONObject.toJSONString(map));
        if(StringUtils.isEmpty(error.toString())){
          userAuth.setAuthStatus(Constants.User_AUTH_SUCCESS);
          userInfoMapper.updateIsAuthByUserId(Constants.User_AUTH_SUCCESS,userId);
          messageService.sendAuthSuccessEmail(userInfoMapper.selectByPrimaryKey(userId), locale);
        }else{
          userAuth.setAuthStatus(Constants.User_AUTH_FAIL);
          userInfoMapper.updateIsAuthByUserId(Constants.User_AUTH_FAIL,userId);
          messageService.sendAuthFailureEmail(userInfoMapper.selectByPrimaryKey(userId), locale);
        }
        userAuth.setError(error.toString());
        userAuth.setUpdateTime(DateUtil.getCurrentTimestamp());
        userAuth.setCardAddress((String)map1.get("住址"));

        Example example = new Example(userAuth.getClass());
        example.createCriteria().andEqualTo("userId",userId);
        userAuthMapper.updateByExampleSelective(userAuth,example);

        UserNews userNews = new UserNews();

        userNews.setUserId(userId);
        userNews.setUsername(userName);
        userNews.setTitle("身份审核已通过");
        userNews.setContent("身份审核已通过");
        userNews.setType(1);
        userNews.setIsTop(0);
        userNews.setIsRead(0);
        userNews.setStatus(0);
        userNews.setCreatetime(DateUtil.getCurrentTimestamp());
        userNewsMapper.insert(userNews);
      }
    });
  }


  public List<HashMap> getIdentityAuthInfo(Long userId){
    List<HashMap> infoList = this.userAuthMapper.selectByUserId(userId);
    return infoList;
  }

}
