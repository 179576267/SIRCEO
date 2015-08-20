package com.xiankezu.sirceo.globals;

import java.io.File;

/**
 * Author: wangzhenfei Description: 项目中所有的URL
 */
public class URL {
	// 头
	public static final String URL_HEAD = "http://";

	/**
	 * 陈珑 192.168.2.110, 
	 * 谭武 192.168.2.126 
	 * 公司账号 115.159.92.36
	 * 企业服务 115.159.96.124	
	 */
	public static final String IP = "192.168.2.110";
	public static final String IP_RESOURCE = "115.159.96.124";
	/**
	 * 图标ROOT地址,图片端口8080，请勿改
	 */
	public static final String URLROOT = URL.URL_HEAD + URL.IP + ":8080";
	/**
	 * 调试
	 */
	public static boolean ISDEBUG = false;
	/**
	 * 测试地址
	 */
	public static String DEBUG = URL.URLROOT + "/resource/";
	/**
	 * 线上地址
	 */
	public static String ONLINE = URL.URLROOT + "/resource/";

	/**
	 * 地址
	 */
	public static String ROOT = ISDEBUG ? DEBUG : ONLINE;

	/**
	 * 是否是线上地�?
	 */
	public static boolean ISONLINE = ISDEBUG ? false : true;

	/**
	 * 端口号
	 */
	public static int PORT = 8011;
	/**
	 * SD卡存储路径
	 */
	public static String SD_CARD_PATH;

	static {
		SD_CARD_PATH = android.os.Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + "DouQu" + File.separator;
		File file = new File(android.os.Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + "DouQu");
		if (!file.exists()) {
			file.mkdir();
		}

		file = new File(URL.SD_CARD_PATH + "image");
		if (!file.exists()) {
			file.mkdirs();
		}

		file = new File(URL.SD_CARD_PATH + "voice");
		if (!file.exists()) {
			file.mkdirs();
		}
	}
}
