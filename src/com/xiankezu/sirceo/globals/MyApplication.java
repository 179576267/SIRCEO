package com.xiankezu.sirceo.globals;


import java.util.Map;

import com.xiankezu.sirceo.tools.ContentUtils;
import com.xiankezu.sirceo.tools.ImageUtils;

import android.annotation.SuppressLint;
import android.app.Application;

/**
 * Created with IntelliJ IDEA. Author: shefenfei Date: 14-10-22 Time: 下午5:21
 * Description: 全局的application，在这里做一些全局框架性的东西 Version: 3.0
 */
public class MyApplication extends Application {


	/**
	 * 数据库操作类
	 */
	public DatabaseHelper mDatabaseHelper;


	/**
	 * 这个才是程序的入口，这个类在不必要的情况下不要做修改
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		// CrashHandler ch = CrashHandler.getInstance();
		// ch.init(this);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		mDatabaseHelper = new DatabaseHelper(this);
		ImageUtils.initImageCache(getApplicationContext());
	}



	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		ContentUtils.printLog(getClass().getSimpleName(), 'e', "onLowMemory");
		super.onLowMemory();
	}

	@SuppressLint("NewApi")
	@Override
	public void onTrimMemory(int level) {
		ContentUtils.printLog(getClass().getSimpleName(), 'e', "onTrimMemory");
		super.onTrimMemory(level);
	}
}
