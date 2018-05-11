package com.tipray.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tipray.bean.baseinfo.InOutReader;
import com.tipray.bean.baseinfo.OilDepot;
import com.tipray.service.OilDepotService;
import com.tipray.util.HttpRequestUtil;
import com.tipray.util.JSONUtil;

/**
 * 油库管理测试
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
public class OilDepotTest {
	@Resource
	private OilDepotService oilDepotService;

	@Test
	public void update() {
		OilDepot oilDepot = new OilDepot();
		oilDepot.setId(3L);
		oilDepot.setOfficialId("123");
		oilDepot.setName("南宁油库1");
		oilDepot.setDirector("wang");
		oilDepot.setPhone("123456789");
		oilDepot.setAddress("广西南宁");
		oilDepot.setCompany("中石油广西分公司");
		oilDepot.setLongitude(118.10501f);
		oilDepot.setLatitude(24.438171f);
		oilDepot.setCoverRegion(
				"(118.10501,24.438171),(118.12478,24.434849),(118.14967,24.437578),(118.16703,24.448236),(118.145615,24.43067)");
		String cardIds = "1,4,22";
		try {
			oilDepotService.updateOilDepot(oilDepot, cardIds, cardIds);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void add() {
		OilDepot oilDepot = new OilDepot();
		oilDepot.setOfficialId("123");
		oilDepot.setName("南宁油库1");
		oilDepot.setDirector("wang");
		oilDepot.setPhone("123456789");
		oilDepot.setAddress("广西南宁");
		oilDepot.setCompany("中石油广西分公司");
		// oilDepot.setLongitude(118.105013f);
		oilDepot.setLongitude(118.10501f);
		oilDepot.setLatitude(24.438171f);
		oilDepot.setCoverRegion(
				// "(118.105013, 24.438171),(118.124776,
				// 24.434848),(118.149677,24.437579),(118.167032, 24.448237),(118.145617,
				// 24.43067)");
				"(118.10501,24.438171),(118.12478,24.434849),(118.14967,24.437578),(118.16703,24.448236),(118.145615,24.43067)");
		// (118.10501,24.438171),(118.12478,24.434849),(118.14967,24.437578),(118.16703,24.448236),(118.145615,24.43067)
		try {
			oilDepotService.addOilDepot(oilDepot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getId() {
		OilDepot oilDepot = new OilDepot();
		try {
			oilDepot = oilDepotService.getOilDepotById(2L);
			System.out.println(oilDepot);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void fenceTest() {
		String url = "http://yingyan.baidu.com/api/v3/entity/aroundsearch";
		Map<String, String> params = new HashMap<>();
		params.put("ak", "");
		params.put("service_id", "159816");
		params.put("center", "159816");
		params.put("radius", "159816");
		try {
			HttpRequestUtil.sendGet(url, params);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void depotsAndStations() {
		Map<String, Object> info1 = oilDepotService.getIdAndNameOfAllOilDepotsAndGasStations(1l,1l);
		Map<String, Object> info2 = oilDepotService.getIdAndNameOfAllOilDepotsAndGasStations(1520401045522l,0l);
		System.out.println(info1);
		System.out.println(info2);
	}
	
	@Test
	public void testJson() throws Exception {
		List<InOutReader> inOutReaders = new ArrayList<>();
		String json1 = JSONUtil.stringify(inOutReaders);
		InOutReader inOutReader = new InOutReader();
		inOutReaders.add(inOutReader);
		String json2 = JSONUtil.stringify(inOutReaders);
		System.out.println(json1);
		System.out.println(json2);
		List<InOutReader> list1 = JSONUtil.parseToList(json1, InOutReader.class);
		List<InOutReader> list2 = JSONUtil.parseToList(json2, InOutReader.class);
		System.out.println(list1.size());
		System.out.println(list2.size());
	}
}
