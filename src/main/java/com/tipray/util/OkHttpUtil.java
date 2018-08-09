package com.tipray.util;

import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp库工具封装
 *
 * @author chenlong
 * @version 1.0 2018-07-18
 */
public class OkHttpUtil {
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    public static final MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain; charset=utf-8");
    public static final MediaType MEDIA_TYPE_FORM = MediaType.parse("text/application/x-www-form-urlencoded; charset=utf-8");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_FILE = MediaType.parse("file/*; charset=utf-8");

    private static OkHttpClient okHttpClient;

    static {
        X509TrustManager trustManager = getX509TrustManager();
        // 初始化OkHttpClient
        okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true) // 当连接失败，尝试重连
                .connectTimeout(60, TimeUnit.SECONDS) // 连接超时
                .readTimeout(60, TimeUnit.SECONDS) // 读取超时
                .writeTimeout(60, TimeUnit.SECONDS) // 写入超时
                .sslSocketFactory(getSSLSocketFactory(trustManager), trustManager) // SSLSocketFactory
                .hostnameVerifier((hostname, sslSession) -> true) // 服务器主机验证，忽略（即全部通过验证）
                .authenticator(getAuthenticator()) // Basic Auth(HTTP Auth)
                .addInterceptor(getRedirectInterceptor()) // 重定向拦截器
                .cookieJar(getStoreCookieJar()) // Cookie管理
                .build();
    }

    /**
     * 获取OkHttpClient
     *
     * @return {@link OkHttpClient}
     */
    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    /**
     * 获取SSLSocketFactory
     *
     * @param trustManagers {@link X509TrustManager} 不定参数，证书信任管理器
     * @return {@link SSLSocketFactory}
     */
    public static SSLSocketFactory getSSLSocketFactory(X509TrustManager... trustManagers) {
        try {
            SSLContext sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(null, trustManagers, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslcontext.getSocketFactory();
            return sslSocketFactory;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取证书信任管理器
     *
     * @return {@link X509TrustManager}
     */
    public static X509TrustManager getX509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    /**
     * 获取基础认证管理器
     *
     * @return {@link Authenticator}
     */
    public static Authenticator getAuthenticator() {
        return new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic("pltone", "pltone");
                if (credential.equals(response.request().header("Authorization"))) {
                    return null; // If we already failed with these credentials, don't retry.
                }
                if (responseCount(response) >= 3) {
                    return null; // If we've failed 3 times, give up. - in real life, never give up!!
                }
                return response.request().newBuilder().header("Authorization", credential).build();
            }

            /**
             * 认证失败重试次数
             *
             * @param response {@link Response}
             * @return {@link Integer} 重试次数
             */
            private int responseCount(Response response) {
                int result = 1;
                while ((response = response.priorResponse()) != null) {
                    result++;
                }
                return result;
            }
        };
    }

    /**
     * 获取重定向拦截器
     * <p>
     * okhttp重定向存在两个缺陷：<br>
     * 1.okhttp处理301,302重定向时，会把请求方式设置为GET，这样会丢失原来Post请求中的参数。<br>
     * 2.okhttp默认不支持跨协议的重定向，比如http重定向到https
     * </p>
     *
     * @return {@link Interceptor} 重定向拦截器
     */
    public static Interceptor getRedirectInterceptor() {
        return chain -> {
            Request request = chain.request();
            HttpUrl beforeUrl = request.url();
            Response response = chain.proceed(request);
            HttpUrl afterUrl = response.request().url();
            //1.根据url判断是否是重定向
            if (!beforeUrl.equals(afterUrl)) {
                //处理两种情况 1、跨协议 2、原先不是GET请求。
                if (!beforeUrl.scheme().equals(afterUrl.scheme()) || !request.method().equals("GET")) {
                    //重新请求
                    Request newRequest = request.newBuilder().url(afterUrl).build();
                    response = chain.proceed(newRequest);
                }
            }
            return response;
        };
    }

    /**
     * 获取Cookie管理器
     *
     * @return {@link CookieJar}
     */
    public static CookieJar getStoreCookieJar() {
        return new CookieJar() {
            private final Map<String, List<Cookie>> COOKIE_STORE = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                COOKIE_STORE.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = COOKIE_STORE.get(url.host());
                return cookies != null ? cookies : Collections.emptyList();
            }
        };
    }

    /**
     * 执行请求
     *
     * @param request {@link Request}
     * @return {@link String} 应答结果
     * @throws IOException
     */
    public static String execute(Request request) throws IOException {
        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return response.body().string();
    }

    /**
     * GET请求
     *
     * @param url {@link String} 请求路径
     * @return {@link String} 应答结果
     * @throws IOException
     */
    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .header("X-Requested-With", "XMLHttpRequest")
                .header("Connection", "close")
                .url(url).build();
        return execute(request);
    }

    /**
     * GET请求
     *
     * @param url    {@link String} 请求路径
     * @param params {@link String} 请求参数
     * @return {@link String} 应答结果
     * @throws IOException
     */
    public static String get(String url, String params) throws IOException {
        if (params != null && !params.trim().isEmpty()) {
            url = new StringBuffer().append(url).append('?').append(params).toString();
        }
        return get(url);
    }

    /**
     * GET请求
     *
     * @param url    {@link String} 请求路径
     * @param params {@link Map} 请求参数
     * @return {@link String} 应答结果
     * @throws IOException
     */
    public static String get(String url, Map<String, String> params) throws IOException {
        if (params != null && !params.isEmpty()) {
            StringBuffer urlBuf = new StringBuffer();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuf.append('&')
                        .append(entry.getKey())
                        .append('=')
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            urlBuf.deleteCharAt(0);
            urlBuf.insert(0, '?').insert(0, url);
            url = urlBuf.toString();
        }
        return get(url);
    }

    /**
     * POST请求
     *
     * @param url         {@link String} 请求路径
     * @param requestBody {@link RequestBody} 请求体
     * @return {@link String} 应答结果
     * @throws IOException
     */
    public static String post(String url, RequestBody requestBody) throws IOException {
        Request request = new Request.Builder()
                .header("X-Requested-With", "XMLHttpRequest")
                .header("Connection", "close")
                .url(url).post(requestBody).build();
        return execute(request);
    }

    /**
     * POST请求（上传文本）
     *
     * @param url  {@link String} 请求路径
     * @param body {@link String} 请求体文本
     * @return {@link String} 应答结果
     * @throws IOException
     */
    public static String post(String url, String body) throws IOException {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, body);
        return post(url, requestBody);
    }

    /**
     * POST请求（上传文件）
     *
     * @param url  {@link String} 请求路径
     * @param file {@link File} 上传文件
     * @return {@link String} 应答结果
     * @throws IOException
     */
    public static String post(String url, File file) throws IOException {
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_FILE, file);
        return post(url, requestBody);
    }

    /**
     * POST请求
     *
     * @param url  {@link String} 请求路径
     * @param form {@link Map} 表单数据
     * @return {@link String} 应答结果
     * @throws IOException
     */
    public static String post(String url, Map<String, String> form) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder(StandardCharsets.UTF_8);
        for (Map.Entry<String, String> entry : form.entrySet()) {
            formBuilder.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBody = formBuilder.build();
        return post(url, requestBody);
    }

    /**
     * 文件上传
     *
     * @param url  {@link String} 上传路径
     * @param name {@link String} 参数名称
     * @param file {@link File} 上传文件
     * @return {@link String} 上传结果
     * @throws IOException
     */
    public static String upload(String url, String name, File file) throws IOException {
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(name, file.getName(), RequestBody.create(MEDIA_TYPE_FILE, file))
                .build();
        return post(url, multipartBody);
    }

    /**
     * 文件下载
     *
     * @param url {@link String} 下载路径
     * @param url {@link String} 存储路径
     * @throws IOException
     */
    public static void download(String url, String path) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        storeDownloadFile(response, path);
    }

    /**
     * 存储下载文件
     *
     * @param response {@link Response}
     * @param path     {@link String} 存储路径
     * @throws IOException
     */
    private static void storeDownloadFile(Response response, String path) throws IOException {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = response.body().byteStream();
            if (inputStream == null) {
                throw new IOException("下载的文件数据流为空！");
            }
            int len;
            byte[] buf = new byte[2048];
            String fileName = parseDownloadFilename(response);
            File file = new File(path, fileName);
            fileOutputStream = new FileOutputStream(file);
            while ((len = inputStream.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, len);
            }
            fileOutputStream.flush();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                }
                fileOutputStream = null;
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
                inputStream = null;
            }
        }
    }

    /**
     * 获取下载文件名
     *
     * @param response {@link Response}
     * @return {@link String} 文件名
     * @throws IOException
     */
    private static String parseDownloadFilename(Response response) throws IOException {
        String header = response.header("Content-Disposition");
        // 通过Content-Disposition获取文件名，这点跟服务器有关，需要灵活变通
        if (header != null && !header.trim().isEmpty()) {
            int index = header.indexOf("filename=");
            if (index > -1) {
                // 解码文件名
                String filename = URLDecoder.decode(header.substring(index + 9), "UTF-8");
                // 有些文件名会被包含在""里面，所以要去掉，不然无法读取文件后缀
                filename = filename.replaceAll("\"", "");
                if (!filename.trim().isEmpty()) {
                    return filename;
                }
            }
        }
        // 通过截取URL来获取文件名
        HttpUrl downloadUrl = response.request().url(); // 获得实际下载文件的URL
        return downloadUrl.pathSegments().get(downloadUrl.pathSize() - 1);
    }
}