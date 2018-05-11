package com.tipray.init;

/**
 * 初始化
 * 
 * @author chends
 *
 */
public abstract class AbstractInitialization {
	/** 初始 */
	public final static String MODE_CREATE = "create";
	/** 更新 */
	public final static String MODE_UPDATE = "update";

	/**
	 * 执行初始化
	 * 
	 * @throws Exception
	 */
	public abstract void init() throws Exception;

	/**
	 * 执行更新
	 * 
	 * @throws Exception
	 */
	public abstract void update() throws Exception;

	public void execute(String mode) throws Exception {
		if (MODE_CREATE.equals(mode)) {
			init();
		} else if (MODE_UPDATE.equals(mode)) {
			update();
		}
	}
}
