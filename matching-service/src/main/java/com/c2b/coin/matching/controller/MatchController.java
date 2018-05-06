package com.c2b.coin.matching.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c2b.coin.matching.match.Matcher;
import com.c2b.coin.matching.vo.queue.ExchangeVO;
import com.c2b.coin.matching.vo.queue.ResultCallbackVO;

@RestController
@RequestMapping("/match")
public class MatchController {

	@Autowired
	private Matcher matcher;
	
	@PostMapping("/callback")
	public ResultCallbackVO callBack(String tradePair,String seq) {
		
		ResultCallbackVO resultCallbackVO=matcher.matchCallback(tradePair,seq);
		return resultCallbackVO;
	}
	
}
