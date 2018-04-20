package com.c2b.ethWallet.enumeration;

import java.util.HashMap;
import java.util.Map;

/**
 * 类说明 ETH系列TOKEN合约地址枚举类
 * 
 * @author Anne
 * @date 2017年12月22日
 */
public enum ETHTokenContractAddressEnum {

  zg_CONTRACT_ADDRESS("zg","zg","zg"),OMG_CONTRACT_ADDRESS("OMG","OMG","OMG"),SNT_CONTRACT_ADDRESS("SNT","SNT","SNT"),GNT_CONTRACT_ADDRESS("GNT","GNT","GNT"),POWR_CONTRACT_ADDRESS("POWR","POWR","POWR"),
  PKT_CONTRACT_ADDRESS("PKT","PKT","PKT");
  
  private String currency;
  private String tokenName;
  private String contractAddress;

  ETHTokenContractAddressEnum(String currency, String tokenName, String contractAddress) {
    this.currency = currency;
    this.tokenName = tokenName;
    this.contractAddress = contractAddress;
  }

  public String getTokenName() {
    return tokenName;
  }

  public void setTokenName(String tokenName) {
    this.tokenName = tokenName;
  }

  public String getContractAddress() {
    return contractAddress;
  }

  public void setContractAddress(String contractAddress) {
    this.contractAddress = contractAddress;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  private static final Map<String, ETHTokenContractAddressEnum> tokenContractAddressEnumMap = new HashMap<>();

  static {
    for (ETHTokenContractAddressEnum tokenContractAddressEnum : ETHTokenContractAddressEnum.values()) {
      tokenContractAddressEnumMap.put(tokenContractAddressEnum.getCurrency(), tokenContractAddressEnum);
    }
  }

  public static ETHTokenContractAddressEnum getETHTokenContractAddressEnum(String currency) {
    return tokenContractAddressEnumMap.get(currency);
  }
}
