package com.xiankezu.sirceo.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xiankezu.sirceo.globals.URL;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 操作文件/uri/sharedpreference的工具类
 * 
 * @author wangzhenfei
 */
public final class ContentUtils {

	/**
	 * scrollview滑动到最底部
	 * @param scroll
	 * @param inner
	 */
	public static void scrollToBottom(final View scroll) {
		Handler mHandler = new Handler();

		mHandler.post(new Runnable() {
			public void run() {
				if (scroll == null ) {
					return;
				}
				scroll.scrollTo(0, scroll.getHeight());
			}
		});
	}

	/**
	 * 保证两位数字
	 * 
	 * @param n
	 * @return 变换后的数字
	 */
	public static String addZero(int n) {
		return n < 10 ? "0" + n : n + "";
	}

	/**
	 * @param date1
	 *            需要比较的时间 不能为空(null),需要正确的日期格式
	 * @param date2
	 *            被比较的时间 为空(null)则为当前时间
	 * @param stype
	 *            返回值类型 0为多少天，1为多少个月，2为多少年
	 * @return
	 */
	public static int compareDate(String date1, String date2, int stype) {
		int n = 0;

		String[] u = { "天", "月", "年" };
		String formatStyle = stype == 1 ? "yyyy-MM" : "yyyy-MM-dd";

		date2 = date2 == null ? ContentUtils.getCurrentDate() : date2;

		DateFormat df = new SimpleDateFormat(formatStyle);
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		try {
			c1.setTime(df.parse(date1));
			c2.setTime(df.parse(date2));
		} catch (Exception e3) {
			System.out.println("wrong occured");
		}
		// List list = new ArrayList();
		while (!c1.after(c2)) { // 循环对比，直到相等，n 就是所要的结果
			// list.add(df.format(c1.getTime())); // 这里可以把间隔的日期存到数组中 打印出来
			n++;
			if (stype == 1) {
				c1.add(Calendar.MONTH, 1); // 比较月份，月份+1
			} else {
				c1.add(Calendar.DATE, 1); // 比较天数，日期+1
			}
		}

		n = n - 1;

		if (stype == 2) {
			n = (int) n / 365;
		}

		return n;
	}

