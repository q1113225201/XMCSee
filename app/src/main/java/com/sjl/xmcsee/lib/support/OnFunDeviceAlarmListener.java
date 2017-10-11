package com.sjl.xmcsee.lib.support;

import com.sjl.xmcsee.lib.support.config.AlarmInfo;
import com.sjl.xmcsee.lib.support.models.FunDevice;

import java.util.List;

public interface OnFunDeviceAlarmListener extends OnFunListener {

	// 设备状态发生变化
	void onDeviceAlarmReceived(final FunDevice funDevice);
	
	// 搜索历史报警消息成功
	void onDeviceAlarmSearchSuccess(final FunDevice funDevice, final List<AlarmInfo> infos);
	
	// 搜索历史报警消息失败
	void onDeviceAlarmSearchFailed(final FunDevice funDevice, final int errCode);
	
	// 接收到一个局域网报警信息
	void onDeviceLanAlarmReceived(final FunDevice funDevice, final AlarmInfo alarmInfo);
}
