package com.tipray.core.job;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tipray.util.CRCUtil;
import com.tipray.util.DateUtil;
import com.tipray.util.RC4Util;

/**
 * ExampleJob
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
//@Component("exampleJob")
public class ExampleJob {
	Logger log = LoggerFactory.getLogger(this.getClass());

	public void execute() {
		System.out.println(DateUtil.formatDate(new Date(), DateUtil.FORMAT_DATETIME) + "	ExampleJob execute");
		for(int i = 0; i<1000000;i++) {
			byte[] key = RC4Util.createBinaryKey();
			key = CRCUtil.addCrcToBytes(key);
			String data = RC4Util.createKey();
			data = RC4Util.rc4ToHexString(data, key);		
		}
	}
}
