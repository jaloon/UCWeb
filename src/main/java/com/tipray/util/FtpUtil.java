package com.tipray.util;

import java.io.File;

import com.tipray.constant.CenterConfigConst;
import com.tipray.ftp.FtpClient;

public class FtpUtil {
	/** ftp客户端 */
	public static final FtpClient FTP_CLIENT = new FtpClient(
														CenterConfigConst.FTP_HOST,
														CenterConfigConst.FTP_PORT,
														CenterConfigConst.FTP_ACOUNT,
														CenterConfigConst.FTP_PASSWORD);

	/**
	 * ftp上传文件
	 * 
	 * @param localFile
	 *            本地文件
	 */
	public static void upload(File localFile) {
		if (FTP_CLIENT.login()) {
			FTP_CLIENT.uploadFile(localFile, CenterConfigConst.FTP_UPLOAD_PATH);
			FTP_CLIENT.logout();
		}
	}
}
