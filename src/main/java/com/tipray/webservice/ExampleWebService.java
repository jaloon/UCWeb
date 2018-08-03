package com.tipray.webservice;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.soap.MTOM;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Component
@Transactional
@WebService(name = "WSExample", serviceName = "WSExample", targetNamespace = "http://ws.TRWeb.com")
@MTOM  //开启MTOM功能
public class ExampleWebService {
	@Resource
	private WebServiceContext context;
	
	@WebMethod
	public String getInfo(
			@WebParam(name = "params") String params) throws Exception {
		System.out.println(params);
		return "success" ;
	}
	
	@WebMethod
	@WebResult
	@XmlMimeType("*/*")
	public DataHandler downloadFile(
			@WebParam(name = "file") String filePath) throws Exception {
		File downloadFile = new File(filePath);
		if(downloadFile.exists()){
			return new DataHandler(new FileDataSource(downloadFile) {
				@Override
				public String getContentType() {
					return "application/octet-stream";
				}
			});
		}
		return null;
	}
	
	@WebMethod
	public void uploadFile(
			@WebParam(name = "fileName") String fileName,
			@XmlMimeType("*/*") @WebParam(name = "dataHandler") DataHandler dataHandler)
			throws Exception {
		OutputStream out = null;
		try {
			File file = new File("输出文件路径");
			if(!file.exists()){
				out = new FileOutputStream(file);
				dataHandler.writeTo(out);
				out.flush();
			}
		} finally{
			if(out!=null) out.close();
		}
	}
}
