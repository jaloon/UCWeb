package com.tipray.test;

import com.tipray.service.CardService;
import com.tipray.service.SqliteSyncService;
import com.tipray.service.TransportCardService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.Map;

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
	@Resource
	private TransportCardService transportCardService;
	@Resource
	private SqliteSyncService sqliteSyncService;

	@Test
	public void executeSqliteSync() {
		sqliteSyncService.syncSqliteFile();
	}

	@Test
    public void testT() {
        Map<String, Object> transportCard = transportCardService.getByTransportCardId(123L);
    }

	@Test
	public void findUnusedCards() {
		cardService.findUnusedCard(1);
	}
}
