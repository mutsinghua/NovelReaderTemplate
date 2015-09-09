package com.win16.utils.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.win16.reader.data.UserProgressData;
import com.win16.reader.annebabytran.data.Constant;

public class QQDbOpenHelper extends SQLiteOpenHelper {
	

	private static final int DATABASE_VERSION = 2;
	
		
	
	Context context;
	public QQDbOpenHelper(Context context) {
		super(context, Constant.DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		//for table user_progress
		db.execSQL("CREATE TABLE IF NOT EXISTS \""+Constant.USER_PROGRESS_TABLENAME+"\" (" + 
			    "_id INTEGER PRIMARY KEY AUTOINCREMENT," + 
			    "userName INTEGER," + //用户名
			    "bookName TEXT," +  //书名
			    "bookPath TEXT," +  //书路径
			    "bookId INTEGER," + //书ID
			    "scrollPosition INTEGER," + //阅读进度
			    "markType INTEGER," + //书ID
			    "lineCount INTEGER," + //阅读进度
			    "lineHeight INTEGER," + //存档类型  1为自动存档 2为手动存档
			    "saveDate INTEGER,"+ //存档日期
			    "saveReason INTEGER,"+//存档原因 
			    "content TEXT);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		if( oldVersion ==1)
		{
			ArrayList<UserProgressData> a = new ArrayList<UserProgressData>();
			
			Cursor marks = db.query(Constant.USER_PROGRESS_TABLENAME, null, "markType=?", new String[]{String.valueOf(UserProgressData.USER_SAVE)}, null, null, null);
			if( marks != null && marks.moveToFirst())
			{
				while(!marks.isAfterLast())
				{
					UserProgressData data = new UserProgressData();
					
					data.setBookId(marks.getInt(marks.getColumnIndex("bookId")));
					data.setBookName (marks.getString(marks.getColumnIndex("bookName")));
					data.setBookPath ( marks.getString(marks.getColumnIndex("bookPath")));
					data.setContent(marks.getString(marks.getColumnIndex("content")));
					data.setmarkType (marks.getInt(marks.getColumnIndex("markType")));
					data.setScrollPosition (marks.getInt(marks.getColumnIndex("scrollPosition")));
					data.setLineCount ( marks.getInt(marks.getColumnIndex("lineCount")));
					data.setLineHeight ( marks.getInt(marks.getColumnIndex("lineHeight")));
					data.setUserName(marks.getString(marks.getColumnIndex("userName")));
					a.add(data);
					marks.moveToNext();
				}
				
			}
			if( marks!=null)
			{
				marks.close();
			}
//			a.add(UserProgressManager.getAutoProgress());
			marks = db.query(Constant.USER_PROGRESS_TABLENAME, null, "markType=?", new String[]{String.valueOf(UserProgressData.AUTO_SAVE)}, null, null, null);
			if( marks != null && marks.moveToFirst())
			{
				UserProgressData data = new UserProgressData();
				data.setBookId(marks.getInt(marks.getColumnIndex("bookId")));
				data.setBookName (marks.getString(marks.getColumnIndex("bookName")));
				data.setBookPath ( marks.getString(marks.getColumnIndex("bookPath")));
				data.setContent(marks.getString(marks.getColumnIndex("content")));
				data.setmarkType (marks.getInt(marks.getColumnIndex("markType")));
				data.setScrollPosition (marks.getInt(marks.getColumnIndex("scrollPosition")));
				data.setLineCount ( marks.getInt(marks.getColumnIndex("lineCount")));
				data.setLineHeight ( marks.getInt(marks.getColumnIndex("lineHeight")));
				data.setUserName(marks.getString(marks.getColumnIndex("userName")));
				a.add(data);
			}
			if( marks!=null)
			{
				marks.close();
			}
			db.execSQL("DROP TABLE " + Constant.USER_PROGRESS_TABLENAME);
			onCreate(db);
			for( UserProgressData upd: a)
			{
				upd.save(db);
			}
		}
	}
	
	
	
    protected void finalize() {
    	try{
    		this.close();
    	}
    	catch(Exception ex){    		
    	}
    }

    
}