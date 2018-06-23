package com.tipray.ftp;

import org.apache.commons.net.ftp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.TimeZone;

/**
 * ftp客户端
 *
 * @author chenlong
 * @version 1.0 2018-01-26
 */
public class FtpClient {
    private static final Logger logger = LoggerFactory.getLogger(FtpClient.class);
    private FTPClient ftpClient;
    private String ip;
    private int port;
    private String account;
    private String password;

    /**
     * FtpClient构造方法
     *
     * @param ip       IP地址
     * @param port     端口号
     * @param account  账户
     * @param password 密码
     */
    public FtpClient(String ip, int port, String account, String password) {
        this.ip = ip;
        this.port = port;
        this.account = account;
        this.password = password;
        this.ftpClient = new FTPClient();
    }

    /**
     * 判断是否登入成功
     *
     * @return
     */
    public boolean login() {
        boolean isLogin = false;
        FTPClientConfig ftpClientConfig = new FTPClientConfig();
        ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
        this.ftpClient.setControlEncoding("UTF-8");
        this.ftpClient.configure(ftpClientConfig);
        try {
            if (this.port > 0) {
                this.ftpClient.connect(this.ip, this.port);
            } else {
                this.ftpClient.connect(this.ip);
            }
            // FTP服务器连接回答
            int reply = this.ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                this.ftpClient.disconnect();
                logger.error("登录FTP服务失败！");
                return isLogin;
            }
            this.ftpClient.login(this.account, this.password);
            // 设置传输协议
            this.ftpClient.enterLocalPassiveMode();
            this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            logger.info("恭喜{}成功登陆FTP服务器", this.account);
            isLogin = true;
        } catch (Exception e) {
            logger.error("{}登录FTP服务失败！\n{}", this.account, e.toString());
        }
        this.ftpClient.setBufferSize(1024 * 1024);
        this.ftpClient.setDataTimeout(30 * 1000);
        return isLogin;
    }

    /**
     * 退出关闭服务器链接
     */
    public void logout() {
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                // 退出FTP服务器
                boolean reuslt = this.ftpClient.logout();
                if (reuslt) {
                    logger.info("成功退出服务器");
                }
            } catch (IOException e) {
                logger.warn("退出FTP服务器异常！\n{}", e.toString());
            } finally {
                try {
                    // 关闭FTP服务器的连接
                    this.ftpClient.disconnect();
                } catch (IOException e) {
                    logger.warn("关闭FTP服务器的连接异常！\n{}", e.toString());
                }
            }
        }
    }

    /***
     * 上传Ftp文件
     *
     * @param localFile
     *            本地文件
     * @param remoteUpLoadePath
     *            上传服务器路径
     */
    public boolean uploadFile(File localFile, String remoteUpLoadePath) {
        BufferedInputStream inStream = null;
        boolean success = false;
        try {
            String filename = new String(localFile.getName().getBytes("UTF-8"), "ISO-8859-1");
            // 改变工作路径
            this.ftpClient.changeWorkingDirectory(remoteUpLoadePath);
            inStream = new BufferedInputStream(new FileInputStream(localFile));
            logger.info("{}开始上传.....", localFile.getName());
            success = this.ftpClient.storeFile(filename, inStream);
            if (success) {
                logger.info("{}上传成功！", localFile.getName());
                return success;
            } else {
                logger.warn("{}上传失败，请查证用户是否有上传权限！", localFile.getName());
            }
        } catch (FileNotFoundException e) {
            logger.error("{}未找到！\n{}", localFile.getPath(), e.toString());
        } catch (IOException e) {
            logger.error("{}上传失败！\n{}", localFile.getName(), e.toString());
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    logger.error("BufferedInputStream close error!\n{}", e.toString());
                }
            }
        }
        return success;
    }

    /***
     * 下载文件
     *
     * @param remoteFileName
     *            待下载文件名称
     * @param localPath
     *            下载到本地哪个路径下 - 应该以/结束
     * @param remoteDownLoadPath
     *            待下载文件所在的路径
     */

    public boolean downloadFile(String remoteFileName, String localPath, String remoteDownLoadPath) {
        String localFile = localPath + remoteFileName;
        BufferedOutputStream outStream = null;
        boolean success = false;
        try {
            this.ftpClient.changeWorkingDirectory(remoteDownLoadPath);
            outStream = new BufferedOutputStream(new FileOutputStream(localFile));
            logger.info("{}开始下载....", remoteFileName);
            success = this.ftpClient.retrieveFile(remoteFileName, outStream);
            if (success == true) {
                logger.info("{}成功下载到{}", remoteFileName, localPath);
                return success;
            }
        } catch (Exception e) {
            logger.error("{}下载失败！\n{}", remoteFileName, e.toString());
        } finally {
            if (null != outStream) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    logger.error("BufferedOutputStream close error!\n{}", e.toString());
                }
            }
        }
        if (!success) {
            logger.error("{}下载失败!!!", remoteFileName);
        }
        return success;
    }

    /***
     * 上传文件夹
     *
     * @param localDirectory
     *            本地文件夹
     * @param remoteDirectoryPath
     *            Ftp 服务器路径 以目录"/"结束
     */
    public boolean uploadDirectory(String localDirectory, String remoteDirectoryPath) {
        File src = new File(localDirectory);
        try {
            remoteDirectoryPath = remoteDirectoryPath + src.getName() + "/";
            this.ftpClient.makeDirectory(remoteDirectoryPath);
            // ftpClient.listDirectories();
        } catch (IOException e) {
            logger.error("{}目录创建失败：\n{}", remoteDirectoryPath, e.toString());
        }
        File[] allFile = src.listFiles();
        for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
            if (!allFile[currentFile].isDirectory()) {
                String srcName = allFile[currentFile].getPath();
                uploadFile(new File(srcName), remoteDirectoryPath);
            }
        }
        for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
            if (allFile[currentFile].isDirectory()) {
                // 递归
                uploadDirectory(allFile[currentFile].getPath(), remoteDirectoryPath);
            }
        }
        return true;
    }

    /**
     * 下载文件夹
     *
     * @param localDirectoryPath 本地地址
     * @param remoteDirectory    远程文件夹
     */
    public boolean downLoadDirectory(String localDirectoryPath, String remoteDirectory) {
        try {
            String fileName = new File(remoteDirectory).getName();
            localDirectoryPath = localDirectoryPath + fileName + "//";
            new File(localDirectoryPath).mkdirs();
            FTPFile[] allFile = this.ftpClient.listFiles(remoteDirectory);
            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
                if (!allFile[currentFile].isDirectory()) {
                    downloadFile(allFile[currentFile].getName(), localDirectoryPath, remoteDirectory);
                }
            }
            for (int currentFile = 0; currentFile < allFile.length; currentFile++) {
                if (allFile[currentFile].isDirectory()) {
                    String strremoteDirectoryPath = remoteDirectory + "/" + allFile[currentFile].getName();
                    downLoadDirectory(localDirectoryPath, strremoteDirectoryPath);
                }
            }
        } catch (IOException e) {
            logger.error("下载文件夹失败：\n{}", e.toString());
            return false;
        }
        return true;
    }

    // FtpClient的Set 和 Get 函数
    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

}
