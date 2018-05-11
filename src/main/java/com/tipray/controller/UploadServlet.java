package com.tipray.controller;

import java.io.PrintWriter;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tipray.core.exception.FileException;
import com.tipray.util.HttpServletUtil;

/**
 * 文件上传控制器
 * 
 * @author chenlong
 * @version 1.0 2018-02-01
 *
 */
@MultipartConfig(maxFileSize = 1024 * 1024 * 200)
@WebServlet(description = "文件上传控制器", urlPatterns = { "/api/upload" })
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(UploadServlet.class);

	public UploadServlet() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
			Part part = request.getPart("file");
			// tomcat实现的getSubmittedFileName方法有缺陷，文件名若包含路径，最后会把路径之间的"/"去掉，但是盘符未去除，导致保存到本地磁盘失败
			// String filename = part.getSubmittedFileName();
			String filename = HttpServletUtil.getUploadFileName(part);
			String suffix = filename.substring(filename.lastIndexOf(".")).toLowerCase();
			if (!".xls".equals(suffix) && !".xlsx".equals(suffix)) {
				throw new FileException(FileException.BAD_FILE_FORMAT_EXCEPTION);
			}
			String path = "d:/ftp/" + filename;
			part.write(path);
			logger.error("文件：{}，上传成功！", filename);
		} catch (Exception e) {
			if (e instanceof IllegalStateException) {
				logger.error("文件大小超过限制！\n{}", e.toString());
				out.write("----");
			} else if (e instanceof FileException) {
				logger.error("文件格式错误！\n{}", e.toString());
				out.write("----");
			} else {
				e.printStackTrace();
			}
		}
	}

}
