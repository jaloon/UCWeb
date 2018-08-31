package com.tipray.test;

import com.tipray.bean.Session;
import com.tipray.service.SessionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * session管理测试
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
public class SessionTest {
	@Resource
	private SessionService sessionService;

	@Test
	public void getSessionByUser() {
		Session session = sessionService.getSessionByUser(5L, 0);
		System.out.println();
		System.out.println(session);
		System.out.println();
	}
}
