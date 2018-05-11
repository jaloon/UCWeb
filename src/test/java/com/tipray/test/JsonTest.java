package com.tipray.test;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.tipray.util.JSONUtil;

import net.sf.json.JSONArray;

public class JsonTest {
	@Test
	public void test() {
		String json="{\r\n" + 
				"    \"vehicle_id\": \"1\",\r\n" + 
				"    \"vehicle_device_id\": \"268435457\",\r\n" + 
				"    \"vehicle_number\": \"闽A123456\",\r\n" + 
				"    \"tracklist\": [\r\n" + 
				"        {\r\n" + 
				"            \"is_lnglat_valid\": \"1\",\r\n" + 
				"            \"longitude\": \"118.070229\",\r\n" + 
				"            \"latitude\": \"24.576000\",\r\n" + 
				"            \"vehicle_status\": \"5\",\r\n" + 
				"            \"vehicle_alarm_status\": \"1\",\r\n" + 
				"            \"angle\": \"120\",\r\n" + 
				"            \"speed\": \"60\",\r\n" + 
				"            \"lock_status_info\": \"363534333231\",\r\n" + 
				"            \"track_time\": \"2018-01-15 13:25:10\"\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"is_lnglat_valid\": \"1\",\r\n" + 
				"            \"longitude\": \"118.070229\",\r\n" + 
				"            \"latitude\": \"24.576000\",\r\n" + 
				"            \"vehicle_status\": \"5\",\r\n" + 
				"            \"vehicle_alarm_status\": \"1\",\r\n" + 
				"            \"angle\": \"120\",\r\n" + 
				"            \"speed\": \"60\",\r\n" + 
				"            \"lock_status_info\": \"36353433323137\",\r\n" + 
				"            \"track_time\": \"2018-01-15 13:25:11\"\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"is_lnglat_valid\": \"1\",\r\n" + 
				"            \"longitude\": \"118.070229\",\r\n" + 
				"            \"latitude\": \"24.576000\",\r\n" + 
				"            \"vehicle_status\": \"5\",\r\n" + 
				"            \"vehicle_alarm_status\": \"1\",\r\n" + 
				"            \"angle\": \"120\",\r\n" + 
				"            \"speed\": \"60\",\r\n" + 
				"            \"lock_status_info\": \"363534333231\",\r\n" + 
				"            \"track_time\": \"2018-01-15 13:25:12\"\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"is_lnglat_valid\": \"1\",\r\n" + 
				"            \"longitude\": \"118.070229\",\r\n" + 
				"            \"latitude\": \"24.576000\",\r\n" + 
				"            \"vehicle_status\": \"5\",\r\n" + 
				"            \"vehicle_alarm_status\": \"1\",\r\n" + 
				"            \"angle\": \"120\",\r\n" + 
				"            \"speed\": \"60\",\r\n" + 
				"            \"lock_status_info\": \"36353433323137\",\r\n" + 
				"            \"track_time\": \"2018-01-15 13:25:13\"\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"is_lnglat_valid\": \"1\",\r\n" + 
				"            \"longitude\": \"118.070229\",\r\n" + 
				"            \"latitude\": \"24.576000\",\r\n" + 
				"            \"vehicle_status\": \"5\",\r\n" + 
				"            \"vehicle_alarm_status\": \"1\",\r\n" + 
				"            \"angle\": \"120\",\r\n" + 
				"            \"speed\": \"60\",\r\n" + 
				"            \"lock_status_info\": \"363534333231\",\r\n" + 
				"            \"track_time\": \"2018-01-15 13:25:14\"\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"is_lnglat_valid\": \"1\",\r\n" + 
				"            \"longitude\": \"118.070229\",\r\n" + 
				"            \"latitude\": \"24.576000\",\r\n" + 
				"            \"vehicle_status\": \"5\",\r\n" + 
				"            \"vehicle_alarm_status\": \"1\",\r\n" + 
				"            \"angle\": \"120\",\r\n" + 
				"            \"speed\": \"60\",\r\n" + 
				"            \"lock_status_info\": \"36353433323137\",\r\n" + 
				"            \"track_time\": \"2018-01-15 13:25:15\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		System.out.println(json);
		Map<String, Object> map=JSONUtil.parseJsonToMap(json);
		JSONArray jsonArray=(JSONArray) map.get("tracklist");
		List<Map<String, Object>> list=JSONUtil.parseJSONArray(jsonArray);
		System.out.println(list.size());
	}
}
