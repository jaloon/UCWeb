package com.tipray.webservice.impl;

import java.io.File;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.xml.ws.WebServiceContext;

import org.springframework.stereotype.Component;

import com.tipray.webservice.ExampleRestService;

/**
 * ExampleRestServiceImpl
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Component("exampleRestService")
public class ExampleRestServiceImpl implements ExampleRestService {
	@Resource
	private WebServiceContext context;

	@Override
	public String getInfo(String params) {
		System.out.println(params);
		return "success";
	}

	@Override
	public Response downloadFile(String params) {
		String file = "test";
		String path = null;
		File downloadFile = new File(path);
		if (downloadFile.exists()) {
			return Response.ok(downloadFile).header("Content-Disposition", "attachment; filename=" + file)
					.header("Content-Length", Long.toString(downloadFile.length())).build();
		}
		return null;
	}

	@Override
	public void uploadFile(String params, File file) {
		
	}
}
