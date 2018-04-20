package com.c2b.coin.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class AuthVo {

  @ApiModelProperty(name = "firstName", value = "姓", dataType = "string", required = true)
  private String firstName;        //姓
  @ApiModelProperty(name = "lastName", value = "名字", dataType = "string", required = true)
  private String lastName;         //名字
  @ApiModelProperty(name = "sex", value = "性别  1 男  2  女", dataType = "Integer", required = true)
  private Integer sex;              //性别    1 男  2  女
  @ApiModelProperty(name = "regionType", value = "地区类型  0 中国大陆  1 港澳台及其他国家和地区", dataType = "Integer", required = true)
  private Integer regionType;      // 地区类型  0 中国大陆  1 港澳台及其他国家和地区
  @ApiModelProperty(name = "region", value = "国家/地区编码（非大陆地区）", dataType = "string", required = false)
  private String region;           //国家/地区编码（非大陆地区）
  @ApiModelProperty(name = "cardId", value = "证件ID", dataType = "string", required = true)
  private String cardId;           //证件ID
  @ApiModelProperty(name = "cardType", value = "证件类型：1 身份证  2 护照  3 驾驶证", dataType = "Integer", required = true)
  private Integer cardType;        //证件类型：1 身份证  2 护照  3 驾驶证
  @ApiModelProperty(name = "imgFrontUrl", value = "证件照正面图片路径", dataType = "string", required = true)
  private String imgFrontUrl;     //证件照正面图片路径（中国大陆地区）
  @ApiModelProperty(name = "imgBehindURL", value = "证件照反面图片路径", dataType = "string", required = true)
  private String imgBehindURL;    //证件照反面图片路径（中国大陆地区）
  @ApiModelProperty(name = "imgHandheldUrl", value = "手持证件照", dataType = "string", required = false)
  private String imgHandheldUrl;    //手持证件照（中国大陆地区）




  public Integer getRegionType() {
    return regionType;
  }

  public void setRegionType(Integer regionType) {
    this.regionType = regionType;
  }

  public Integer getCardType() {
    return cardType;
  }

  public void setCardType(Integer cardType) {
    this.cardType = cardType;
  }

  public String getCardId() {
    return cardId;
  }

  public void setCardId(String cardId) {
    this.cardId = cardId;
  }

  public Integer getSex() {
    return sex;
  }

  public void setSex(Integer sex) {
    this.sex = sex;
  }


  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getImgFrontUrl() {
    return imgFrontUrl;
  }

  public void setImgFrontUrl(String imgFrontUrl) {
    this.imgFrontUrl = imgFrontUrl;
  }

  public String getImgBehindURL() {
    return imgBehindURL;
  }

  public void setImgBehindURL(String imgBehindURL) {
    this.imgBehindURL = imgBehindURL;
  }

  public String getImgHandheldUrl() {
    return imgHandheldUrl;
  }

  public void setImgHandheldUrl(String imgHandheldUrl) {
    this.imgHandheldUrl = imgHandheldUrl;
  }
}
