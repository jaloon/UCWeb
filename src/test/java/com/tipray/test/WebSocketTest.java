package com.tipray.test;

import com.tipray.bean.track.ReTrack;
import com.tipray.service.VehicleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
public class WebSocketTest {
	@Resource
	private VehicleService carService;

	@Test
	public void test1() throws Exception {
		ReTrack carTrack = new ReTrack();
		carTrack.setCarNumber("桂A12345");
		carTrack.setBegin("2017-12-11 08:06:50");
		carTrack.setEnd("2017-12-13 07:06:50");
		// List<ReTrack> list = carService.findTracks(carTrack);
		// Map<String, Object> map = new HashMap<>();
		// for (ReTrack track : list) {
		// 	map.put("biz", "track");
		// 	map.put("id", track.getId());
		// 	map.put("carNumber", track.getCarNumber());
		// 	map.put("longitude", track.getLongitude());
		// 	map.put("latitude", track.getLatitude());
		// 	map.put("velocity", track.getVelocity());
		// 	map.put("angle", track.getAngle());
		// 	map.put("carStatus", track.getCarStatus());
		// 	map.put("lockStatusInfo", track.getLockStatusInfo());
		// 	map.put("alarm", track.isAlarm());
		// 	String trackJson = JSONUtil.stringify(map);
		// 	String str = HttpRequestUtil.sendPost("http://192.168.3.225:8080/UCWeb/api/track", "track=" + trackJson);
		// 	System.out.println(str);
		// 	Thread.sleep(1000);
		// }
	}

	@Test
	public void test2() throws Exception {
		ReTrack carTrack = new ReTrack();
		carTrack.setCarNumber("桂B42133");
		carTrack.setBegin("2017-12-12 09:06:50");
		carTrack.setEnd("2017-12-12 17:06:58");
		// List<ReTrack> list = carService.findTracks(carTrack);
		// Map<String, Object> map = new HashMap<>();
		// for (ReTrack track : list) {
		// 	map.put("biz", "track");
		// 	map.put("id", track.getId());
		// 	map.put("carNumber", track.getCarNumber());
		// 	map.put("longitude", track.getLongitude());
		// 	map.put("latitude", track.getLatitude());
		// 	map.put("velocity", track.getVelocity());
		// 	map.put("angle", track.getAngle());
		// 	map.put("carStatus", track.getCarStatus());
		// 	map.put("lockStatusInfo", track.getLockStatusInfo());
		// 	map.put("alarm", track.isAlarm());
		// 	String trackJson = JSONUtil.stringify(map);
		// 	String str = HttpRequestUtil.sendPost("http://192.168.3.225:8080/UCWeb/api/track", "track=" + trackJson);
		// 	System.out.println(str);
		// 	Thread.sleep(1000);
		// }
	}

}
