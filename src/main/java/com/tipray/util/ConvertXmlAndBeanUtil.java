package com.tipray.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URLDecoder;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;
/**
 * 转换xml 与  bean
 * @author chends
 *
 */
public class ConvertXmlAndBeanUtil {

	@SuppressWarnings("rawtypes")
	public static Object xmlToBean(String xml, Class className, String map)
			throws MarshalException,
			ValidationException, MappingException, IOException{
		if (xml.endsWith(".xml")) {
			String xmlPath = ConvertXmlAndBeanUtil.class.getResource("/").getPath() + xml;
			InputStreamReader inputStreamReader = new InputStreamReader(
					new FileInputStream(URLDecoder.decode(xmlPath,"utf-8")), "UTF-8");
			return getBeanByInputStream(className, map, inputStreamReader);

		} else {
			StringReader stringReader = new StringReader(xml);
			return Unmarshaller.unmarshal(className, stringReader);
		}
	}

	@SuppressWarnings("rawtypes")
	public static Object getBeanByInputStream(Class className, String map, InputStreamReader inputStreamReader)
			throws MappingException, IOException, MarshalException, ValidationException{
			XMLContext context = new XMLContext();
			Mapping mapping = context.createMapping();
			mapping.loadMapping(map);
			context.addMapping(mapping);
			Unmarshaller unmarshaller = new Unmarshaller(mapping);
			unmarshaller.setClass(className);
			return unmarshaller.unmarshal(inputStreamReader);
	}

	public static boolean beanToXml(Object object, String pathName, String map) throws IOException, MappingException, MarshalException, ValidationException {
		Mapping mapping = new Mapping();
		mapping.loadMapping(map);
		if (pathName.endsWith(".xml")) {
			File file = new File(pathName);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					fileOutputStream, "UTF-8");
			Marshaller marshaller = new Marshaller(outputStreamWriter);
			marshaller.setSuppressXSIType(true);
			marshaller.setMapping(mapping);
			marshaller.marshal(object);
			return true;
		} else {
			return false;
		}
	}
}
