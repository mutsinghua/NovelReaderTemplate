package com.win16.reader.data;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.win16.reader.annebabytran.data.Constant;
import com.win16.utils.db.SQLiteManager;


/**
 * 存档管理器
 * @author Rex
 *
 */
public class UserProgressManager {

	/**
	 * 获取手动存档记录
	 * @return
	 */
	public static ArrayList<UserProgressData> getUserProgress()
	{
		ArrayList <UserProgressData> listData = new ArrayList<UserProgressData>();
		SQLiteDatabase db = SQLiteManager.getWritableDatabase();
		Cursor marks = db.query(Constant.USER_PROGRESS_TABLENAME, null, "markType=?", new String[]{String.valueOf(UserProgressData.USER_SAVE)}, null, null, null);
		if( marks != null && marks.moveToFirst())
		{
			while(!marks.isAfterLast())
			{
				listData.add(UserProgressData.load(marks));
				marks.moveToNext();
			}
			
		}
		if( marks!=null)
		{
			marks.close();
		}
		return listData;
	}
	
	/**
	 * 获得自动存档记录
	 * @return
	 */
	public static UserProgressData getAutoProgress()
	{
		SQLiteDatabase db = SQLiteManager.getWritableDatabase();
		Cursor marks = db.query(Constant.USER_PROGRESS_TABLENAME, null, "markType=?", new String[]{String.valueOf(UserProgressData.AUTO_SAVE)}, null, null, null);
		UserProgressData data = null;
		if( marks != null && marks.moveToFirst())
		{
			data = UserProgressData.load(marks);
		}
		if( marks!=null)
		{
			marks.close();
		}
		return data;
	}
	/**
	 * 获得书本自动存档记录
	 * @return
	 */
	public static UserProgressData getBooKProgress(int bookID)
	{
		SQLiteDatabase db = SQLiteManager.getWritableDatabase();
		Cursor marks = db.query(Constant.USER_PROGRESS_TABLENAME, null, "markType=? and bookId=?", new String[]{String.valueOf(UserProgressData.BOOK_SAVE),String.valueOf(bookID)}, null, null, null);
		UserProgressData data = null;
		if( marks != null && marks.moveToFirst())
		{
			data = UserProgressData.load(marks);
		}
		if( marks!=null)
		{
			marks.close();
		}
		return data;
	}
	
	/**
	 * 获得自动存档记录
	 * @return
	 */
	public static UserProgressData getProgress(long id)
	{
//		Log.i("Reader","id="+id);
		
		SQLiteDatabase db = SQLiteManager.getWritableDatabase();
		Cursor marks = db.query(Constant.USER_PROGRESS_TABLENAME, null, "_ID=?", new String[]{String.valueOf(id)}, null, null, null);
		UserProgressData data = null;
		if( marks != null && marks.moveToFirst())
		{
			data = UserProgressData.load(marks);
		}
		if( marks!=null)
		{
			marks.close();
		}
		return data;
	}
	
	/**
	 * 通过书签ID删除
	 * @param id
	 */
	public static void deleteByID(long id)
	{
		UserProgressData.delete(id);
	}
	
	public static void deleteByType(int type)
	{
		SQLiteDatabase db = SQLiteManager.getWritableDatabase();
		db.delete(Constant.USER_PROGRESS_TABLENAME, "markType=?", new String[]{String.valueOf(type)});

	}
}