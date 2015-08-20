package com.xiankezu.sirceo.activitys.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.ab.activity.AbActivity;
import com.xiankezu.sirceo.globals.ActivityCollector;
import com.xiankezu.sirceo.globals.MyApplication;
import com.xiankezu.sirceo.tools.NetworkUtils;
import com.xiankezu.sirceo.widghts.FlippingLoadingDialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Author: wangzhenfei 
 * Date: 15-04-09 
 * Time: 16:36 notice:
 * 4.0以后的sdk要使用fragment要使用FragmentActivity
 */
public abstract class BaseActivity extends AbActivity {
	protected FlippingLoadingDialog mLoadingDialog;

	/**
	 * 全局的application
	 */
	protected MyApplication application;

	/**
	 * 网络连接状态
	 */
	public boolean ISCONNECTED;

	protected List<AsyncTask<Void, Void, Boolean>> mAsyncTasks = new ArrayList<AsyncTask<Void, Void, Boolean>>();

	/**
	 * handler，处理消息的接受
	 */
	public Handler globalHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

		}
	};

	protected void clearAsyncTask() {
		Iterator<AsyncTask<Void, Void, Boolean>> iterator = mAsyncTasks
				.iterator();
		while (iterator.hasNext()) {
			AsyncTask<Void, Void, Boolean> asyncTask = iterator.next();
			if (asyncTask != null && !asyncTask.isCancelled()) {
				asyncTask.cancel(true);
			}
		}
		mAsyncTasks.clear();
	}



	/** 初始化视图 **/
	protected abstract void findViews();

	/** 初始化事件 **/
	protected abstract void initSomething();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityCollector.addActivity(this, getClass().getSimpleName());
		ISCONNECTED = NetworkUtils.isNetworkAvailable(this);
		if (!ISCONNECTED) {
			Toast.makeText(this, "当前网络连接不可用", Toast.LENGTH_SHORT).show();
		}
		application = (MyApplication) getApplication();
		mLoadingDialog = new FlippingLoadingDialog(this, "数据加载中...");
	}

	@Override
	protected void onDestroy() {// 一定要记住解除绑定
		ActivityCollector.removeActivity(this);
		clearAsyncTask();
		super.onDestroy();
	}

	/**
	 * 控制物理返回键的使用权 true禁止使用
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}


	protected void putAsyncTask(AsyncTask<Void, Void, Boolean> asyncTask) {
		mAsyncTasks.add(asyncTask.execute());
	}


	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		findViews();
		initSomething();
	}

	/** 含有标题和内容的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message).show();
		return alertDialog;
	}

	/** 含有标题、内容、图标、两个按钮的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message,
			int icon, String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message).setIcon(icon)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener)
				.show();
		return alertDialog;
	}

	/** 含有标题、内容、两个按钮的对话框 **/
	protected AlertDialog showAlertDialog(String title, String message,
			String positiveText,
			DialogInterface.OnClickListener onPositiveClickListener,
			String negativeText,
			DialogInterface.OnClickListener onNegativeClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title)
				.setMessage(message)
				.setPositiveButton(positiveText, onPositiveClickListener)
				.setNegativeButton(negativeText, onNegativeClickListener)
				.show();
		return alertDialog;
	}



	protected void showLoadingDialog(String text) {
		if (text == null) {
			mLoadingDialog = new FlippingLoadingDialog(this, text);
		}
		mLoadingDialog.setText(text);
		mLoadingDialog.show();
	}


	/** 长时间显示Toast提示(来自res) **/
	protected void showLongToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show();
	}

	/** 长时间显示Toast提示(来自String) **/
	protected void showLongToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	/** 短暂显示Toast提示(来自res) **/
	protected void showShortToast(int resId) {
		Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
	}

	/** 短暂显示Toast提示(来自String) **/
	protected void showShortToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/** 通过Class跳转界面 **/
	protected void startActivity(Class<?> cls) {
		startActivity(cls, null);
	}

	/** 含有Bundle通过Class跳转界面 **/
	protected void startActivity(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(this, cls);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}

	/** 通过Action跳转界面 **/
	protected void startActivity(String action) {
		startActivity(action, null);
	}

	/** 含有Bundle通过Action跳转界面 **/
	protected void startActivity(String action, Bundle bundle) {
		Intent intent = new Intent();
		intent.setAction(action);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		startActivity(intent);
	}
}
