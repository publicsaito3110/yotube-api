package com.yotubeAPI.common;

/**
 * @author saito
 *
 */
public class Util {
	private Util() {
		//インスタンス化を禁止
	}

	public static String chengeEmptyByNull(String value) {

		if (value == null) {
			return "";
		}
		return value;

	}
}
