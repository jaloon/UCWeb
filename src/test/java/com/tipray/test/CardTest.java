package com.tipray.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tipray.service.CardService;

/**
 * 卡管理测试
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
public class CardTest {
	@Resource
	private CardService cardService;

	@Test
	public void findUnusedCards() {
		cardService.findUnusedCard(1, 1L, null);
	}
}
