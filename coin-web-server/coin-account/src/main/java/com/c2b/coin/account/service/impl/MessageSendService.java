package com.c2b.coin.account.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.c2b.coin.account.entity.UserNews;
import com.c2b.coin.account.mapper.UserNewsMapper;
import com.c2b.coin.account.service.IMessageSendService;
import com.c2b.coin.common.Constants;
import com.c2b.coin.common.DateUtil;

@Service
public class MessageSendService implements IMessageSendService{

	@Autowired
	private UserNewsMapper userNewsMapper;

	@Override
	public void insertToDB(Long userId, String userName, int type, BigDecimal amount, BigDecimal afterAmount) {
		UserNews userNews = this.buildUserNews(userId, userName, type);
		switch (type) {
		case Constants.USER_NEWS_DEPOSIT:
			this.setDepositNews(userNews,amount,afterAmount);
			break;
		case Constants.USER_NEWS_WITHDRAW:
			this.setWithdrawNews(userNews,amount,afterAmount);
			break;
		default:
			break;
		}
		//插入数据库
		this.userNewsMapper.insert(userNews);
	}


	private void setWithdrawNews(UserNews userNews, BigDecimal amount, BigDecimal afterAmount) {
		userNews.setTitle("用户提币通知");
		userNews.setContent("用户提现金额为："+amount.toString()+"提现后余额："+afterAmount);
	}


	private void setDepositNews(UserNews userNews, BigDecimal amount, BigDecimal afterAmount) {
		userNews.setTitle("用户充币通知");
		userNews.setContent("用户充现金额为："+amount.toString()+"充值后余额："+afterAmount);
	}


	private UserNews buildUserNews(Long userId, String userName, int type) {
		UserNews userNews=new UserNews();
		userNews.setType(type);
		userNews.setUserId(userId);
		userNews.setUsername(userName);
		userNews.setIsTop(0);
		userNews.setIsRead(0);
		userNews.setCreatetime(DateUtil.getCurrentTimestamp());
		userNews.setIsDel(0);
		userNews.setStatus(0);
		return userNews;
	}
}
