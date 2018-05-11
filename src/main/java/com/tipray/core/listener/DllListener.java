package com.tipray.core.listener;

import java.lang.reflect.Field;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * dll文件路径监听器
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public class DllListener implements ServletContextListener {
	private static final Logger logger = LoggerFactory.getLogger(DllListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// 获取存放dll文件的绝对路径（假设将dll文件放在系统根目录下的WEB-INF文件夹中）
		String path = sce.getServletContext().getRealPath("WEB-INF");
		// 将此目录添加到系统环境变量中
		addDirToPath(path);
		// 加载相应的dll文件，注意要将'\'替换为'/'
		System.load(path.replaceAll("\\\\", "/") + "/XXXX.dll");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

	/**
	 * 将dll文件所在的路径添加到系统环境java.library.path中。
	 * <p>
	 * 添加过程需要使用到反射机制来进行，不能使用System.setProperty()进行设置，设置了也无效。
	 * 
	 * @param dirPath
	 *            dll文件所在的路径
	 */
	private void addDirToPath(String dirPath) {
		try {
			// 获取系统path变量对象
			Field field = ClassLoader.class.getDeclaredField("sys_paths");
			// 设置此变量对象可访问
			field.setAccessible(true);
			// 获取此变量对象的值
			String[] path = (String[]) field.get(null);
			// 创建字符串数组，在原来的数组长度上增加一个，用于存放增加的目录
			String[] tem = new String[path.length + 1];
			// 将原来的path变量复制到tem中
			System.arraycopy(path, 0, tem, 0, path.length);
			// 将增加的目录存入新的变量数组中
			tem[path.length] = dirPath;
			// 将增加目录后的数组赋给path变量对象
			field.set(null, tem);
		} catch (Exception e) {
			logger.error("添加系统环境变量异常：\n{}", e.toString());
		}
	}
}
