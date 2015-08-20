package com.xiankezu.sirceo.globals;

import android.app.Activity;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Author: wagzhenfei
 * Description:管理所有的栈中的Activity
 */
public class ActivityCollector {

    /**
     * 存放activity的列表
     */
    public static HashMap<String, Activity> activities = new  HashMap<String, Activity>();

    /**
     * 添加Activity
     * @param activity
     */
    public static void addActivity(Activity activity,String name){
        activities.put(name, activity);
    }

    /**
     * 获得指定activity实例
     * @param str
     * @return
     */
    public  static Activity getActivity(String str){
    	return activities.get(str);
    }

    /**
     * 移除activity
     * @param activity
     */
    public static void removeActivity(Activity activity){
    	activity.finish();
        activities.remove(activity);
    }
    /**
     * 移除所有的Activity
     */
    public static void removeAllActivity(){
        if (activities!=null && activities.size()>0){
        	 Set<Entry<String, Activity>> sets= activities.entrySet();
        	 for (Entry<String, Activity> s:sets) {
				if (!s.getValue().isFinishing()) {
					s.getValue().finish();
				}
			}
        }
        activities.clear();
    }
}
