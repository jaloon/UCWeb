package com.tipray.util;

import org.apache.commons.fileupload.ParameterParser;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Locale;
import java.util.Map;

/**
 * Web请求的处理工具类
 *
 * @author chenlong
 * @version 1.0 2018-02-02
 */
public class HttpServletUtil {
    /**
     * 获取浏览器IP
     *
     * @param request {@link HttpServletRequest}
     * @return 浏览器IP
     */
    public static String getHost(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    /**
     * 是否HTTPS连接
     *
     * @param request {@link HttpServletRequest}
     * @return
     */
    public static boolean isHttps(HttpServletRequest request) {
        String schem = request.getScheme();
        if (schem == null || schem.trim().isEmpty()) {
            return false;
        }
        return schem.equalsIgnoreCase("https");
    }

    /**
     * 获取上传文件的文件名
     *
     * @param part {@link Part}
     * @return 上传文件的文件名
     */
    public static String getUploadFileName(Part part) {
        String fileName = null;
        String header = part.getHeader("Content-Disposition");
        if (header != null) {
            String lowerHeader = header.toLowerCase(Locale.ENGLISH);
            if (lowerHeader.startsWith("form-data") || lowerHeader.startsWith("attachment")) {
                ParameterParser paramParser = new ParameterParser();
                paramParser.setLowerCaseNames(true);
                // Parameter parser can handle null input
                Map<String, String> params = paramParser.parse(header, ';');
                if (params.containsKey("filename")) {
                    fileName = params.get("filename");
                    if (fileName != null) {
                        fileName = fileName.trim();
                        // 参考spring中CommonsMultipartFile#getOriginalFilename()方法改进tomcat对包含路径的文件名处理
                        //（win10带的IE11上传文件会带上路径），此处将路径部分截取掉，只要文件名
                        // Check for Unix-style path
                        int unixSep = fileName.lastIndexOf("/");
                        // Check for Windows-style path
                        int winSep = fileName.lastIndexOf("\\");
                        // Cut off at latest possible point
                        int pos = (winSep > unixSep ? winSep : unixSep);
                        if (pos != -1) {
                            // Any sort of path separator found...
                            return fileName.substring(pos + 1);
                        } else {
                            // A plain name
                            return fileName;
                        }
                    } else {
                        // Even if there is no value, the parameter is present,
                        // so we return an empty file name rather than no file name.
                        fileName = "";
                    }
                }
            }
        }
        return fileName;
    }

    /**
     * 输出文件供下载
     *
     * @param response {@link HttpServletResponse}
     * @param filePath 文件路径名称
     * @throws IOException
     */
    public static void outputFile(HttpServletResponse response, String filePath) throws IOException {
        File file = new File(filePath);
        outputFile(response, file);
    }

    /**
     * 输出文件供下载
     *
     * @param response {@link HttpServletResponse}
     * @param file     {@link File}
     * @throws IOException
     */
    public static void outputFile(HttpServletResponse response, File file) throws IOException {
        OutputStream out = null;
        try {
            response.reset();
            String fileName = file.getName();// 得到文件名
            // 把文件名按UTF-8取出并按ISO8859-1编码，保证弹出窗口中的文件名中文不乱码，中文不要太多，最多支持17个中文，因为header有150个字节限制。
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            response.setContentType("application/octet-stream; charset=utf-8");// 告诉浏览器输出内容为流
            // Content-Disposition中指定的类型是文件的扩展名，并且弹出的下载对话框中的文件类型是按照文件的扩展名显示的，点保存后，
            // 文件以filename的值命名，保存类型以Content中设置的为准。注意：在设置Content-Disposition头字段之前，一定要设置Content-Type头字段。
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setHeader("Content-Length", file.length() + "");// 设置内容长度
            out = response.getOutputStream();
            out.write(FileUtils.readFileToByteArray(file));
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * springMVC下载
     *
     * @param file {@link File}
     * @return {@link ResponseEntity}
     * @throws IOException
     */
    public static ResponseEntity<byte[]> outputForSpringMVC(File file) throws IOException {
        if (file == null || !file.exists()) {
            return null;
        }
        String fileName = file.getName();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        if (isMSBrowser(request)) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
    }

    /**
     * 判断是否是IE浏览器
     *
     * @param request {@link HttpServletRequest}
     * @return
     */
    public static boolean isMSBrowser(HttpServletRequest request) {
        String[] iEBrowserSignals = {"MSIE", "Trident", "Edge"};
        String userAgent = request.getHeader("User-Agent");
        for (String signal : iEBrowserSignals) {
            if (userAgent.contains(signal)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 信任所有主机
     *
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    public static void setDefaultSSLTrustAllHosts() throws KeyManagementException, NoSuchAlgorithmException {
        // 覆盖java默认的证书验证
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        }};
        // 设置不验证主机
        HostnameVerifier doNotVerify = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(doNotVerify);
    }
}
