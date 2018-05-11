package com.tipray.init.impl;

import com.tipray.core.Callback;
import com.tipray.init.AbstractInitialization;
/**
 * 回调初始化
 * @author chends
 *
 */
public class CallbackInit extends AbstractInitialization {
	private Callback callback;
	public CallbackInit(Callback callback){
		this.callback = callback;
	}
	@Override
	public void init() throws Exception {
		callback.call(null);
	}
	@Override
	public void update() throws Exception {
		init();
	}
}
