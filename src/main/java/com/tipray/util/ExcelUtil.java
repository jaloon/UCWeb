package com.tipray.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;//对应xls格式的Excel文档
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;//对应一个单元格
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;//对应一个sheet中的一行
import org.apache.poi.ss.usermodel.Sheet;//对应Excel文档中的一个sheet
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;//对应Excel文档
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;//对应xlsx格式的Excel文档
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * POI操作EXCEL工具类
 * 
 * @author chenlong
 * @version 1.0 2018-01-30
 *
 */
public class ExcelUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);

	/** Excel 97-2003 (.xls) */
	public static final int XLS = 0;
	/** Excel (.xlsx) */
	public static final int XLSX = 1;
	/** 标题行背景颜色 */
	public static final short HEADER_BACKGROUND_COLOR = HSSFColorPredefined.PALE_BLUE.getIndex();
	/** 标题行字体颜色 */
	public static final short HEADER_FONT_COLOR = HSSFColorPredefined.DARK_RED.getIndex();
	/** 默认行高 */
	public static final short DEFAULT_ROW_HEIGHT = 500;
	/** 默认列宽 */
	public static final short DEFAULT_COLUMN_WIDTH = 20 * 256;

	public static final String FILE_NOT_FOUND_EXCEPTION = "路径指定的文件不存在";
	public static final String BAD_FILE_FORMAT_EXCEPTION = "Excel文件格式不正确";
	public static final String COLUMN_COUNT_NOT_SAME = "单行列数不一致";

	/**
	 * 打开指定路径文件
	 * 
	 * @param path
	 *            EXCEL文件路径
	 * @return {@link Workbook}
	 */
	public static Workbook open(String path) {
		// 文件后缀
		String suffix = path.substring(path.lastIndexOf(".")).toLowerCase();
		if (!".xls".equals(suffix) && !".xlsx".equals(suffix)) {
			throw new RuntimeException(BAD_FILE_FORMAT_EXCEPTION);
		}

		InputStream in = null;
		Workbook workbook = null;
		try {
			in = new FileInputStream(new File(path));
			if (".xls".equals(suffix)) {
				workbook = new HSSFWorkbook(in);
			} else {
				workbook = new XSSFWorkbook(in);
			}
		} catch (Exception e) {
			LOGGER.error("打开文档失败：\n{}", e.toString());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error("关闭输入流异常：\\n{}", e);
				}
			}
		}
		return workbook;
	}

	/**
	 * 读取EXCEL文件获得对象列表
	 * 
	 * @param path
	 *            EXCEL文件路径
	 * @param clazz
	 *            获得的对象类型 {@link Class}
	 * @param firstRowIndex
	 *            起始行索引，从0开始
	 * @param lastRowIndex
	 *            结束行索引，从0开始
	 * @return
	 */
	public static List<Object> xlsDto(String path, Class<?> clazz, int firstRowIndex, int lastRowIndex) {
		return xlsDto(open(path), clazz, firstRowIndex, lastRowIndex);
	}

	/**
	 * 读取EXCEL文件获得对象列表
	 * 
	 * @param inputStream
	 *            {@link InputStream} 输入流
	 * @param fileType
	 *            {@link ExcelUtil#XLS}，{@link ExcelUtil#XLSX}
	 * @param clazz
	 *            获得的对象类型 {@link Class}
	 * @param firstRowIndex
	 *            起始行索引，从0开始
	 * @param lastRowIndex
	 *            结束行索引，从0开始
	 * @return
	 */
	public static List<Object> xlsDto(InputStream inputStream, int fileType, Class<?> clazz, int firstRowIndex,
			int lastRowIndex) {
		if (XLS != fileType && XLSX != fileType) {
			throw new RuntimeException(BAD_FILE_FORMAT_EXCEPTION);
		}

		List<Object> list = new ArrayList<Object>();
		Workbook workbook = null;
		try {
			if (XLS == fileType) {
				workbook = new HSSFWorkbook(inputStream);
			} else {
				workbook = new XSSFWorkbook(inputStream);
			}
			list = xlsDto(workbook, clazz, firstRowIndex, lastRowIndex);
		} catch (Exception e) {
			LOGGER.error("读取EXCEL文件获得对象列表失败", e);
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					LOGGER.error("关闭EXCEL文件异常", e);
				}
			}
		}
		return list;
	}

	/**
	 * 读取EXCEL文件获得对象列表
	 * 
	 * @param workbook
	 *            Excel文档 {@link Workbook}
	 * @param clazz
	 *            获得的对象类型 {@link Class}
	 * @param firstRowIndex
	 *            起始行索引，从0开始
	 * @param lastRowIndex
	 *            结束行索引，从0开始
	 * @return
	 */
	public static List<Object> xlsDto(Workbook workbook, Class<?> clazz, int firstRowIndex, int lastRowIndex) {
		List<Object> list = new ArrayList<Object>();
		try {
			// java反射获得类的属性及类型
			Field[] fields = clazz.getDeclaredFields();
			int len = fields.length;
			String[] fieldNames = new String[len];
			Class<?>[] fieldTypes = new Class<?>[len];
			for (int i = 0; i < len; i++) {
				fieldNames[i] = fields[i].getName();
				fieldTypes[i] = fields[i].getType();
			}
			// 获取第一个工作簿
			Sheet sheet = workbook.getSheetAt(0);
			Row row;
			Cell cell;
			Object object;
			Method method;
			for (int rowIndex = firstRowIndex; rowIndex <= lastRowIndex; rowIndex++) {
				row = sheet.getRow(rowIndex);
				// 创建一个新对象
				object = Class.forName(clazz.getName()).newInstance();
				for (int fieldIndex = 0; fieldIndex < len; fieldIndex++) {
					cell = row.getCell(fieldIndex);
					// java反射调用属性的set方法给属性赋值
					String methodName = new StringBuffer("set").append(fieldNames[fieldIndex].substring(0, 1).toUpperCase())
							.append(fieldNames[fieldIndex].substring(1)).toString();
					method = clazz.getMethod(methodName, fieldTypes[fieldIndex]);
					method.invoke(object, getCellValue(cell, fieldTypes[fieldIndex].getName()));
				}
				list.add(object);
			}
		} catch (Exception e) {
			LOGGER.error("读取EXCEL文件获得对象列表失败：\n{}", e.toString());
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					LOGGER.error("关闭EXCEL文件异常：\n{}", e.toString());
				}
			}
		}
		return list;
	}

	/**
	 * 读取EXCEL文件获得String[][]数组
	 * 
	 * @param path
	 *            Excel文件路径
	 * @param firstRowIndex
	 *            起始行索引，从0开始
	 * @param lastRowIndex
	 *            结束行索引，从0开始
	 * @param firstColIndex
	 *            起始列索引，从0开始
	 * @param lastColIndex
	 *            结束列索引，从0开始
	 * @return
	 */
	public static String[][] xlsToStringArray(String path, int firstRowIndex, int lastRowIndex, int firstColIndex,
			int lastColIndex) {
		String suffix = path.substring(path.lastIndexOf(".")).toLowerCase();
		if (!".xls".equals(suffix) && !".xlsx".equals(suffix)) {
			throw new RuntimeException(BAD_FILE_FORMAT_EXCEPTION);
		}

		String[][] array = new String[0][0];
		Workbook workbook = null;
		try {
			workbook = open(path);
			array = xlsToStringArray(workbook, firstRowIndex, lastRowIndex, firstColIndex, lastColIndex);
		} catch (Exception e) {
			LOGGER.error("读取EXCEL文件获得String[][]数组失败：\n{}", e.toString());
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					LOGGER.error("关闭EXCEL文件异常：\n{}", e.toString());
				}
			}
		}
		return array;
	}

	/**
	 * 读取EXCEL文件获得String[][]数组
	 * 
	 * @param bytes
	 *            Excel文件内容
	 * @param fileType
	 *            {@link ExcelUtil#XLS}, {@link ExcelUtil#XLSX}
	 * @param firstRowIndex
	 *            起始行索引，从0开始
	 * @param lastRowIndex
	 *            结束行索引，从0开始
	 * @param firstColIndex
	 *            起始列索引，从0开始
	 * @param lastColIndex
	 *            结束列索引，从0开始
	 * @return
	 */
	public static String[][] xlsToStringArray(byte[] bytes, int fileType, int firstRowIndex, int lastRowIndex,
			int firstColIndex, int lastColIndex) {
		if (XLS != fileType && XLSX != fileType) {
			throw new RuntimeException(BAD_FILE_FORMAT_EXCEPTION);
		}

		String[][] array = new String[0][0];
		Workbook workbook = null;
		try (InputStream is = new ByteArrayInputStream(bytes)) {
			if (XLS == fileType) {
				workbook = new HSSFWorkbook(is);
			} else {
				workbook = new XSSFWorkbook(is);
			}
			array = xlsToStringArray(workbook, firstRowIndex, lastRowIndex, firstColIndex, lastColIndex);
		} catch (IOException e) {
			LOGGER.error("读取EXCEL文件获得String[][]数组失败：\n{}", e.toString());
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					LOGGER.error("关闭EXCEL文件异常：\n{}", e.toString());
				}
			}
		}
		return array;
	}
	
	/**
	 * 读取EXCEL文件获得String[][]数组
	 * 
	 * @param workbook
	 *            {@link Workbook} 对象
	 * @param firstRowIndex
	 *            起始行索引，从0开始
	 * @param lastRowIndex
	 *            结束行索引，从0开始
	 * @param firstColIndex
	 *            起始列索引，从0开始
	 * @param lastColIndex
	 *            结束列索引，从0开始
	 * @return
	 */
	public static String[][] xlsToStringArray(Workbook workbook, int firstRowIndex, int lastRowIndex,
			int firstColIndex, int lastColIndex) {
		String[][] array = new String[lastRowIndex - firstRowIndex + 1][lastColIndex - firstColIndex + 1];

		Sheet sheet = workbook.getSheetAt(0);
		Row row;
		Cell cell;
		for (int rowIndex = firstRowIndex; rowIndex <= lastRowIndex; rowIndex++) {
			row = sheet.getRow(rowIndex);

			for (int colIndex = firstColIndex; colIndex <= lastColIndex; colIndex++) {
				cell = row.getCell(colIndex);
				array[rowIndex - firstRowIndex][colIndex - firstColIndex] = cell.getStringCellValue();
			}
		}

		return array;
	}

	/**
	 * 导出EXCEL文件到response流
	 * 
	 * @param response
	 *            {@link HttpServletResponse}
	 * @param filename
	 *            文件名(不包含后缀)
	 * @param encoding
	 *            字符编码
	 * @param sheetName
	 *            sheet表名
	 * @param headers
	 *            标题行(String[])
	 * @param dataSet
	 *            数据(Collection<?>)
	 * @param dateFormat
	 *            由yyyy年、MM月、dd日、HH小时、mm分钟、ss秒构成的字符串
	 */
	public static void export(HttpServletResponse response, String filename, String encoding, String sheetName,
			String[] headers, Collection<?> dataSet, String dateFormat) {
		try {
			resetResponseToDownload(response, filename, encoding);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("文件名转码失败：\n{}", e.toString());
			return;
		}

		try (OutputStream out = response.getOutputStream()) {
			export(out, sheetName, headers, dataSet, dateFormat);
			out.flush();
		} catch (IOException e) {
			LOGGER.error("导出EXCEL文件到response流失败：\n{}", e.toString());
		}
	}

	/**
	 * 导出EXCEL文件到指定路径
	 * 
	 * @param path
	 *            导出路径
	 * @param sheetName
	 *            sheet表名
	 * @param headers
	 *            标题行(String[])
	 * @param dataSet
	 *            数据(Collection<?>)
	 * @param dateFormat
	 *            由yyyy年、MM月、dd日、HH小时、mm分钟、ss秒构成的字符串
	 */
	public static void export(String path, String sheetName, String[] headers, Collection<?> dataSet,
			String dateFormat) {
		OutputStream out = null;
		try {
			File file = createFile(path);
			out = new FileOutputStream(file);
			export(out, sheetName, headers, dataSet, dateFormat);
			out.flush();
		} catch (IOException e) {
			LOGGER.error("导出EXCEL文件到指定路径失败：\n{}", e.toString());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LOGGER.error("关闭输出流异常：\n{}", e.toString());
				}
			}
		}
	}

	/**
	 * 导出EXCEL文件到response流
	 * 
	 * @param response
	 *            {@link HttpServletResponse}
	 * @param filename
	 *            文件名(不包含后缀)
	 * @param encoding
	 *            字符编码
	 * @param sheetName
	 *            sheet表名
	 * @param headers
	 *            标题行(String[])
	 * @param data
	 *            数据(String[][])
	 */
	public static void export(HttpServletResponse response, String filename, String encoding, String sheetName,
			String[] headers, String[][] data) {
		try {
			resetResponseToDownload(response, filename, encoding);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("文件名转码失败：\n{}", e.toString());
			return;
		}

		try (OutputStream out = response.getOutputStream()){
			export(out, sheetName, headers, data);
			out.flush();
		} catch (IOException e) {
			LOGGER.error("导出EXCEL文件到response流失败：\n{}", e.toString());
		}
	}

	/**
	 * 导出EXCEL文件到指定路径
	 * 
	 * @param path
	 *            导出路径
	 * @param sheetName
	 *            sheet表名
	 * @param headers
	 *            标题行(String[])
	 * @param data
	 *            数据(String[][])
	 */
	public static void export(String path, String sheetName, String[] headers, String[][] data) {
		OutputStream out = null;
		try {
			File file = createFile(path);
			out = new FileOutputStream(file);
			export(out, sheetName, headers, data);
			out.flush();
		} catch (IOException e) {
			LOGGER.error("导出EXCEL文件到指定路径失败：\n{}", e.toString());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LOGGER.error("关闭输出流异常：\n{}", e.toString());
				}
			}
		}
	}

	/**
	 * 导出EXCEL文件到response流
	 * 
	 * @param response
	 *            {@link HttpServletResponse}
	 * @param filename
	 *            文件名(不包含后缀)
	 * @param encoding
	 *            字符编码
	 * @param sheetName
	 *            sheet表名
	 * @param headers
	 *            标题行(String[][])
	 * @param dataSet
	 *            数据(Collection<?>)
	 * @param dateFormat
	 *            由yyyy年、MM月、dd日、HH小时、mm分钟、ss秒构成的字符串
	 */
	public static void export(HttpServletResponse response, String filename, String encoding, String sheetName,
			String[][] headers, Collection<?> dataSet, String dateFormat) {
		try {
			resetResponseToDownload(response, filename, encoding);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("文件名转码失败：\n{}", e.toString());
			return;
		}

		try (OutputStream out = response.getOutputStream()) {
			export(out, sheetName, headers, dataSet, dateFormat);
			out.flush();
		} catch (IOException e) {
			LOGGER.error("导出EXCEL文件到response流失败：\n{}", e.toString());
		}
	}

	/**
	 * 导出EXCEL文件到指定路径
	 * 
	 * @param path
	 *            导出路径
	 * @param sheetName
	 *            sheet表名
	 * @param headers
	 *            标题行(String[][])
	 * @param dataSet
	 *            数据(Collection<?>)
	 * @param dateFormat
	 *            由yyyy年、MM月、dd日、HH小时、mm分钟、ss秒构成的字符串
	 */
	public static void export(String path, String sheetName, String[][] headers, Collection<?> dataSet,
			String dateFormat) {
		OutputStream out = null;
		try {
			File file = createFile(path);
			out = new FileOutputStream(file);
			export(out, sheetName, headers, dataSet, dateFormat);
			out.flush();
		} catch (IOException e) {
			LOGGER.error("导出EXCEL文件到指定路径失败：\n{}", e.toString());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LOGGER.error("关闭输出流异常：\n{}", e.toString());
				}
			}
		}
	}

	/**
	 * 导出EXCEL文件到response流
	 * 
	 * @param response
	 *            {@link HttpServletResponse}
	 * @param filename
	 *            文件名(不包含后缀)
	 * @param encoding
	 *            字符编码
	 * @param sheetName
	 *            sheet表名
	 * @param headers
	 *            标题行(String[][])
	 * @param data
	 *            数据(String[][])
	 */
	public static void export(HttpServletResponse response, String filename, String encoding, String sheetName,
			String[][] headers, String[][] data) {
		try {
			resetResponseToDownload(response, filename, encoding);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("文件名转码失败：\n{}", e.toString());
			return;
		}

		try (OutputStream out = response.getOutputStream()) {
			export(out, sheetName, headers, data);
			out.flush();
		} catch (IOException e) {
			LOGGER.error("导出EXCEL文件到response流失败：\n{}", e.toString());
		}
	}

	/**
	 * 导出EXCEL文件到指定路径
	 * 
	 * @param path
	 *            导出路径
	 * @param sheetName
	 *            sheet表名
	 * @param headers
	 *            标题行(String[][])
	 * @param data
	 *            数据(String[][])
	 */
	public static void export(String path, String sheetName, String[][] headers, String[][] data) {
		OutputStream out = null;
		try {
			File file = createFile(path);
			out = new FileOutputStream(file);
			export(out, sheetName, headers, data);
			out.flush();
		} catch (IOException e) {
			LOGGER.error("导出EXCEL文件到指定路径失败：\n{}", e.toString());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LOGGER.error("关闭输出流异常：\n{}", e.toString());
				}
			}
		}
	}

	/**
	 * 将工作簿导出至指定路径
	 * 
	 * @param workbook
	 *            {@link Workbook}
	 * @param destPath
	 *            指定路径
	 */
	public static void export(Workbook workbook, String destPath) {
		OutputStream out = null;
		try {
			File file = createFile(destPath);
			out = new FileOutputStream(file);
			workbook.write(out);
			out.flush();
		} catch (IOException e) {
			LOGGER.error("将工作簿导出至指定路径失败：\n{}", e.toString());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LOGGER.error("关闭输出流异常：\n{}", e.toString());
				}
			}
		}
	}

	/**
	 * 将工作簿导出至response流
	 * 
	 * @param workbook
	 *            {@link Workbook}
	 * @param response
	 *            {@link HttpServletResponse}
	 * @param filename
	 *            文件名(不含后缀)
	 * @param encoding
	 *            字符编码
	 */
	public static void export(Workbook workbook, HttpServletResponse response, String filename, String encoding) {
		try {
			resetResponseToDownload(response, filename, encoding);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("文件名转码失败", e);
			return;
		}

		OutputStream out = null;
		try {
			out = response.getOutputStream();
			workbook.write(out);
			out.flush();
		} catch (Exception e) {
			LOGGER.error("将工作簿导出至response流失败", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 打开指定模板填充数据并导出至文件
	 * 
	 * @param templatePath
	 *            模板路径
	 * @param sheetName
	 *            sheet表名
	 * @param lineCnt
	 *            数据区域总行数
	 * @param colCnt
	 *            数据区域总列数
	 * @param firstRowIndex
	 *            数据起始行索引，从0开始
	 * @param firstColIndex
	 *            数据起始列索引，从0开始
	 * @param data
	 *            double[][]型数据
	 * @param decimal
	 *            小数位
	 * @param reserveHidden
	 *            是否保留单元格隐藏内容（"$"及其后的内容）
	 * @param destPath
	 *            导出文件路径
	 */
	public static void export(String templatePath, String sheetName, int lineCnt, int colCnt, int firstRowIndex,
			int firstColIndex, String[][] data, int decimal, boolean reserveHidden, String destPath) {
		// 复制模板到目的路径
		if (!templatePath.equals(destPath)) {
			try {
				File file = createFile(destPath);
				FileUtils.copyFile(new File(templatePath), file);
			} catch (IOException e) {
				LOGGER.error("复制模板失败", e);
			}
		}

		Workbook workbook = null;
		OutputStream out = null;
		try {
			workbook = open(destPath);
			Sheet sheet = workbook.getSheet(sheetName);
			Row row;
			Cell cell;
			String value;
			for (int i = 0; i < lineCnt; i++) {
				row = sheet.getRow(i + firstRowIndex);
				for (int j = 0; j < colCnt; j++) {
					cell = row.getCell(j + firstColIndex);
					value = data[i][j];
					if (StringUtils.isNumeric(value)) { // 数字
						cell.setCellType(CellType.NUMERIC);
						if (value.contains(".")) { // 小数
							cell.setCellValue(Double.parseDouble(value));
						} else { // 整数
							cell.setCellValue(Integer.parseInt(value));
						}
					} else { // 字符串
						if (!reserveHidden && value.contains("$")) {
							value = value.substring(0, value.indexOf("$"));
						}
						cell.setCellType(CellType.STRING);
						cell.setCellValue(value);
					}
				}
			}

			out = new FileOutputStream(new File(destPath));
			workbook.write(out);
			out.flush();
		} catch (IOException e) {
			LOGGER.error("导出至文件失败", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LOGGER.error("关闭输出流异常：\n{}", e.toString());
				}
			}
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 打开指定模板填充数据并导出至文件
	 * 
	 * @param path
	 *            导出文件路径
	 * @param templatePath
	 *            模板路径
	 * @param sheetName
	 *            sheet表名
	 * @param lineCnt
	 *            数据区域总行数
	 * @param colCnt
	 *            数据区域总列数
	 * @param firstRowIndex
	 *            数据起始行索引，从0开始
	 * @param firstColIndex
	 *            数据起始列索引，从0开始
	 * @param data
	 *            double[][]型数据
	 * @param decimal
	 *            小数位
	 */
	public static void export(String path, String templatePath, String sheetName, int lineCnt, int colCnt,
			int firstRowIndex, int firstColIndex, String[][] data, int decimal) {
		Workbook workbook = null;
		OutputStream out = null;
		try {
			workbook = open(templatePath);
			Sheet sheet = workbook.getSheet(sheetName);
			Row row;
			Cell cell;
			String value;
			for (int i = 0; i < lineCnt; i++) {
				row = sheet.getRow(i + firstRowIndex);
				for (int j = 0; j < colCnt; j++) {
					cell = row.getCell(j + firstColIndex);
					value = data[i][j];
					if (StringUtils.isNumeric(value)) { // 数字
						cell.setCellType(CellType.NUMERIC);
						if (value.contains(".")) { // 小数
							cell.setCellValue(Double.parseDouble(value));
						} else { // 整数
							cell.setCellValue(Integer.parseInt(value));
						}
					} else { // 字符串
						value = value.substring(0, value.indexOf("$"));
						cell.setCellType(CellType.STRING);
						cell.setCellValue(value);
					}
				}
			}

			File file = createFile(path);
			out = new FileOutputStream(file);
			workbook.write(out);
			out.flush();
		} catch (IOException e) {
			LOGGER.error("写入文件失败", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 打开指定模板填充数据并导出至response流
	 * 
	 * @param response
	 *            {@link HttpServletResponse}
	 * @param filename
	 *            文件名(不包含后缀)
	 * @param encoding
	 *            字符编码
	 * @param templatePath
	 *            模板路径
	 * @param sheetName
	 *            sheet表名
	 * @param lineCnt
	 *            数据区域总行数
	 * @param colCnt
	 *            数据区域总列数
	 * @param firstRowIndex
	 *            数据起始行索引，从0开始
	 * @param firstColIndex
	 *            数据起始列索引，从0开始
	 * @param data
	 *            double[][]型数据
	 * @param decimal
	 *            小数位
	 */
	public static void export(HttpServletResponse response, String filename, String encoding, String templatePath,
			String sheetName, int lineCnt, int colCnt, int firstRowIndex, int firstColIndex, String[][] data,
			int decimal) {
		try {
			resetResponseToDownload(response, filename, encoding);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("文件名转码失败", e);
			return;
		}

		Workbook workbook = null;
		OutputStream out = null;
		try {
			workbook = open(templatePath);
			Sheet sheet = workbook.getSheet(sheetName);
			Row row;
			Cell cell;
			String value;
			for (int i = 0; i < lineCnt; i++) {
				row = sheet.getRow(i + firstRowIndex);
				for (int j = 0; j < colCnt; j++) {
					cell = row.getCell(j + firstColIndex);
					value = data[i][j];
					if (StringUtils.isNumeric(value)) { // 数字
						cell.setCellType(CellType.NUMERIC);
						if (value.contains(".")) { // 小数
							cell.setCellValue(Double.parseDouble(value));
						} else { // 整数
							cell.setCellValue(Integer.parseInt(value));
						}
					} else { // 字符串
						value = value.substring(0, value.indexOf("$"));
						cell.setCellType(CellType.STRING);
						cell.setCellValue(value);
					}
				}
			}

			out = response.getOutputStream();
			workbook.write(out);
			out.flush();
		} catch (IOException e) {
			LOGGER.error("写入response流失败", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 合并单元格
	 * 
	 * @param sheet
	 *            {@link Sheet}
	 * @param regionArray
	 *            合并区域数组，region形如"A1:E3"表示合并从A1至E3的区域
	 */
	public static void merge(Sheet sheet, String[] regionArray) {
		for (String region : regionArray) {
			sheet.addMergedRegion(formatCellRangeAddress(region));
		}
	}

	/**
	 * 合并单元格
	 * 
	 * @param sheet
	 *            {@link Sheet}
	 * @param regionArray
	 *            合并区域数组，region形如"A1:E3"表示合并从A1至E3的区域
	 */
	public static void merge(Sheet sheet, CellRangeAddress[] regionArray) {
		for (CellRangeAddress region : regionArray) {
			sheet.addMergedRegion(region);
		}
	}

	/**
	 * 获取单元格的值
	 * 
	 * @param cell
	 *            {@link Cell}
	 * @param type
	 *            {@link Class#getName()}
	 * @return
	 */
	public static Object getCellValue(Cell cell, String type) {
		if ("char".equals(type) || "java.lang.String".equals(type)) {
			cell.setCellType(CellType.STRING);
			return cell.getStringCellValue();
		}
		if ("short".equals(type) || "java.lang.Short".equals(type)) {
			cell.setCellType(CellType.STRING);
			String value = cell.getStringCellValue();
			return Short.parseShort(value);
		}
		if ("int".equals(type) || "java.lang.Integer".equals(type)) {
			cell.setCellType(CellType.STRING);
			String value = cell.getStringCellValue();
			return Integer.parseInt(value);
		}
		if ("long".equals(type) || "java.lang.Long".equals(type)) {
			cell.setCellType(CellType.STRING);
			String value = cell.getStringCellValue();
			return Long.parseLong(value);
		}
		if ("float".equals(type) || "java.lang.Float".equals(type)) {
			Double value = cell.getNumericCellValue();
			return Float.parseFloat(value.toString());
		}
		if ("double".equals(type) || "java.lang.Double".equals(type)) {
			return cell.getNumericCellValue();
		}
		if ("boolean".equals(type) || "java.lang.Boolean".equals(type)) {
			return cell.getBooleanCellValue();
		}
		if ("java.util.Date".equals(type)) {
			return cell.getDateCellValue();
		}
		return null;
	}

	/**
	 * 获取单元格的值
	 * 
	 * @param cell
	 *            {@link Cell}
	 * @return
	 */
	public static String getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		cell.setCellType(CellType.STRING);
		if (cell.getCellTypeEnum().equals(CellType.STRING)) {
			String value = cell.getStringCellValue();
			return value;
		}
		if (cell.getCellTypeEnum().equals(CellType.BOOLEAN)) {
			String value = String.valueOf(cell.getBooleanCellValue());
			return value;
		}
		if (cell.getCellTypeEnum().equals(CellType.FORMULA)) {
			String value = cell.getCellFormula();
			return value;
		}
		if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
			String value = String.valueOf(cell.getNumericCellValue());
			return value;
		}
		return "";
	}

	/**
	 * 获取单元格的值
	 * 
	 * @param sheet
	 *            {@link sheet}
	 * @param rowIndex
	 *            行索引
	 * @param colIndex
	 *            列索引
	 * @return
	 */
	public static String getCellValue(Sheet sheet, int rowIndex, int colIndex) {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			return "";
		}
		Cell cell = row.getCell(colIndex);
		if (cell == null) {
			return "";
		}
		String value = getCellValue(cell);
		return value;
	}

	/**
	 * 复制单元格
	 * 
	 * @param srcCell
	 *            源单元格
	 * @param destCell
	 *            目标单元格
	 * @param onlyStyle
	 *            仅复制样式
	 */
	public static void copyCell(Cell srcCell, Cell destCell, boolean onlyStyle) {
		destCell.setCellStyle(srcCell.getCellStyle());
		CellType cellType = srcCell.getCellTypeEnum();
		destCell.setCellType(cellType);
		if (!onlyStyle) {
			switch (cellType) {
			case BOOLEAN:
				destCell.setCellValue(srcCell.getBooleanCellValue());
				break;
			case ERROR:
				destCell.setCellErrorValue(srcCell.getErrorCellValue());
				break;
			case FORMULA:
				destCell.setCellFormula(parseFormula(srcCell.getCellFormula()));
				break;
			case NUMERIC:
				destCell.setCellValue(srcCell.getNumericCellValue());
				break;
			case STRING:
				destCell.setCellValue(srcCell.getRichStringCellValue());
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 复制行
	 * 
	 * @param srcRow
	 *            源行
	 * @param destRow
	 *            目标行
	 * @param colCnt
	 *            列数
	 * @param onlyStyle
	 *            仅复制样式
	 */
	public static void copyRow(Row srcRow, Row destRow, int colCnt, boolean onlyStyle) {
		destRow.setHeight(srcRow.getHeight());

		Cell srcCell, destCell;
		for (int i = 0; i < colCnt; i++) {
			srcCell = srcRow.getCell(i);
			if (srcCell == null) {
				continue;
			}
			destCell = destRow.getCell(i);
			if (destCell == null) {
				destCell = destRow.createCell(i);
			}
			copyCell(srcCell, destCell, onlyStyle);
		}
	}

	/**
	 * 插入行
	 * 
	 * @param sheet
	 *            sheet表对象
	 * @param insertLineIndex
	 *            插入行索引，从0开始
	 * @param styleRow
	 *            样式行对象，新插入行将会复制styleRow的样式
	 * @param styleRowColCnt
	 *            样式行总列数
	 */
	public static void insertRow(Sheet sheet, int insertLineIndex, Row styleRow, int styleRowColCnt) {
		sheet.shiftRows(insertLineIndex, sheet.getLastRowNum(), 1, false, false);
		Row row = sheet.createRow(insertLineIndex);
		copyRow(styleRow, row, styleRowColCnt, true);
	}

	/**
	 * 复制sheet
	 * 
	 * @param srcSheet
	 *            源sheet
	 * @param destSheet
	 *            目标sheet
	 * @param colCnt
	 *            列数
	 */
	public static void copySheet(Sheet srcSheet, Sheet destSheet, int colCnt) {
		// 复制行
		Row srcRow, destRow;
		for (int i = srcSheet.getFirstRowNum(); i <= srcSheet.getLastRowNum(); i++) {
			srcRow = srcSheet.getRow(i);
			destRow = srcSheet.createRow(i);
			copyRow(srcRow, destRow, colCnt, false);
		}

		// 复制合并单元格
		for (int i = 0; i < srcSheet.getNumMergedRegions(); i++) {
			destSheet.addMergedRegion(srcSheet.getMergedRegion(i));
		}
	}

	/**
	 * POI的bug:如果公式里面的函数不带参数，比如now()或today()， 则通过getCellFormula()取出来的值为
	 * now(ATTR(semiVolatile))和today(ATTR(semiVolatile))，这样的值写入Excel时会出错。该方法的功能很简
	 * 单，就是把ATTR(semiVolatile)删掉。
	 * 
	 * @param formula
	 *            公式
	 * @return
	 */
	public static String parseFormula(String formula) {
		final String REPLACE_STR = "ATTR(semiVolatile)";
		formula = formula.replaceAll(REPLACE_STR, "");
		return formula;
	}

	/**
	 * 把形如"A1:E3"形式的合并区域格式化成CellRangeAddress
	 * 
	 * @param region
	 */
	public static CellRangeAddress formatCellRangeAddress(String region) {
		int index = region.indexOf(":");
		int colStart = (int) (region.charAt(0)) - (int) ('A');
		int colEnd = (int) (region.charAt(index + 1)) - (int) ('A');
		int rowStart = Integer.parseInt(region.substring(1, index));
		int rowEnd = Integer.parseInt(region.substring(index + 2));

		CellRangeAddress cra = new CellRangeAddress(rowStart, rowEnd, colStart, colEnd);
		return cra;
	}

	/**
	 * 插入图片
	 * 
	 * @param excelPath
	 *            excel文件路径
	 * @param sheetName
	 *            sheet表名
	 * @param imageBytes
	 *            图片二进制文件
	 * @param firstRowIndex
	 *            起始行，从0开始
	 * @param lastRowIndex
	 *            结束行，从0开始
	 * @param firstColIndex
	 *            起始列，从0开始
	 * @param lastColIndex
	 *            结束列，从0开始
	 */
	public static void insertImage(String excelPath, String sheetName, byte[] imageBytes, int firstRowIndex,
			int lastRowIndex, int firstColIndex, int lastColIndex) {
		Workbook workbook = null;
		OutputStream out = null;
		try {
			workbook = open(excelPath);
			HSSFSheet sheet = (HSSFSheet) workbook.getSheet(sheetName);
			// 画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			// anchor主要用于设置图片的属性
			HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) firstColIndex, firstRowIndex,
					(short) lastColIndex, lastRowIndex);
			anchor.setAnchorType(AnchorType.MOVE_DONT_RESIZE);
			// 插入图片
			patriarch.createPicture(anchor, workbook.addPicture(imageBytes, HSSFWorkbook.PICTURE_TYPE_JPEG));

			out = new FileOutputStream(excelPath);
			workbook.write(out);
			out.flush();
		} catch (IOException e) {
			LOGGER.error("写入文件失败", e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 插入图片
	 * 
	 * @param excelPath
	 *            excel文件路径
	 * @param sheetName
	 *            sheet表名
	 * @param imagePath
	 *            图片文件路径
	 * @param firstRowIndex
	 *            起始行，从0开始
	 * @param lastRowIndex
	 *            结束行，从0开始
	 * @param firstColIndex
	 *            起始列，从0开始
	 * @param lastColIndex
	 *            结束列，从0开始
	 */
	public static void insertImage(String excelPath, String sheetName, String imagePath, int firstRowIndex,
			int lastRowIndex, int firstColIndex, int lastColIndex) {
		byte[] bytes = null;
		try {
			bytes = FileUtils.readFileToByteArray(new File(imagePath));
		} catch (IOException e) {
			LOGGER.error("读取文件失败", e);
		}

		if (bytes != null && bytes.length > 0) {
			insertImage(excelPath, sheetName, bytes, firstColIndex, firstRowIndex, lastColIndex, lastRowIndex);
		}
	}

	/**
	 * 导出EXCEL到指定IO设备
	 * 
	 * @param out
	 *            {@link OutputStream}
	 * @param sheetName
	 *            sheet表名称
	 * @param headers
	 *            标题行(String[])
	 * @param data
	 *            数据(String[][] data)
	 * @throws IOException
	 */
	private static void export(OutputStream out, String sheetName, String[] headers, String[][] data)
			throws IOException {
		Workbook workbook = null;
		try {
			// 声明一个工作薄
			workbook = new HSSFWorkbook();
			// 生成一个表格
			Sheet sheet = workbook.createSheet(sheetName);
			// 生成标题样式
			CellStyle headStyle = getHeadStyle(workbook);
			// 生成记录样式
			CellStyle bodyStyle = getBodyStyle(workbook);

			int i = 0, colCnt = 0;
			// 产生表格标题行
			if (headers != null) {
				Row row = sheet.createRow(i++);
				row.setHeight(DEFAULT_ROW_HEIGHT);
				Cell cell;
				HSSFRichTextString text;
				for (int j = 0; j < headers.length; j++) {
					if (headers[j].startsWith("$")) { // 隐藏列
						continue;
					}

					cell = row.createCell(colCnt++);
					cell.setCellStyle(headStyle);
					text = new HSSFRichTextString(headers[j]);
					cell.setCellValue(text);

					// 设置列宽
					sheet.setColumnWidth(j, DEFAULT_COLUMN_WIDTH);
				}
			}
			// 遍历集合数据，产生数据行
			int height = data.length;
			int width = height > 0 ? data[0].length : 0;
			Row row;
			Cell cell;
			for (int j = 0; j < height; j++) {
				row = sheet.createRow(i++);
				row.setHeight(DEFAULT_ROW_HEIGHT);
				colCnt = 0;
				for (int k = 0; k < width; k++) {
					if (headers[k].startsWith("$")) { // 隐藏列
						continue;
					}

					cell = row.createCell(colCnt++);
					cell.setCellStyle(bodyStyle);
					cell.setCellType(CellType.STRING);
					cell.setCellValue(data[j][k]);
				}
			}

			workbook.write(out);
			out.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 导出EXCEL到指定IO设备
	 * 
	 * @param out
	 *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
	 * @param sheetName
	 *            sheet表名
	 * @param headers
	 *            标题行(String[])
	 * @param dataSet
	 *            需要显示的数据集合，集合中一定要放置符合javabean风格的类的对象
	 * @param dateFormat
	 *            由yyyy年、MM月、dd日、HH小时、mm分钟、ss秒构成的字符串
	 * @throws IOException
	 */
	private static void export(OutputStream out, String sheetName, String[] headers, Collection<?> dataSet,
			String dateFormat) throws IOException {
		String[][] data = convertCollectionToStringArray(dataSet, dateFormat);
		export(out, sheetName, headers, data);
	}

	/**
	 * 导出EXCEL到指定IO设备
	 * 
	 * @param out
	 *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
	 * @param sheetName
	 *            sheet表名
	 * @param headers
	 *            标题行(String[][])，支持合并单元格
	 * @param data
	 *            数据(String[][] data)
	 * @throws IOException
	 */
	private static void export(OutputStream out, String sheetName, String[][] headers, String[][] data)
			throws IOException {
		HSSFWorkbook workbook = null;
		try {
			// 声明一个工作薄
			workbook = new HSSFWorkbook();
			// 生成一个表格
			Sheet sheet = workbook.createSheet(sheetName);
			// 生成标题样式
			CellStyle headStyle = getHeadStyle(workbook);
			// 生成记录样式
			CellStyle bodyStyle = getBodyStyle(workbook);

			List<CellRangeAddress> mergeList = new ArrayList<CellRangeAddress>(); // 合并单元格区域
			CellRangeAddress merge;
			int rowIndex = 0, colIndex;
			Row row;
			Cell cell;
			String[] line;
			String th, span;
			int rowspan, colspan;

			// 产生表格标题行
			for (int i = 0; i < headers.length; i++) {
				row = sheet.createRow(rowIndex);
				row.setHeight(DEFAULT_ROW_HEIGHT);
				line = headers[i];
				colIndex = 0;
				for (int j = 0; j < line.length; j++) {
					th = line[j];
					if (th.startsWith("$")) { // 隐藏列
						continue;
					}

					cell = row.createCell(colIndex);
					cell.setCellStyle(headStyle);
					if (rowIndex == 0) { // 设置列宽
						sheet.setColumnWidth(colIndex, DEFAULT_COLUMN_WIDTH);
					}
					th = th.replaceAll("#", "");
					rowspan = 1;
					colspan = 1;
					if (th.contains("^")) { // 多行
						span = "";
						if (th.contains("*")) { // 多列
							span = th.substring(th.indexOf("^") + 1, th.indexOf("*"));
						} else if (th.indexOf("^") < th.length() - 1) {
							span = th.substring(th.indexOf("^") + 1);
						}
						rowspan = StringUtil.isEmpty(span) ? 1 : Integer.parseInt(span);
						if (rowspan > 1) {
							merge = new CellRangeAddress(rowIndex, rowIndex + rowspan - 1, colIndex, colIndex);
							mergeList.add(merge);
						}
					}
					if (th.contains("*")) { // 多列
						colspan = th.indexOf("*") == th.length() - 1 ? 1
								: Integer.parseInt(th.substring(th.indexOf("*") + 1));
						if (colspan > 1) {
							merge = new CellRangeAddress(rowIndex, rowIndex, colIndex, colIndex + colspan - 1);
							mergeList.add(merge);
						}
					}

					if (th.contains("^")) {
						th = th.substring(0, th.indexOf("^"));
					} else if (th.contains("*")) {
						th = th.substring(0, th.indexOf("*"));
					}
					th = StringUtil.isEmpty(th) || th.contains("&nbsp;") ? "" : th;
					cell.setCellValue(new HSSFRichTextString(th));
					colIndex++;

					while (colspan-- > 1) {
						cell = row.createCell(colIndex);
						cell.setCellStyle(headStyle);
						cell.setCellValue("");
						if (rowIndex == 0) { // 设置列宽
							sheet.setColumnWidth(colIndex, DEFAULT_COLUMN_WIDTH);
						}
						colIndex++;
					}
				}
				rowIndex++;
			}

			// 遍历集合数据，产生数据行
			for (int i = 0; i < data.length; i++) {
				row = sheet.createRow(rowIndex++);
				row.setHeight(DEFAULT_ROW_HEIGHT);
				colIndex = 0;
				for (int j = 0; j < data[i].length; j++) {
					if (headers[headers.length - 1][j].startsWith("$")) { // 隐藏列
						continue;
					}

					cell = row.createCell(colIndex++);
					cell.setCellStyle(bodyStyle);
					cell.setCellType(CellType.STRING);
					cell.setCellValue(data[i][j]);
				}
			}

			// 查询合并单元列
			String old, curr;
			int oldIndex, currIndex;
			colIndex = 0;
			for (int j = 0; j < headers[headers.length - 1].length; j++) {
				th = headers[headers.length - 1][j];
				if (th.startsWith("$")) { // 隐藏列
					continue;
				}

				if (th.contains("#")) { // 此列值相同的行合并单元格
					old = data[0][j];
					oldIndex = 0;
					curr = "";
					currIndex = 0;
					for (int i = 1; i < data.length; i++) {
						curr = data[i][j];
						currIndex = i;
						if (!curr.equals(old)) {
							if (currIndex - oldIndex > 1) {
								merge = new CellRangeAddress(oldIndex + headers.length, currIndex + headers.length - 1,
										colIndex, colIndex);
								mergeList.add(merge);
							}
							old = curr;
							oldIndex = currIndex;
						}
					}
					if (curr.equals(old) && currIndex - oldIndex > 1) {
						merge = new CellRangeAddress(oldIndex + headers.length, currIndex + headers.length, colIndex,
								colIndex);
						mergeList.add(merge);
					}
				}
				colIndex++;
			}

			// 合并单元格
			CellRangeAddress[] mergeArray = new CellRangeAddress[mergeList.size()];
			for (int i = 0; i < mergeList.size(); i++) {
				mergeArray[i] = mergeList.get(i);
			}
			merge(sheet, mergeArray);

			workbook.write(out);
			out.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 导出EXCEL到指定IO设备
	 * 
	 * @param out
	 *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
	 * @param sheetName
	 *            sheet表名
	 * @param headers
	 *            标题行(String[][])，支持合并单元格
	 * @param dataSet
	 *            需要显示的数据集合，集合中一定要放置符合javabean风格的类的对象
	 * @param dateFormat
	 *            由yyyy年、MM月、dd日、HH小时、mm分钟、ss秒构成的字符串
	 * @throws IOException
	 */
	private static void export(OutputStream out, String sheetName, String[][] headers, Collection<?> dataSet,
			String dateFormat) throws IOException {
		String[][] data = convertCollectionToStringArray(dataSet, dateFormat);
		export(out, sheetName, headers, data);
	}

	/**
	 * 获得标题行单元格样式
	 * 
	 * @param workbook
	 *            {@link Workbook}
	 * @return {@link CellStyle}
	 */
	private static CellStyle getHeadStyle(Workbook workbook) {
		// 生成标题样式
		CellStyle headStyle = workbook.createCellStyle();
		// 设置样式
		headStyle.setAlignment(HorizontalAlignment.CENTER);
		headStyle.setBorderBottom(BorderStyle.THIN);
		headStyle.setBorderLeft(BorderStyle.THIN);
		headStyle.setBorderRight(BorderStyle.THIN);
		headStyle.setBorderTop(BorderStyle.THIN);
		headStyle.setFillForegroundColor(HEADER_BACKGROUND_COLOR);
		headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 生成一个字体
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 12);
		font.setBold(false);
		font.setColor(HEADER_FONT_COLOR);
		headStyle.setFont(font);

		return headStyle;
	}

	/**
	 * 获得数据行单元格样式
	 * 
	 * @param workbook
	 *            {@link Workbook}
	 * @return {@link CellStyle}
	 */
	private static CellStyle getBodyStyle(Workbook workbook) {
		// 生成记录样式
		CellStyle bodyStyle = workbook.createCellStyle();
		bodyStyle.setAlignment(HorizontalAlignment.CENTER);
		bodyStyle.setBorderBottom(BorderStyle.THIN);
		bodyStyle.setBorderLeft(BorderStyle.THIN);
		bodyStyle.setBorderRight(BorderStyle.THIN);
		bodyStyle.setBorderTop(BorderStyle.THIN);
		bodyStyle.setFillForegroundColor(HSSFColorPredefined.WHITE.getIndex());
		bodyStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		bodyStyle.setWrapText(true);
		// 生成字体
		Font font2 = workbook.createFont();
		font2.setBold(false);
		// 把字体应用到当前的样式
		bodyStyle.setFont(font2);

		return bodyStyle;
	}

	/**
	 * 把对象集合转成字符串二维数组
	 * 
	 * @param dataSet
	 *            对象集合
	 * @param dateFormat
	 *            由yyyy年、MM月、dd日、HH小时、mm分钟、ss秒构成的字符串
	 * @return {String[][]}
	 */
	private static String[][] convertCollectionToStringArray(Collection<?> dataSet, String dateFormat) {
		// 将Collection<?>集合转成String[][]二维数组
		List<String[]> lineList = new ArrayList<String[]>();
		String[] line;
		int colCnt = 0;
		Iterator<?> it = dataSet.iterator();
		Object obj, value;
		Field[] fields;
		String[] fieldNames;
		Class<?>[] fieldTypes;
		Field field;
		String fieldName, methodName, type;
		Class<?> objClass;
		Method getMethod;
		if (dateFormat == null || dateFormat.trim() == "") {
			dateFormat = "yyyy-MM-dd";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		while (it.hasNext()) {
			obj = it.next();
			if (obj == null) {
				continue;
			}

			// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
			fields = obj.getClass().getDeclaredFields();
			fieldNames = new String[fields.length];
			fieldTypes = new Class<?>[fields.length];
			for (int i = 0; i < fields.length; i++) {
				fieldNames[i] = fields[i].getName();
				fieldTypes[i] = fields[i].getType();
			}

			if (lineList.size() == 0) {
				colCnt = fields.length;
			} else if (colCnt != fields.length) {
				throw new RuntimeException(COLUMN_COUNT_NOT_SAME);
			}

			line = new String[colCnt];
			for (int i = 0; i < fields.length; i++) {
				field = fields[i];
				fieldName = field.getName();
				methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				value = null;

				objClass = obj.getClass();
				try {
					getMethod = objClass.getMethod(methodName);
					value = getMethod.invoke(obj);
				} catch (Exception e) {
					LOGGER.error("获取属性值失败", e);
				}

				if (value == null) {
					line[i] = "";
					continue;
				}

				// 判断值的类型后进行强制类型转换
				type = fieldTypes[i].getName();
				if ("char".equals(type) || "java.lang.String".equals(type)) {
					line[i] = (String) value;
				} else if ("short".equals(type) || "int".equals(type) || "long".equals(type) || "float".equals(type)
						|| "double".equals(type) || value instanceof java.lang.Number) {
					line[i] = String.valueOf(value);
				} else if ("boolean".equals(type) || "java.lang.Boolean".equals(type)) {
					line[i] = (Boolean) value ? "是" : "否";
				} else if ("java.util.Date".equals(type)) {
					line[i] = sdf.format((Date) value);
				}
			}
			lineList.add(line);
		}

		String[][] data = new String[lineList.size()][colCnt];
		for (int i = 0; i < lineList.size(); i++) {
			data[i] = lineList.get(i);
		}
		return data;
	}

	/**
	 * 重置response以下载
	 * 
	 * @param response
	 *            {@link HttpServletResponse}
	 * @param filename
	 *            文件名(不含后缀)
	 * @param encoding
	 *            字符编码
	 * @throws UnsupportedEncodingException
	 */
	private static void resetResponseToDownload(HttpServletResponse response, String filename, String encoding)
			throws UnsupportedEncodingException {
		filename = new String((filename + ".xls").getBytes(encoding), "iso-8859-1");
		response.setContentType("application/vnd.ms-excel;charset=iso-8859-1");
		response.setHeader("Content-disposition", "attachment; filename=" + filename);
	}

	/**
	 * 创建文件
	 * 
	 * @param filename
	 *            文件路径
	 * @throws IOException
	 */
	private static File createFile(String filename) throws IOException {
		File file = new File(filename);
		File dir = new File(file.getParent());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}
}
