package com.sjl.xmcsee.lib.support.config;

public interface JsonListener {
	String getSendMsg();

	boolean onParse(String json);
}
