package com.tipray.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 文件工具类
 *
 * @author chends
 *
 */
public class FileUtil {
	/** 1B等于多少 */
	public static final long KB_SIZE_TO_B = 1000;
	/** 1MB等于多少 */
	public static final long MB_SIZE_TO_B = 1024 * KB_SIZE_TO_B;
	/** 1GB等于多少B */
	public static final long GB_SIZE_TO_B = 1024 * MB_SIZE_TO_B;

	private static String WEB_CLASSES_PATH = "";
	private static String WEB_ROOT_PATH = "";
	private static String WEB_INF_PATH = "";
	private static String JAVA_LIBRARY_PATH = "";
	private static String USER_DIR = "";

	/**
	 * 拼接路径
	 *
	 * @param paths
	 * @return
	 */
	public static String append(String... paths) {
		StringBuffer result = new StringBuffer();
		if (paths != null) {
			for (String path : paths) {
				if (path != null) {
					path = path.replaceAll("\\\\", "/");
					result.append(result.length() == 0 || result.toString().endsWith("/") ? "" : "/");
					result.append(path);
				}
			}
		}
		return result.toString().replace("/", File.separator);

	}

	/**
	 * 获取绝对路径
	 *
	 * @param path
	 * @return
	 */
	public static String getAbsolutePath(String path) {
		File file = new File(path);
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			return file.getAbsolutePath();
		}
	}

	/**
	 * Resource locator helper
	 *
	 * http://www.thinkplexx.com/learn/howto/java/system/java-resource-loading-
	 * explained
	 * -absolute-and-relative-names-difference-between-classloader-and-class
	 * -resource-loading
	 */
	public static InputStream getResourceAsStream(String resource) throws IOException {
		String stripped = resource.startsWith("/") ? resource.substring(1) : resource;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream stream = null;
		if (classLoader != null) {
			stream = classLoader.getResourceAsStream(stripped);
		}
		if (stream == null) {
			stream = FileUtil.class.getResourceAsStream(resource);
		}
		if (stream == null) {
			stream = FileUtil.class.getClassLoader().getResourceAsStream(stripped);
		}
		if (stream == null) {
			throw new IOException(resource + " not found");
		}
		return stream;
	}

	/**
	 * Resource locator helper
	 */
	public static List<String> getResources(String resourceBase) throws URISyntaxException, IOException {
		String stripped = resourceBase.startsWith("/") ? resourceBase.substring(1) : resourceBase;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		List<String> resources = null;

		if (classLoader != null) {
			resources = getResourceListing(classLoader, stripped);
		}
		if (resources == null) {
			resources = getResourceListing(FileUtil.class.getClassLoader(), stripped);
		}
		if (resources == null) {
			throw new IOException(resourceBase + " not found");
		}
		return resources;
	}

	@SuppressWarnings("resource")
	public static List<String> getResourceListing(ClassLoader cl, String path) throws URISyntaxException, IOException {
		URL dirUrl = cl.getResource(path);

		if (dirUrl != null && dirUrl.getProtocol().equals("file")) {
			/* A file path: easy enough */
			return Arrays.asList(new File(dirUrl.toURI()).list());
		}
		if (dirUrl == null) {
			/*
			 * In case of a jar file, we can't actually find a directory. Have to assume the
			 * same jar as clazz.
			 */
			// String me = clazz.getName().replace(".", "/")+".class";
			// dirUrl = clazz.getClassLoader().getResource(me);
		}
		if (dirUrl != null && dirUrl.getProtocol().equals("jar")) {
			/* A JAR path */
			String jarPath = dirUrl.getPath().substring(5, dirUrl.getPath().indexOf("!")); // strip out only the JAR
																							// file
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries
															// in jar
			Set<String> result = new HashSet<String>(); // avoid duplicates in
														// case it is a
														// subdirectory

			while (entries.hasMoreElements()) {
				String name = entries.nextElement().getName();

				if (name.startsWith(path)) { // filter according to the path
					String entry = name.substring(path.length());
					int checkSubdir = entry.indexOf("/");

					if (checkSubdir >= 0) {
						// if it is a subdirectory, we just return the directory
						// name
						entry = entry.substring(0, checkSubdir);
					}

					result.add(entry);
				}
			}
			return new ArrayList<String>(result);
		}

		throw new UnsupportedOperationException("Cannot list files for URL " + dirUrl);
	}

	public static List<String> loadSqlFile(InputStream inputStream) throws Exception {
		if (inputStream == null) {
			return null;
		}
		List<String> sqls = new ArrayList<>();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
			StringBuffer sqlSb = new StringBuffer();
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				if (!str.startsWith("/*!") && !str.startsWith("--") && str.indexOf("/*") <= 0
						&& str.trim().length() > 0) {
					sqlSb.append(sqlSb.length() > 0 ? "\n" : "").append(str);
					if (str.trim().endsWith(";")) {
						sqls.add(sqlSb.toString());
						sqlSb.setLength(0);
					}
				}
			}
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return sqls;
	}

	/**
	 * 判断文件的编码格式
	 *
	 * @param fileName
	 * @return
	 */
	public static String getFilecharset(String fileName) {
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		BufferedInputStream bis = null;
		try {
			boolean checked = false;
			bis = new BufferedInputStream(new FileInputStream(new File(fileName)));
			bis.mark(0);
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1) {
				return charset; // 文件编码为 ANSI
			} else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
				charset = "UTF-16LE"; // 文件编码为 Unicode
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
				charset = "UTF-16BE"; // 文件编码为 Unicode big endian
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB
					&& first3Bytes[2] == (byte) 0xBF) {
				charset = "UTF-8"; // 文件编码为 UTF-8
				checked = true;
			}
			bis.reset();
			if (!checked) {
				while ((read = bis.read()) != -1) {
					if (read >= 0xF0) {
						break;
					}
					if (0x80 <= read && read <= 0xBF) { // 单独出现BF以下的，也算是GBK
						break;
					}
					if (0xC0 <= read && read <= 0xDF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) { // 双字节 (0xC0 - 0xDF)
							// (0x80
							// - 0xBF),也可能在GB编码内
							continue;
						} else {
							break;
						}
					} else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) {
							read = bis.read();
							if (0x80 <= read && read <= 0xBF) {
								charset = "UTF-8";
								break;
							} else {
								break;
							}
						} else {
							break;
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return charset;
	}

	/**
	 * 读取文件
	 *
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 */
	public static InputStream readFile(String path) throws FileNotFoundException {
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			return new FileInputStream(file);
		}
		return null;
	}

	/**
	 * 使用NIO输出文件
	 *
	 * @param is
	 * @param filePath
	 */
	@SuppressWarnings("resource")
	public static void writeFileByNIO(FileInputStream is, String filePath) {
		if (is == null || filePath == null || filePath.isEmpty()) {
			return;
		}
		FileChannel isChannel = null;
		FileChannel osChannel = null;
		try {
			createFile(filePath);
			isChannel = is.getChannel();
			osChannel = new FileOutputStream(filePath).getChannel();
			ByteBuffer buf = ByteBuffer.allocate(1024);
			while (isChannel.read(buf) != -1) {
				buf.flip();
				osChannel.write(buf);
				buf.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (osChannel != null) {
					osChannel.close();
				}
				if (isChannel != null) {
					isChannel.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 输出文件
	 *
	 * @param is
	 * @param filePath
	 */
	public static void writeFile(InputStream is, String filePath) {
		if (is == null || filePath == null || filePath.isEmpty()) {
			return;
		}
		if (is instanceof FileInputStream) {
			writeFileByNIO((FileInputStream) is, filePath);
		} else {
			FileOutputStream fos = null;
			try {
				createFile(filePath);
				fos = new FileOutputStream(filePath);
				byte[] buf = new byte[1024];
				int byteCount = 0;

				while ((byteCount = is.read(buf)) != -1) {
					fos.write(buf, 0, byteCount);
				}
				fos.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (null != fos) {
						fos.close();
					}
					if (null != is) {
						is.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 格式化路径
	 *
	 * @param string
	 * @return
	 */
	public static String formatPath(String path) {
		path = path.replaceAll("\\\\", "/");
		return path.replace("/", File.separator);
	}

	/**
	 * 格式化默认路径
	 *
	 * @param path
	 * @return
	 */
	public static String formatSystemProperty(String path) {
		if (path == null || path.trim().isEmpty()) {
			return path;
		}
		String catalinaHome = System.getProperty("catalina.home");
		String userDir = System.getProperty("user.dir");
		path = path.replace("${catalina.home}", catalinaHome == null ? "" : catalinaHome);
		path = path.replace("${user.dir}", userDir == null ? "" : userDir);
		path = path.replace("${webapp.root}", getWebRoot());
		return toBackslant(path);
	}

	/**
	 * 创建文件夹
	 *
	 * @param path
	 */
	public static void createPath(String path) {
		if (path != null) {
			try {
				File file = new File(path);
				if (!file.exists()) {
					file.mkdirs();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 创建文件
	 *
	 * @param path
	 */
	public static void createFile(String path) {
		if (path != null) {
			try {
				File file = new File(path);
				createPath(file.getParent());
				if (!file.exists()) {
					file.createNewFile();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取java.library.path的路径
	 *
	 * @return
	 */
	public static String getJavaLibraryPath() {
		if (JAVA_LIBRARY_PATH.isEmpty()) {
			synchronized (JAVA_LIBRARY_PATH) {
				String libpath = System.getProperty("java.library.path");
				StringTokenizer st = new StringTokenizer(libpath, System.getProperty("path.separator"));
				if (st.hasMoreElements()) {
					JAVA_LIBRARY_PATH = st.nextToken();
				}
			}
		}
		return JAVA_LIBRARY_PATH;
	}

	/**
	 * 获取user.dir用户的当前工作目录
	 *
	 * @return
	 */
	public static String getUserDir() {
		if (USER_DIR.isEmpty()) {
			synchronized (USER_DIR) {
				String userDir = System.getProperty("user.dir");
				StringTokenizer st = new StringTokenizer(userDir, System.getProperty("path.separator"));
				if (st.hasMoreElements()) {
					USER_DIR = st.nextToken();
				}
			}
		}
		return USER_DIR;
	}

	/**
	 * 获取web class目录
	 *
	 * @return
	 */
	public static String getWebClassesPath() {
		if (WEB_CLASSES_PATH == null || WEB_CLASSES_PATH.isEmpty()) {
			synchronized (WEB_CLASSES_PATH) {
				// 在Linux下获取的路径不是classes的路径
				// try {
				// WEB_CLASSES_PATH =
				// FileUtil.class.getProtectionDomain().getCodeSource()
				// .getLocation().toURI().getPath();
				// } catch (URISyntaxException e) {
				// e.printStackTrace();
				// }
				// WEB_CLASSES_PATH =
				// FileUtil.class.getProtectionDomain().getCodeSource()
				// .getLocation().getPath();
				try {
					WEB_CLASSES_PATH = Thread.currentThread().getContextClassLoader().getResource("/").toURI()
							.getPath();
				} catch (URISyntaxException e) {
					e.printStackTrace();
					WEB_CLASSES_PATH = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
				}
			}
		}
		return WEB_CLASSES_PATH;
	}

	/**
	 * 获取WEB-INF目录
	 *
	 * @return
	 * @throws RuntimeException
	 */
	public static String getWebInfPath() throws RuntimeException {
		if (WEB_INF_PATH == null || WEB_INF_PATH.isEmpty()) {
			synchronized (WEB_INF_PATH) {
				String path = getWebClassesPath();
				String webInf = "WEB-INF";
				if (path.indexOf(webInf) > 0) {
					path = path.substring(0, path.indexOf(webInf) + webInf.length());
				} else {
					throw new RuntimeException("路径获取错误");
				}
				WEB_INF_PATH = path;
			}
		}
		return WEB_INF_PATH;
	}

	/**
	 * 获取WEB根目录
	 *
	 * @return
	 * @throws RuntimeException
	 */
	public static String getWebRoot() throws RuntimeException {
		if (WEB_ROOT_PATH == null || WEB_ROOT_PATH.isEmpty()) {
			synchronized (WEB_ROOT_PATH) {
				String path = getWebClassesPath();
				if (path.indexOf("WEB-INF") > 0) {
					path = path.substring(0, path.indexOf("WEB-INF") - 1);
				} else {
					throw new RuntimeException("路径获取错误");
				}
				WEB_ROOT_PATH = path;
			}
		}
		return WEB_ROOT_PATH;
	}

	/**
	 * 项目根目录
	 *
	 * @return
	 */
	public static String getRootPath() {
		try {
			return new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 临时文件存放目录
	 *
	 * @return
	 */
	public static String getTempPath() {
		// return new File("../temp").getCanonicalPath();
		return System.getProperty("java.io.tmpdir");
	}

	/**
	 * 获取文件名
	 *
	 * @param path
	 * @return
	 */
	public static String getFileName(String path) {
		if (path == null || path.trim().isEmpty()) {
			return null;
		}
		if (path.lastIndexOf("\\") >= 0) {
			return path.substring(path.lastIndexOf("\\") + 1);
		} else if (path.lastIndexOf("/") >= 0) {
			return path.substring(path.lastIndexOf("/") + 1);
		}
		return path;
	}

	/**
	 * 压缩文件夹里的文件 起初不知道是文件还是文件夹--- 统一调用该方法
	 *
	 * @param zipPath
	 *            压缩后文件的完整路径
	 * @param dirPath
	 *            要压缩的文件路径
	 * @param basedir
	 *            压缩文件中文件目录
	 * @throws IOException
	 */
	public static void zipFile(String zipPath, String basedir, String dirPath) throws IOException {
		List<File> files = new ArrayList<File>();
		files = converToFiles(dirPath, files);
		zipFiles(zipPath, basedir, files.toArray(new File[files.size()]));
	}

	/**
	 * 获取文件路径下所有文件
	 *
	 * @param dirPath
	 *            要压缩的文件夹路径
	 * @param list
	 *            要压缩的文件夹路径包含的文件和目录集合
	 */
	public static List<File> converToFiles(String dirPath, List<File> list) {
		File dirFile = new File(dirPath);
		File[] files = dirFile.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				converToFiles(file.getPath(), list);
			} else {
				list.add(file);
			}
		}
		return list;
	}

	/**
	 * 压缩多个文件
	 *
	 * @param zipPath
	 *            压缩后文件的完整路径
	 * @param files
	 *            要压缩的文件
	 * @throws IOException
	 */
	public static void zipFiles(String zipPath, String basedir, File... files) throws IOException {
		zipFiles(zipPath, null, null, basedir, files);
	}

	public static void zipFiles(String zipFilePath, InputStream is, String fileName, String basedir,
			String... filePaths) throws IOException {
		File[] files = new File[filePaths.length];
		int i = 0;
		for (String filePath : filePaths) {
			files[i++] = new File(filePath);
		}
		zipFiles(zipFilePath, is, fileName, basedir, files);
	}

	private static void zipFiles(String zipPath, InputStream is, String fileName, String basedir, File... files)
			throws IOException {
		ZipOutputStream zipOut = null;
		BufferedInputStream input = null;
		try {
			if (files != null && files.length > 0) {
				File zipFile = new File(zipPath);
				zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
				zipOut.setComment("zip file");
				for (File file : files) {
					input = new BufferedInputStream(new FileInputStream(file));
					zipOut.putNextEntry(new ZipEntry(file.getPath().replace(basedir, "")));
					int length = 0;
					byte[] buffer = new byte[4096];
					while ((length = input.read(buffer)) != -1) {
						zipOut.write(buffer, 0, length);
					}
				}
				if (is != null) {
					input = new BufferedInputStream(is);
					zipOut.putNextEntry(new ZipEntry(fileName));
					int length = 0;
					byte[] buffer = new byte[4096];
					while ((length = input.read(buffer)) != -1) {
						zipOut.write(buffer, 0, length);
					}
				}
				zipOut.flush();
			}
		} finally {
			if (zipOut != null) {
				zipOut.closeEntry();
				zipOut.close();
			}
			if (input != null) {
				input.close();
			}
			if (is != null) {
				is.close();
			}
		}
	}

	/**
	 * 解压缩文件
	 *
	 * @param zipPath
	 *            待解压文件的完整路径
	 * @param outPath
	 *            加压后文件的输出路径
	 * @throws IOException
	 */
	public static void unzipFile(String zipPath, String outPath) throws IOException {
		InputStream input = null;
		OutputStream output = null;
		ZipInputStream zipInput = null;
		ZipFile zipFile = null;
		try {
			File file = new File(zipPath);
			File outFile = null;
			zipFile = new ZipFile(file);
			zipInput = new ZipInputStream(new FileInputStream(file));
			ZipEntry entry = null;
			while ((entry = zipInput.getNextEntry()) != null) {
				outFile = new File(append(outPath, entry.getName()));
				if (!outFile.getParentFile().exists()) {
					outFile.getParentFile().mkdirs();
				}
				if (!outFile.exists()) {
					outFile.createNewFile();
				}
				input = zipFile.getInputStream(entry);
				output = new FileOutputStream(outFile);
				byte[] b = new byte[2048];
				while (input.read(b) != -1) {
					output.write(b);
				}
			}
			output.flush();
		} finally {
			if (output != null) {
				output.close();
			}
			if (input != null) {
				input.close();
			}
			if (zipInput != null) {
				zipInput.closeEntry();
				zipInput.close();
			}
			if (zipFile != null) {
				zipFile.close();
			}
		}
	}

	/**
	 * 获取解压后的文件流
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static List<InputStream> getUnzipFiles(File file) throws IOException {
		List<InputStream> list = new ArrayList<InputStream>();
		if (file != null) {
			ZipFile zipFile = new ZipFile(file);
			ZipInputStream zipInput = new ZipInputStream(new FileInputStream(file));
			ZipEntry entry = null;
			try {
				while ((entry = zipInput.getNextEntry()) != null) {
					list.add(zipFile.getInputStream(entry));
				}
			} finally {
				if (entry != null) {
					entry.clone();
				}
				if (zipInput != null) {
					zipInput.closeEntry();
					zipInput.close();
				}
			}
		}
		return list;
	}

	/**
	 * 读取文件的部分信息
	 *
	 * @param file
	 * @param sord
	 *            asc:从头开始读，desc:从结尾开始读
	 * @param row
	 *            读取行数
	 * @return
	 */
	@SuppressWarnings({ "resource" })
	public static List<String> readFile(File file, int row) {
		FileChannel fcin = null;
		try {
			ByteBuffer buf = ByteBuffer.allocate(5120);
			fcin = new FileInputStream(file).getChannel();
			List<String> dataList = new ArrayList<String>();// 存储读取的每行数据
			int lf = 10;// 换行符
			// int CR = 13;//回车符
			// String encode = "GBK";
			String encode = "UTF-8";

			long position = Math.max(fcin.size() - buf.limit(), 0);
			while (position >= 0 && fcin.read(buf, position) != -1) {// fcin.read(rBuffer)：从文件管道读取内容到缓冲区(rBuffer)
				int rSize = buf.position();// 读取结束后的位置，相当于读取的长度
				byte[] bs = new byte[rSize];// 用来存放读取的内容的数组
				buf.rewind();// 将position设回0,所以你可以重读Buffer中的所有数据,此处如果不设置,无法使用下面的get方法
				buf.get(bs);// 相当于rBuffer.get(bs,0,bs.length())：从position初始位置开始相对读,读bs.length个byte,并写入bs[0]到bs[bs.length-1]的区域
				buf.clear();

				int lineStart = 0;
				int lineEnd = rSize - 1;

				for (int i = rSize - 1; i >= 0 && dataList.size() < row; i--) {
					if (bs[i] == lf) {
						lineStart = i;
						String line = new String(bs, lineStart, lineEnd - lineStart + 1, encode);// 一行完整的字符串(过滤了换行和回车)
						lineEnd = lineStart - 1;
						dataList.add(0, line);
					}
				}
				if (position == 0 && lineEnd > 0 && dataList.size() < row) {
					String line = new String(bs, 0, lineEnd, encode);// 一行完整的字符串
					dataList.add(0, line);
				}
				if (dataList.size() >= row) {
					break;
				}
				if (position == 0) {
					break;
				} else {
					position = position + lineEnd;
					if (position - buf.limit() < 0) {
						ByteBuffer.allocate((int) position);
						position = 0;
					} else {
						position = position - buf.limit();
					}
				}
			}
			return dataList;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fcin != null) {
				try {
					fcin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 根据大小格式化对应的单位
	 *
	 * @param b
	 * @return
	 */
	public static String formatSize(long b) {
		Double d = Double.valueOf(b);
		DecimalFormat format = new DecimalFormat("#.##");
		if (b > GB_SIZE_TO_B) {
			return format.format(d / GB_SIZE_TO_B) + "GB";
		} else if (b > MB_SIZE_TO_B) {
			return format.format(d / MB_SIZE_TO_B) + "MB";
		} else {
			return format.format(d / KB_SIZE_TO_B) + "KB";
		}
	}

	/**
	 * 将斜杠"\"转为反斜杠"/"
	 */
	public static String toBackslant(String path) {
		if (path == null || path.trim().isEmpty()) {
			return path;
		}
		return path.replaceAll("\\\\", "/");
	}

	/**
	 * 删除文件
	 *
	 * @param filePaths
	 */
	public static void deleteFile(File file) {
		if (file == null || !file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			for (File childFile : file.listFiles()) {
				deleteFile(childFile);
			}
		} else {
			if (!file.delete()) {
				System.gc();
				file.delete();
			}
		}
	}

    /**
     * 删除指定文件夹下指定日期前的文件
     * @param folderPath 文件夹路径
     * @param duration 日期间隔
     * @param timeUnit 日期单位
     * @return 删除的文件
     * @throws IllegalArgumentException if path is file not folder.
     */
	public static File[] deleteFilesBeforeDateInFolder(String folderPath, long duration, TimeUnit timeUnit) {
	    File folder = new File(folderPath);
	    if (folder.isFile()) throw new IllegalArgumentException("给定的路径[" + folderPath + "]是文件而非文件夹！");
	    long durationMillis = timeUnit.toMillis(duration);
	    long beforeMillis = System.currentTimeMillis() - durationMillis;
	    return folder.listFiles(file -> {
            long lastModified = file.lastModified();
            boolean flag = lastModified < beforeMillis;
            if (flag) file.delete();
            return flag;
        });
    }

    /**
     * 获取整形属性值
     *
     * @param properties   {@link Properties}
     * @param key          属性名称
     * @param defaultValue 默认值
     * @return 属性值
     */
    public static int getIntProp(Properties properties, String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value, 10);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取布尔型属性值
     *
     * @param properties   {@link Properties}
     * @param key          属性名称
     * @param defaultValue 默认值
     * @return 属性值
     */
    public static boolean getBoolProp(Properties properties, String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.equals("1")
                || value.equalsIgnoreCase("true")
                || value.equalsIgnoreCase("yes");
    }
}
