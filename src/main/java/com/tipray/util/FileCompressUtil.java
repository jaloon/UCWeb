package com.tipray.util;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.ice.tar.TarEntry;
import com.ice.tar.TarInputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.*;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

/**
 * 文件压缩/解压工具类
 *
 * @author chenlong
 * @version 1.0 2018-06-13
 */
public class FileCompressUtil {

    public enum FileType {
        // 未知
        UNKNOWN,
        // 压缩文件
        ZIP, RAR, _7Z, TAR, GZ, TAR_GZ, BZ2, TAR_BZ2,
        // 位图文件
        BMP, PNG, JPG, JPEG,
        // 矢量图文件
        SVG,
        // 影音文件
        AVI, MP4, MP3, AAR, OGG, WAV, WAVE;
    }


    /**
     * 获取文件真实类型
     *
     * @param file 要获取类型的文件。
     * @return 文件类型枚举。
     */
    public static FileType getFileType(File file) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            byte[] head = new byte[4];
            if (-1 == inputStream.read(head)) {
                return FileType.UNKNOWN;
            }
            int headHex = 0;
            for (byte b : head) {
                headHex <<= 8;
                headHex |= b;
            }
            switch (headHex) {
                case 0x504B0304:
                    return FileType.ZIP;
                case 0x776f7264:
                    return FileType.TAR;
                case -0x51:
                    return FileType._7Z;
                case 0x425a6839:
                    return FileType.BZ2;
                case -0x74f7f8:
                    return FileType.GZ;
                case 0x52617221:
                    return FileType.RAR;
                default:
                    return FileType.UNKNOWN;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return FileType.UNKNOWN;
    }

