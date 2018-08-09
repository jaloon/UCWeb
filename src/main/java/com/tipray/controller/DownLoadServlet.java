package com.tipray.controller;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件下载控制器
 * 
 * @author chenlong
 * @version 1.0 2018-02-02
 *
 */
// @WebServlet(description = "文件下载控制器", urlPatterns = { "/downloadUpgradeFile" })
public class DownLoadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public DownLoadServlet() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		//1.获取要下载的文件的绝对路径
        String realPath = this.getServletContext().getRealPath("/load/ceshi.txt");
        //2.获取要下载的文件名
        String fileName = realPath.substring(realPath.lastIndexOf("\\")+1);
        String userAgent = request.getHeader("User-Agent");
        //针对IE或者以IE为内核的浏览器：
        if (userAgent.contains("MSIE")||userAgent.contains("Trident")) {
            fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
        } else {
        //非IE浏览器的处理：
            fileName = new String(fileName.getBytes("UTF-8"),"ISO-8859-1");
        }
        //3.设置content-disposition响应头控制浏览器以下载的方式打开文件
        response.setHeader("content-disposition","attachment;filename="+fileName);
        //4.获取要下载的文件输入流
        InputStream in = new FileInputStream(realPath);

        int len = 0;
        //5.创建书缓冲区
        byte[] buffer = new byte[1024];
        //6.通过response对象获取OutputStream输出流对象
        OutputStream os = response.getOutputStream();
        //7.将FileInputStream流对象写入到buffer缓冲区
        while((len=in.read(buffer))>0){
            os.write(buffer,0,len);
        }
        //8.关闭流
        in.close();
        os.close();
	}

}
