package com.tipray.test;

import com.tipray.bean.baseinfo.User;
import com.tipray.bean.log.InfoManageLog;
import com.tipray.core.exception.ServiceException;
import com.tipray.service.InfoManageLogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Random;

/**
 * 操作日志管理测试
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
public class OperateLogTest {
	@Resource
	private InfoManageLogService infoManageLogService;

	@Test
	public void add() {
		Random random = new Random();
		int type = random.nextInt(10);
		long id = Long.valueOf(random.nextInt(10) + 1 + "");
		User user = new User();
		user.setId(id);
		InfoManageLog infoManageLog = new InfoManageLog();
		infoManageLog.setUser(user);
		infoManageLog.setType(type);
		infoManageLog.setDescription("test");
		try {
			infoManageLogService.addInfoManageLog(infoManageLog);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void add10() {
		try {
			for (int i = 0; i < 500; i++) {
				add();
				Thread.sleep(1500);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
