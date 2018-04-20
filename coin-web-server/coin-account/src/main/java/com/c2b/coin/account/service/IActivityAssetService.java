package com.c2b.coin.account.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IActivityAssetService {

	void addActivityAsset(long userId, String userName, int currencyType, BigDecimal amount,int operationType, String remark);

  List<Map<String, Object>> getActivityCoin(String userId, String activityName);
}