	// 删除某个表
	public static void deleteField(Context mContext, String whichSp) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		sp.edit().clear().commit();
	}

	/**
	 * 文件转化为字节数组
	 * 
	 * @param file
	 * @param type
	 *            2==图片 3==语音
	 * @return
	 */
	public static byte[] getBytesFromFile(String path, int type) {

		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		if (type == 2) {
			return ImageUtils.getCompressImage(path);
		} else {
			byte[] ret = null;
			try {
				FileInputStream in = new FileInputStream(file);
				ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
				byte[] b = new byte[4096];
				int n;
				while ((n = in.read(b)) != -1) {
					out.write(b, 0, n);
				}
				in.close();
				out.close();
				ret = out.toByteArray();
			} catch (IOException e) {
				// log.error("helper:get bytes from file process error!");
				e.printStackTrace();
			}
			return ret;
		}

	}

	/**
	 * 得到当前日期
	 * 
	 * @return
	 */
	public static String getCurrentDate() {
		Calendar c = Calendar.getInstance();
		Date date = c.getTime();
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
		return simple.format(date);
	}

	/**
	 * 获取当前的版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getCurrentVersionCode(Context context) {
		String versionCode = "";
		PackageManager manager = context.getPackageManager();
		try {
			versionCode = manager.getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return versionCode;
	}

	/**
	 * 根据时间戳获取时间
	 * 
	 * @param timeMill
	 *            微秒数时间戳
	 * @return
	 */
	public static Date getDate(long timeMill) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date(timeMill));
		Date date = ca.getTime();
		return date;
	}

	/**
	 * 获取设备id/imei码
	 * 
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		if (imei == null || imei.equals("")) {
			String did = Settings.Secure.getString(
					context.getContentResolver(), Settings.Secure.ANDROID_ID);
			imei = did;
			if (did == null || did.equals("")) {
				imei = "无法获取到设备id";
			}
		}
		return imei;
	}

	/**
	 * 获得时间间隔
	 * 
	 * @param nowZero
	 * @param history
	 * @return
	 */
	public static String getInterval(long nowZero, long history) {
		if (history > nowZero) {// 大于今天零时 ,属于今天
			return "";
		} else if (history > nowZero - 24 * 60 * 60 && history < nowZero) {// 昨天
			return "昨天	";
		} else {// 大于昨天
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date(history * 1000));
			return ContentUtils.getWeek(calendar.get(Calendar.DAY_OF_WEEK) - 1);
		}
	}

	/**
	 * 获得屏幕像素
	 * 
	 * @param activity
	 */
	public static void getScreenSize(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);
		final float density = displayMetrics.density; // 得到密度
		final float width = displayMetrics.widthPixels;// 得到宽度
		final float height = displayMetrics.heightPixels;// 得到高度
	}

	// 取出whichSp中field字段对应的boolean类型的值
	public static boolean getSharePreBoolean(Context mContext, String whichSp,
			String field) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		boolean i = sp.getBoolean(field, false);
		return i;
	}

	// 取出whichSp中field字段对应的int类型的值
	public static int getSharePreInt(Context mContext, String whichSp,
			String field) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		int i = sp.getInt(field, 0);// 如果该字段没对应值，则取出0
		return i;
	}

	// 取出whichSp中field字段对应的long类型的值
	public static long getSharePreLong(Context mContext, String whichSp,
			String field) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		long s = sp.getLong(field, 0);// 如果该字段没对应值，则取出字符串0
		return s;
	}

	/**
	 * @param mContext上下文
	 *            ，来区别哪一个activity调用的
	 * @param whichSp
	 *            使用的SharedPreferences的名字
	 * @param fieldSharedPreferences的哪一个字段
	 * @return
	 */
	// 取出whichSp中field字段对应的string类型的值
	public static String getSharePreString(Context mContext, String whichSp,
			String field) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		String s = sp.getString(field, "");// 如果该字段没对应值，则取出字符串0
		return s;
	}

	/**
	 * 获取短信验证码
	 * 
	 * @param context
	 */
	public static void getSmsFromPhone(Context context) {
		ContentResolver cr = context.getContentResolver();
		String[] projection = new String[] { "body" };// "_id", "address",
		// "person",, "date",
		// "type
		String where = " address = '10690042229208' AND date >  "
				+ (System.currentTimeMillis() - 10 * 60 * 1000);
		Cursor cur = cr.query(Uri.parse("content://sms/"), projection, where,
				null, "date desc");
		if (null == cur)
			return;
		if (cur.moveToNext()) {
			String number = cur.getString(cur.getColumnIndex("address"));// 手机号
			String body = cur.getString(cur.getColumnIndex("body"));
			// 这里我是要获取自己短信服务号码中的验证码~~
			Pattern pattern = Pattern.compile("(?<!\\d)\\d{6}(?!\\d)");
			Matcher matcher = pattern.matcher(body);
			if (matcher.find()) {
				String res = matcher.group();
				// .substring(1, 11);
			}
		}
		cur.close();
	}

	/**
	 * 获得正在前段运行的activity
	 * 
	 * @param context
	 * @return
	 */
	public static String getTopActivityName(Context context) {
		String topActivityClassName = null;
		ActivityManager activityManager = (ActivityManager) (context
				.getSystemService(Context.ACTIVITY_SERVICE));
		List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager
				.getRunningTasks(1);
		if (runningTaskInfos != null) {
			ComponentName f = runningTaskInfos.get(0).topActivity;
			topActivityClassName = f.getClassName();
		}
		return topActivityClassName;
	}

	/**
	 * 获得星期
	 * 
	 * @param i
	 * @return
	 */
	public static String getWeek(int i) {
		switch (i) {
		case 0:
			return "星期日";

		case 1:
			return "星期一";

		case 2:
			return "星期二";

		case 3:
			return "星期三";

		case 4:
			return "星期四";

		case 5:
			return "星期五";

		case 6:
			return "星期六";

		}
		return "星期七";
	}

	/**
	 * 判断字符串是否含有数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean hasNum(String str) {
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c <= '9' && c >= '0') {
				return true;
			}
		}

		return false;
	}

	/**
	 * 判断点击事件是否在控件范围内
	 * 
	 * @param view
	 * @param ev
	 * @return
	 */
	public static boolean inRangeOfView(View view, MotionEvent ev) {
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int x = location[0];
		int y = location[1];
		if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y
				|| ev.getY() > (y + view.getHeight())) {
			return false;
		}
		return true;
	}

	/**
	 * 判断应用是否在前段运行
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isRunningForeground(Context context) {
		String packageName = context.getPackageName();
		String topActivityClassName = getTopActivityName(context);
		if (packageName != null && topActivityClassName != null
				&& topActivityClassName.startsWith(packageName)) {
			return true;
		} else {
			return false;
		}
	}

	// ----------------------------以下是想着的相机操作--------------------------------

	/**
	 * 判断Service是不是在运行
	 * 
	 * @param mContext
	 * @param className
	 * @return
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);

		if (!(serviceList.size() > 0)) {
			return false;
		}

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	/**
	 * 输出打印信息
	 * 
	 * @param className
	 * @param type
	 * @param message
	 */
	public static void printLog(String className, char type, String message) {
		if (!URL.ISDEBUG) {// 若是线上的则不打印任何信息
			return;
		}
		switch (type) {
		case 'i':
			Log.i(className, message);
			break;
		case 'e':
			Log.e(className, message);
			break;
		case 'd':
			Log.d(className, message);
			break;
		}
	}

	/**
	 * 输出打印信息,默认位i
	 * 
	 * @param className
	 * @param message
	 */
	public static void printLog(String className, String message) {
		if (!URL.ISDEBUG) {// 若是线上的则不打印任何信息
			return;
		}
		Log.i(className, message);

	}

	// 保存boolen类型的value到whichSp中的field字段(主要做登陆状态)
	public static void putSharePreBoolean(Context mContext, String whichSp,
			String field, boolean value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		sp.edit().putBoolean(field, value).commit();
	}

	// 保存int类型的value到whichSp中的field字段
	public static void putSharePreInt(Context mContext, String whichSp,
			String field, int value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		sp.edit().putInt(field, value).commit();
	}

	// 保存long类型的value到whichSp中的field字段
	public static void putSharePreLong(Context mContext, String whichSp,
			String field, long value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		sp.edit().putLong(field, value).commit();
	}

	// 保存string类型的value到whichSp中的field字段
	public static void putSharePreString(Context mContext, String whichSp,
			String field, String value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		sp.edit().putString(field, value).commit();
	}

	/**
	 * 删除莫个字段
	 * 
	 * @param mContext
	 * @param whichSp
	 */
	public static void removeKey(Context mContext, String whichSp,
			String field, int value) {
		SharedPreferences sp = (SharedPreferences) mContext
				.getSharedPreferences(whichSp, 0);
		sp.edit().remove(field).commit();
	}

	/**
	 * 存储图片和语音
	 * 
	 * @param data
	 * @param type
	 *            1 表示图片，2，表示语音
	 * @param name
	 * @param needSave
	 * 
	 */
	public static String saveFileFromByte(byte[] data, int type) {
		File dirFile;
		String name;
		if (type == 1) {
			dirFile = new File(URL.SD_CARD_PATH + "image");
			name = System.currentTimeMillis() + ".jpg";
		} else {
			dirFile = new File(URL.SD_CARD_PATH + "voice");
			name = System.currentTimeMillis() + ".amr";
		}
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}

		File file = new File(dirFile, name);
		// 创建输出流
		FileOutputStream outStream;
		try {
			outStream = new FileOutputStream(file);
			outStream.write(data); // 写入数据
			outStream.close(); // 关闭输出流
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return file.getAbsolutePath();
	}

	/**
	 * 发送自定底义广播
	 * 
	 * @param context
	 * @param intent
	 */
	public static void sendBroast(Context context, Intent intent) {
		context.sendBroadcast(intent);
	}

	// 动态计算listview的高度
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		if (listView == null)
			return;

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	// 根据子listview的item个数动态计算listview的高度
	public static void setListViewHeightBasedOnChildren2(ListView listView,
			int count) {

		if (listView == null)
			return;

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < count; i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (count - 1));
		listView.setLayoutParams(params);
	}

	/**
	 * 计算view的高度
	 * 
	 * @param view
	 */
	public static void setViewHeight(View view, float scale) {
		if (view == null)
			return;

		ViewGroup.LayoutParams params = view.getLayoutParams();
		params.width = (int) (view.getMeasuredWidth() * scale);
		params.height = (int) (view.getMeasuredHeight() * scale);

		view.setLayoutParams(params);
	}

	/**
	 * 一个按钮dialog
	 * 
	 * @param context
	 * @param string
	 * @param listener
	 */
	public static void showDialogForOne(Context context, String string,
			DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);// 上下文只能是activity上下文
		builder.setMessage(string);
		builder.setTitle("提示信息");
		builder.setCancelable(false);
		builder.setPositiveButton("确定", listener);
		builder.show();
	}

	/**
	 * 两个按钮 dialog
	 * 
	 * @param context
	 * @param string
	 * @param listener
	 */
	public static void showDialogForTwo(Context context, String string,
			String sure, String cancle, DialogInterface.OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);// 上下文只能是activity上下文
		builder.setMessage(string);
		builder.setTitle("提示信息");
		builder.setPositiveButton(sure, listener);
		builder.setNeutralButton(cancle, listener);
		builder.setCancelable(false);
		builder.show();
	}

}
