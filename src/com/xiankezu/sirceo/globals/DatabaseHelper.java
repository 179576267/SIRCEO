package com.xiankezu.sirceo.globals;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA. Author: shefenfei Date: 14-10-30 Time: 上午10:23
 * Description:数据库工具类，使用ormlite进行ORM框架封装 Version: 3.0
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	/**
	 * 数据�?
	 */
	private static final String DATABASE_NAME = "douqu.db";
	/**
	 * 数据库版本号
	 */
	private static final int DATABASE_VERSION = 1;

	private SQLiteDatabase sqLiteDatabase;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void clearTable() {
//		try {
//			TableUtils.dropTable(getConnectionSource(), ChatMessage.class,
//					true);
//			onCreate(sqLiteDatabase, getConnectionSource());
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	/**
	 * 获取传入对象的数据库操作对象
	 * 
	 * @param t
	 * @param <T>
	 * @return
	 * @throws SQLException
	 */
	public <T> Dao<T, Integer> getObjectDao(Class<T> t) throws SQLException {
		return getDao(t);
	}

	/**
	 * 首次创建数据库的时�?会调�?
	 * 
	 * @param sqLiteDatabase
	 * @param connectionSource
	 */
	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase,
			ConnectionSource connectionSource) {
		this.sqLiteDatabase = sqLiteDatabase;
//		try {
//			TableUtils.createTable(connectionSource, ChatMessage.class);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * 数据库版本号变化的时候调�?
	 * 
	 * @param sqLiteDatabase
	 * @param connectionSource
	 * @param oldVersion
	 * @param newVersion
	 */
	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

}
