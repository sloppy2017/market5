package com.c2b.coin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c2b.coin.util.IdWorker;

@RestController
public class IdController {

	@RequestMapping("next-id")
	public long nextId() {
		IdWorker worker2 = new IdWorker(2);
		return worker2.nextId(); 
	}
}
