package com.c2b.coin.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c2b.coin.util.IdWorker;

@RestController
public class IdController {

	@RequestMapping(value = "next-id",produces=MediaType.APPLICATION_JSON_VALUE)
	public Object nextId() {
		IdWorker worker2 = new IdWorker(2);
		Map<String,Object> map =new HashMap();
		String id =String.valueOf(worker2.nextId());
		map.put("id", id);
		return map; 
	}
}
