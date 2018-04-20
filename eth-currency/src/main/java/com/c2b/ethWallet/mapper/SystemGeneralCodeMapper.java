package com.c2b.ethWallet.mapper;

import java.util.List;
import java.util.Map;

import com.c2b.ethWallet.entity.SystemGeneralCode;
import com.coin.config.mybatis.BaseMapper;

public interface SystemGeneralCodeMapper extends BaseMapper<SystemGeneralCode> {
  List<Map<String,String>> findCode(String groupCode);
}