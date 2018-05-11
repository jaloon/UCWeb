package com.tipray.test;

import java.io.File;
import java.text.DecimalFormat;

/**
 * 获取磁盘分区列表
 * 
 * @author chenlong
 * @version 1.0 2018-02-26
 *
 */
public class DiskTest {
	public static void main(String[] args) {
		File[] roots = File.listRoots();// 获取磁盘分区列表
		for (File file : roots) {
			System.out.println(file.getPath() + "信息如下:");
			long free = file.getFreeSpace();
			long total = file.getTotalSpace();
			long use = total - free;
			System.out.println("空闲未使用 = " + change(free) + "G");// 空闲空间
			System.out.println("已经使用 = " + change(use) + "G");// 可用空间
			System.out.println("总容量 = " + change(total) + "G");// 总空间
			System.out.println("使用百分比 = " + bfb(use, total));
			System.out.println();
		}
	}

	public static long change(long num) {
		// return num;
		return num / 1024 / 1024 / 1024;
	}

	public static String bfb(Object num1, Object num2) {
		double val1 = Double.valueOf(num1.toString());
		double val2 = Double.valueOf(num2.toString());
		if (val2 == 0) {
			return "0.0%";
		} else {
			DecimalFormat df = new DecimalFormat("#0.00");
			return df.format(val1 / val2 * 100) + "%";
		}
	}
}
