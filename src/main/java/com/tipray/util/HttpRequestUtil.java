package com.tipray.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * java操作http请求工具类
 * 
 * @author chenlong
 *
 */
public class HttpRequestUtil {
	private static final Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);
	public static final String GET = "get";
	public static final String POST = "post";
	public static final String PARAM_MAP = "map";
	public static final String PARAM_STR = "str";

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param params
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 * @throws IOException 
	 */
	public static String sendGet(String url, String params) throws IOException {
		BufferedReader in = null;// 读取响应输入流
		try {
			params = encodeParam(params, PARAM_STR);
			return setHttpRequest(url, params, GET, in, null);
		} finally {// 使用finally块来关闭输入流
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				logger.error("关闭输入流异常！\n{}", e.toString());
			}
		}
	}

	/**
	 * 发送GET请求
	 *
	 * @param url
	 *            目的地址
	 * @param parameters
	 *            请求参数，Map类型。
	 * @return 远程响应结果
	 * @throws IOException
	 */
	public static String sendGet(String url, Map<String, String> parameters) throws IOException {
		BufferedReader in = null;// 读取响应输入流
		String params = "";// 编码之后的参数
		try {
			params = encodeParam(parameters, PARAM_MAP);
			return setHttpRequest(url, params, GET, in, null);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				logger.error("关闭输入流异常！\n{}", e.toString());
			}
		}
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 *
	 * @param url
	 *            发送请求的 URL
	 * @param params
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 * @throws IOException
	 */
	public static String sendPost(String url, String params) throws IOException {
		BufferedReader in = null;// 读取响应输入流
		PrintWriter out = null;
		try {
			params = encodeParam(params, PARAM_STR);
			return setHttpRequest(url, params, POST, in, out);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				logger.error("关闭输入流异常！\n{}", e.toString());
			} finally {
				if (out != null) {
					out.close();
				}
			}
		}
	}

	/**
	 * 发送POST请求
	 *
	 * @param url
	 *            目的地址
	 * @param parameters
	 *            请求参数，Map类型。
	 * @return 远程响应结果
	 * @throws IOException
	 */
	public static String sendPost(String url, Map<String, String> parameters) throws IOException {
		BufferedReader in = null;// 读取响应输入流
		PrintWriter out = null;
		String params = "";// 编码之后的参数
		try {
			params = encodeParam(parameters, PARAM_MAP);
			return setHttpRequest(url, params, POST, in, out);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				logger.error("关闭输入流异常！\n{}", e.toString());
			} finally {
				if (out != null) {
					out.close();
				}
			}
		}
	}

	/**
	 * 获取Get请求的参数
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestParametersForGet(HttpServletRequest request) {
		return request.getQueryString();
	}

	/**
	 * 获取http请求的参数
	 *
	 * @param request
	 * @return
	 */
	public static String getRequestParameters(HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		StringBuffer stringBuffer = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (String key : params.keySet()) {
				for (String value : params.get(key)) {
					stringBuffer.append(key).append('=').append(value).append('&');
				}
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1);
		}
		return stringBuffer.toString();
	}

	/**
	 * 获取Get请求的完整路径
	 *
	 * @param request
	 * @return
	 */
	public static String getRequestUrlForGet(HttpServletRequest request) {
		String queryStr = request.getQueryString();
		String requestUrl = request.getRequestURI();
		if (StringUtil.isNotEmpty(queryStr)) {
			requestUrl = new StringBuffer(requestUrl).append('?').append(queryStr).toString();
		}
		return requestUrl;
	}

	/**
	 * 获取http请求的完整路径
	 *
	 * @param request
	 * @return
	 */
	public static String getRequestUrl(HttpServletRequest request) {
		String queryStr = getRequestParameters(request);
		String requestUrl = request.getRequestURI();
		if (StringUtil.isNotEmpty(queryStr)) {
			requestUrl = new StringBuffer(requestUrl).append('?').append(queryStr).toString();
		}
		return requestUrl;
	}

	/**
	 * 参数utf-8编码，防止中文乱码
	 * 
	 * @param params
	 *            参数
	 * @param paramMode
	 *            参数形式：<code>PARAM_STR</code> 字符串形式，<code>PARAM_MAP</code> Map形式
	 * @return
	 * @throws IOException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static String encodeParam(Object params, String paramMode) throws IOException   {
		if (params == null) {
			return "";
		} else {
			StringBuffer paramBuf = new StringBuffer();// 处理请求参数
			// 编码请求参数
			switch (paramMode) {
			case PARAM_STR:
				String[] objs = ((String) params).split("&");
				for (String param : objs) {
					int i = param.indexOf('=');
					String str1 = param.substring(0, i);
					String str2 = param.substring(i + 1, param.length());
					paramBuf.append(str1).append('=').append(URLEncoder.encode(str2, "UTF-8")).append('&');
				}
				break;
			case PARAM_MAP:
				Map<String, String> parameters = (Map) params;
				for (String name : parameters.keySet()) {
					paramBuf.append(name).append('=').append(URLEncoder.encode(parameters.get(name), "UTF-8")).append('&');
				}
				break;
			default:
				break;
			}
			paramBuf.deleteCharAt(paramBuf.length() - 1);
			return paramBuf.toString();
		}
	}

	/**
	 * 设置http请求
	 * 
	 * @param url
	 *            请求路径
	 * @param params
	 *            请求参数
	 * @param httpRequestMode
	 *            请求方式：<code>GET</code>，<code>POST</code>
	 * @param in
	 *            读取响应输入流
	 * @param out
	 *            POST请求获取HttpURLConnection对象的输出流
	 * @return 请求结果字符串
	 * @throws IOException 
	 */
	private static String setHttpRequest(String url, String params, String httpRequestMode, BufferedReader in,
			PrintWriter out) throws IOException {
		if (httpRequestMode.equals(GET)) {
			url = new StringBuffer(url).append('?').append(params).toString();
		}
		// 创建URL对象
		URL connURL = new URL(url);
		// 打开URL连接
		HttpURLConnection httpConn = (HttpURLConnection) connURL.openConnection();
		// 设置通用属性
		httpConn.setRequestProperty("Accept", "*/*");
		httpConn.setRequestProperty("Connection", "Keep-Alive");
		httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
		switch (httpRequestMode) {
		case GET:
			// 建立实际的连接
			httpConn.connect();
			// 响应头部获取
			Map<String, List<String>> headers = httpConn.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : headers.keySet()) {
				System.out.println(key + "\t：\t" + headers.get(key));
			}
			break;
		case POST:
			// 设置POST方式，发送POST请求必须设置如下两行
			httpConn.setDoInput(true);
			httpConn.setDoOutput(true);
			// 获取HttpURLConnection对象对应的输出流
			out = new PrintWriter(httpConn.getOutputStream());
			// 发送请求参数
			out.write(params);
			// flush输出流的缓冲
			out.flush();
			break;
		default:
			break;
		}
		// 定义BufferedReader输入流来读取URL的响应，设置编码方式
		in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), StandardCharsets.UTF_8));
		String line;
		StringBuffer resultBuf = new StringBuffer();// 返回的结果
		// 读取返回的内容
		while ((line = in.readLine()) != null) {
			resultBuf.append(line);
		}
		return resultBuf.toString();
	}

	public void test() throws Exception {
        TrustManager[] myTMs = new TrustManager[] { new MyX509TrustManager() };
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, myTMs, null);

        SSLSocketFactory sslSocketFactory = ctx.getSocketFactory();

    }

}