    /**
     * 构建目录
     *
     * @param outputDir 输出目录
     * @param subDir    子目录
     */
    private static void createDirectory(String outputDir, String subDir) {
        File file = new File(outputDir);
        if (!(subDir == null || subDir.trim().equals(""))) {//子目录不为空
            file = new File(outputDir + File.separator + subDir);
        }
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.mkdirs();
        }
    }

    /**
     * 解压缩tar文件
     *
     * @param file       压缩包文件
     * @param targetPath 目标文件夹
     * @param delete     解压后是否删除原压缩包文件
     */
    public static void decompressTar(File file, String targetPath, boolean delete) {
        FileInputStream fis = null;
        OutputStream fos = null;
        TarInputStream tarInputStream = null;
        try {
            fis = new FileInputStream(file);
            tarInputStream = new TarInputStream(fis, 1024 * 2);
            // 创建输出目录
            createDirectory(targetPath, null);

            TarEntry entry = null;
            while (true) {
                entry = tarInputStream.getNextEntry();
                if (entry == null) {
                    break;
                }
                if (entry.isDirectory()) {
                    createDirectory(targetPath, entry.getName()); // 创建子目录
                } else {
                    fos = new FileOutputStream(new File(targetPath + File.separator + entry.getName()));
                    int count;
                    byte data[] = new byte[2048];
                    while ((count = tarInputStream.read(data)) != -1) {
                        fos.write(data, 0, count);
                    }
                    fos.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (tarInputStream != null) {
                    tarInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解压缩bz2文件
     *
     * @param file       压缩包文件
     * @param targetPath 目标文件夹
     * @param delete     解压后是否删除原压缩包文件
     */
    public static void decompressBZ2(File file, String targetPath, boolean delete) {
        FileInputStream fis = null;
        OutputStream fos = null;
        BZip2CompressorInputStream bis = null;
        String suffix = ".bz2";
        try {
            fis = new FileInputStream(file);
            bis = new BZip2CompressorInputStream(fis);
            // 创建输出目录
            createDirectory(targetPath, null);
            File tempFile = new File(targetPath + File.separator + file.getName().replace(suffix, ""));
            fos = new FileOutputStream(tempFile);

            int count;
            byte data[] = new byte[2048];
            while ((count = bis.read(data)) != -1) {
                fos.write(data, 0, count);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解压缩tar.bz2文件
     *
     * @param file       压缩包文件
     * @param targetPath 目标文件夹
     * @param delete     解压后是否删除原压缩包文件
     */
    public static void decompressTarBz2(File file, String targetPath, boolean delete) {
        FileInputStream fis = null;
        OutputStream fos = null;
        BZip2CompressorInputStream bis = null;
        TarInputStream tis = null;
        try {
            fis = new FileInputStream(file);
            bis = new BZip2CompressorInputStream(fis);
            tis = new TarInputStream(bis, 1024 * 2);
            // 创建输出目录
            createDirectory(targetPath, null);
            TarEntry entry;
            while ((entry = tis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    createDirectory(targetPath, entry.getName()); // 创建子目录
                } else {
                    fos = new FileOutputStream(new File(targetPath + File.separator + entry.getName()));
                    int count;
                    byte data[] = new byte[2048];
                    while ((count = tis.read(data)) != -1) {
                        fos.write(data, 0, count);
                    }
                    fos.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (bis != null) {
                    bis.close();
                }
                if (tis != null) {
                    tis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解压缩tar.gz文件
     *
     * @param file       压缩包文件
     * @param targetPath 目标文件夹
     * @param delete     解压后是否删除原压缩包文件
     */
    public static void decompressTarGz(File file, String targetPath, boolean delete) {
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        GZIPInputStream gzipIn = null;
        TarInputStream tarIn = null;
        OutputStream out = null;
        try {
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            gzipIn = new GZIPInputStream(bufferedInputStream);
            tarIn = new TarInputStream(gzipIn, 1024 * 2);

            // 创建输出目录
            createDirectory(targetPath, null);

            TarEntry entry = null;
            while ((entry = tarIn.getNextEntry()) != null) {
                if (entry.isDirectory()) { // 是目录
                    createDirectory(targetPath, entry.getName()); // 创建子目录
                } else { // 是文件
                    File tempFIle = new File(targetPath + File.separator + entry.getName());
                    createDirectory(tempFIle.getParent() + File.separator, null);
                    out = new FileOutputStream(tempFIle);
                    int len = 0;
                    byte[] b = new byte[2048];

                    while ((len = tarIn.read(b)) != -1) {
                        out.write(b, 0, len);
                    }
                    out.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (tarIn != null) {
                    tarIn.close();
                }
                if (gzipIn != null) {
                    gzipIn.close();
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解压缩gz文件
     *
     * @param file       压缩包文件
     * @param targetPath 目标文件夹
     * @param delete     解压后是否删除原压缩包文件
     */
    public static void decompressGz(File file, String targetPath, boolean delete) {
        FileInputStream fileInputStream = null;
        GZIPInputStream gzipIn = null;
        OutputStream out = null;
        String suffix = ".gz";
        try {
            fileInputStream = new FileInputStream(file);
            gzipIn = new GZIPInputStream(fileInputStream);
            // 创建输出目录
            createDirectory(targetPath, null);

            File tempFile = new File(targetPath + File.separator + file.getName().replace(suffix, ""));
            out = new FileOutputStream(tempFile);
            int count;
            byte data[] = new byte[2048];
            while ((count = gzipIn.read(data)) != -1) {
                out.write(data, 0, count);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (gzipIn != null) {
                    gzipIn.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解压缩7z文件
     *
     * @param file       压缩包文件
     * @param targetPath 目标文件夹
     * @param delete     解压后是否删除原压缩包文件
     */
    public static void decompress7Z(File file, String targetPath, boolean delete) {
        SevenZFile sevenZFile = null;
        OutputStream outputStream = null;
        try {
            sevenZFile = new SevenZFile(file);
            // 创建输出目录
            createDirectory(targetPath, null);
            SevenZArchiveEntry entry;

            while ((entry = sevenZFile.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    createDirectory(targetPath, entry.getName()); // 创建子目录
                } else {
                    outputStream = new FileOutputStream(new File(targetPath + File.separator + entry.getName()));
                    int len = 0;
                    byte[] b = new byte[2048];
                    while ((len = sevenZFile.read(b)) != -1) {
                        outputStream.write(b, 0, len);
                    }
                    outputStream.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (sevenZFile != null) {
                    sevenZFile.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解压缩RAR文件
     *
     * @param file       压缩包文件
     * @param targetPath 目标文件夹
     * @param delete     解压后是否删除原压缩包文件
     */
    public static void decompressRAR(File file, String targetPath, boolean delete) {
        Archive archive = null;
        OutputStream outputStream = null;
        try {
            archive = new Archive(file);
            FileHeader fileHeader;
            // 创建输出目录
            createDirectory(targetPath, null);
            while ((fileHeader = archive.nextFileHeader()) != null) {
                if (fileHeader.isDirectory()) {
                    createDirectory(targetPath, fileHeader.getFileNameString().trim()); // 创建子目录
                } else {
                    outputStream = new FileOutputStream(new File(targetPath + File.separator + fileHeader.getFileNameString().trim()));
                    archive.extractFile(fileHeader, outputStream);
                }
            }
        } catch (RarException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (archive != null) {
                    archive.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * java调用winrar解压RAR文件
     *
     * @param target
     * @param oldFile
     */
    public void unRarFile(File target, File oldFile) {
        try {
            if (isWindow()) {
                String path = target.getParentFile().getAbsolutePath();
                File tmp = target.getParentFile();
                if (path.charAt(path.length() - 1) != ((char) File.separatorChar)) {
                    tmp = new File(path + File.separator);
                    if (!tmp.exists())
                        tmp.mkdirs();
                }
                String cmd2 = "C:\\Program Files\\WinRAR\\winrar.exe x -r -o+ -ibck -y "
                        + oldFile + " *.* " + tmp;
                Runtime rt = Runtime.getRuntime();
                Process pre = rt.exec(cmd2);
                if (0 != pre.waitFor()) {
                    pre.destroy();
                }
                rt.runFinalization();
                System.out.println(cmd2);
            } else {
                System.out.println("can't get rar command abort");
            }
        } catch (Exception e) {
            System.out.println("解压发生异常");
        }
    }

    /**
     * 是否是window
     *
     * @return
     */
    public boolean isWindow() {
        Properties properties = System.getProperties();
        String os = properties.getProperty("os.name");
        if (os != null && os.contains("Windows"))
            return true;
        return false;
    }
}
