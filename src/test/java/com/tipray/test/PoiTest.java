package com.tipray.test;

import java.util.List;

import org.junit.Test;

import com.github.crab2died.ExcelUtils;
import com.tipray.bean.baseinfo.GasStation;

public class PoiTest {
	@Test
	public void test() {
		try {
			List<GasStation> gasStations = ExcelUtils.getInstance().readExcel2Objects("C:\\Users\\jazur\\Desktop\\加油站.xlsx", GasStation.class, 0, Integer.MAX_VALUE, 0);
			for (GasStation gasStation : gasStations) {
				System.out.println(gasStation);
			}
//			ExcelUtils.getInstance().exportObjects2Excel(stations, Station.class, "C:\\Users\\jazur\\Desktop\\加油站编码1.xlsx");
//			ExcelUtils.getInstance().exportObjects2Excel("C:\\Users\\jazur\\Desktop\\template.xlsx", stations, Station.class, "C:\\Users\\jazur\\Desktop\\加油站编码2.xlsx");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
