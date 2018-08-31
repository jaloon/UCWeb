package com.tipray.webservice.client;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.URL;

/**
 * ElockClient
 * 
 * @author chenlong
 * @version 1.0 2018-01-19
 *
 */
public class ElockClient {
	/**
	 * 推送物流信息
	 * 
	 * @param wsAaddr
	 *            {@link String} WebService服务器地址
	 * @param txt
	 *            {@link String} 物流信息XML文本
	 * @return
	 * @throws IOException
	 */
	public static String setPlan(String wsAaddr, String txt) throws IOException {
		String wsdl = new StringBuffer(wsAaddr).append("?wsdl").toString();
		// 创建WSDL的URL，注意不是服务地址
		URL url = new URL(wsdl);
		// 创建服务名称
		// 1.namespaceURI - 命名空间地址 (wsdl文档中的targetNamespace)
		// 2.localPart - 服务视图名 (wsdl文档中服务名称，例如<wsdl:service name="Elock">)
		QName qname = new QName("http://www.cnpc.com/", "Elock");
		// 创建服务视图
		// 参数解释：
		// 1.wsdlDocumentLocation - wsdl地址
		// 2.serviceName - 服务名称
		Service service = Service.create(url, qname);
		// 获取服务实现类
		// 参数解释:serviceEndpointInterface - 服务端口(wsdl文档中服务端口的name属性，
		// 例如<wsdl:port name="ElockSoap" binding="tns:ElockSoap">)
		ElockSoap elockSoap = service.getPort(ElockSoap.class);
		// 调用查询方法
		String result = elockSoap.setPlan(txt);
		return result;
	}

	private ElockClient() {
	}
}
