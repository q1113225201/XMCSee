/**
 * Android_NetSdk
 * StringUtils.java
 * Administrator
 * TODO
 * 2015-4-10
 */
package com.sjl.xmcsee.lib.sdk.bean;

/**
 * Android_NetSdk
 * StringUtils.java
 * @author huangwanshui
 * TODO
 * 2015-4-10
 */
public class StringUtils {
	/**
	 * �Ա��ַ�
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean contrast(String str1, String str2) {
		if(str1 == null && str2 != null)
			return false;
		else if(str1 != null && str2 == null)
			return false;
		else if(str1 == null && str2 == null)
			return true;
		else return str1.equals(str2);
	}
	public static boolean isStringNULL(String str) {
		return str == null || str.equals("") || str.equals("null");
	}
}
