package com.sjl.xmcsee.lib.support;

import com.sjl.xmcsee.lib.support.models.FunDevice;

public interface OnFunDeviceConnectListener extends OnFunListener {

	/**
	 * 设备连接成功
	 * @param funDevice 设备对象
	 */
	void onDeviceReconnected(final FunDevice funDevice);
	
	/**
	 * 设备已经断开
	 * @param funDevice
	 */
	void onDeviceDisconnected(final FunDevice funDevice);
}
