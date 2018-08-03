package com.tipray.util;

import com.tipray.bean.upgrade.TerminalUpgradeFile;
import com.tipray.constant.CenterConst;
import com.tipray.constant.TerminalUpgradeConst;
import com.tipray.ftp.FtpClient;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FtpUtil {
    /**
     * ftp客户端
     */
    public static final FtpClient FTP_CLIENT = new FtpClient(
            CenterConst.FTP_HOST,
            CenterConst.FTP_PORT,
            CenterConst.FTP_ACOUNT,
            CenterConst.FTP_PASSWORD);

    /**
     * ftp上传文件
     *
     * @param localFile 本地文件
     */
    public static void upload(File localFile) {
        if (FTP_CLIENT.login()) {
            FTP_CLIENT.uploadFile(localFile, CenterConst.FTP_UPLOAD_PATH);
            FTP_CLIENT.logout();
        }
    }

    /**
     * 打开到FTP服务器指定文件路径的连接并返回一个用于从该连接读入的 InputStream
     *
     * @param ftpUrl ftp文件路径
     * @return InputStream
     * @throws IOException
     */
    public static InputStream openStream(String ftpUrl) throws IOException {
        URL url = new URL(ftpUrl);
        return url.openStream();
    }


    /**
     * 根据FTP文件目录和文件名下载车台升级文件
     *
     * @param ftpUrl   FTP文件目录路径
     * @param fileName 文件名
     * @return 车台升级文件
     * @throws IOException
     */
    public static TerminalUpgradeFile downloadUpgradeFile(String ftpUrl, byte fileType, String fileName) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            String ftpFileUrl = new StringBuffer(ftpUrl).append('/').append(fileName).toString();
            int size = byteArrayOutputStream.write(openStream(ftpFileUrl));
            byte[] fileBytes = byteArrayOutputStream.toByteArray();
            int crc32 = CRCUtil.getCRC32(fileBytes);
            return new TerminalUpgradeFile(fileType, fileName, size, NumberHexUtil.intToHex(crc32));
        } finally {
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
                byteArrayOutputStream = null;
            }
        }
    }

    /**
     * 下载内核文件
     *
     * @param ftpUrl FTP文件目录路径
     * @return 内核文件
     * @throws IOException
     */
    public static TerminalUpgradeFile downloadFileKernel(String ftpUrl) throws IOException {
        return downloadUpgradeFile(ftpUrl, TerminalUpgradeConst.FILE_TYPE_2_KERNEL, TerminalUpgradeConst.FILE_NAME_KERNEL);
    }

    /**
     * 下载文件系统文件
     *
     * @param ftpUrl FTP文件目录路径
     * @return 文件系统文件
     * @throws IOException
     */
    public static TerminalUpgradeFile downloadFileSys(String ftpUrl) throws IOException {
        return downloadUpgradeFile(ftpUrl, TerminalUpgradeConst.FILE_TYPE_3_FILESYS, TerminalUpgradeConst.FILE_NAME_FILESYS);
    }

    /**
     * 下载设备树文件
     *
     * @param ftpUrl FTP文件目录路径
     * @return 设备树文件
     * @throws IOException
     */
    public static TerminalUpgradeFile downloadFileDeviceTree(String ftpUrl) throws IOException {
        return downloadUpgradeFile(ftpUrl, TerminalUpgradeConst.FILE_TYPE_4_DEVICE_TREE, TerminalUpgradeConst.FILE_NAME_DEVICE_TREE);
    }

    /**
     * 下载APP文件
     *
     * @param ftpUrl FTP文件目录路径
     * @return APP文件
     * @throws IOException
     */
    public static TerminalUpgradeFile downloadFileApp(String ftpUrl) throws IOException {
        return downloadUpgradeFile(ftpUrl, TerminalUpgradeConst.FILE_TYPE_100_APP, TerminalUpgradeConst.FILE_NAME_APP);
    }

    /**
     * 下载版本号
     *
     * @param ftpUrl FTP文件目录路径
     * @return 版本号字符串
     * @throws IOException
     */
    public static String downloadVer(String ftpUrl) throws IOException {
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            String verFileUrl = new StringBuffer(ftpUrl).append('/').append(TerminalUpgradeConst.FILE_VER).toString();
            outputStream.write(openStream(verFileUrl));
            return new String(outputStream.toByteArray(), "utf-8");
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
