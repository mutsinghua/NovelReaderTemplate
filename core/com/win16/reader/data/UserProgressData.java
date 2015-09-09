package com.win16.reader.data;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import com.win16.reader.annebabytran.R;
import com.win16.reader.annebabytran.ReadApplication;
import com.win16.reader.annebabytran.data.Constant;
import com.win16.utils.db.SQLiteManager;


/**
 * 用户存档数据
 * @author Rex
 *
 */
public class UserProgressData implements Parcelable {
	

	public static final byte AUTO_SAVE = 1;
	public static final byte USER_SAVE = 2;
	public static final byte BOOK_SAVE = 4;
	/*
	 *  "\"_ID\" INTEGER PRIMARY KEY AUTOINCREMENT," + 
			    "\"userName\" INTEGER," + //用户名
			    "\"bookName\" TEXT," +  //书名
			    "\"bookPath\" TEXT," +  //书路径
			    "\"bookId\" INTEGER," + //书ID
			    "\"scrollPosition\" INTEGER," + //阅读进度
			    "\"markType\" INTEGER," + //存档类型  1为自动存档 2为手动存档
			    "\"content\" TEXT);");
	 */
	/**
	 * 
	 */
	private long _ID;  //数据库名称
	private String bookName;  //图书名
	private String bookPath;  //图书路径
	private int bookId;  //图书编号
	private int scrollPosition; //滚动位置
	private int markType = USER_SAVE; //书签类型
	private String userName; //用户名
	private String content; //书签内容
	private int lineCount;  //总行数
	private int lineHeight; //行高
	private long saveDate; //书签保存日期
	private String saveReason; //书签保存原因
	
	
	public UserProgressData()
	{
		
	}

	public long get_ID() {
		return _ID;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

//	public String getBookPath() {
//		return bookPath;
//	}

	public void setBookPath(String bookPath) {
		this.bookPath = bookPath;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public int getScrollPosition() {
		return scrollPosition;
	}

	public void setScrollPosition(int scrollPosition) {
		this.scrollPosition = scrollPosition;
	}

	public int getmarkType() {
		return markType;
	}

	public void setmarkType(int markType) {
		this.markType = markType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	public int getLineCount() {
		return lineCount;
	}

	public void setLineCount(int lineCount) {
		this.lineCount = lineCount;
	}

	public int getLineHeight() {
		return lineHeight;
	}

	public void setLineHeight(int lineHeight) {
		this.lineHeight = lineHeight;
	}

	/**
	 * 保存
	 */
	public void save()
	{
		SQLiteDatabase db = SQLiteManager.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("bookName", bookName);
		cv.put("bookPath", bookPath);
		cv.put("bookId", bookId);
		cv.put("scrollPosition", scrollPosition);
		cv.put("markType", markType);
		cv.put("content", content);
		cv.put("lineCount", lineCount);
		cv.put("lineHeight", lineHeight);
		cv.put("userName", userName);
		cv.put("saveDate", saveDate);
		cv.put("saveReason", saveReason);
		_ID=db.insert(Constant.USER_PROGRESS_TABLENAME, null, cv);
	
	}
	
	/**
	 * 保存
	 */
	public void save(SQLiteDatabase db )
	{
		ContentValues cv = new ContentValues();
		cv.put("bookName", bookName);
		cv.put("bookPath", bookPath);
		cv.put("bookId", bookId);
		cv.put("scrollPosition", scrollPosition);
		cv.put("markType", markType);
		cv.put("content", content);
		cv.put("lineCount", lineCount);
		cv.put("lineHeight", lineHeight);
		cv.put("userName", userName);
		cv.put("saveDate", saveDate);
		cv.put("saveReason", saveReason);
		_ID=db.insert(Constant.USER_PROGRESS_TABLENAME, null, cv);
	
	}
	
	public void saveBookMark()
	{
		
	}
	
	/**
	 * 从数据库中读取数据
	 * @param cursor
	 * @return
	 */
	public static UserProgressData load(Cursor cursor)
	{
		UserProgressData data = new UserProgressData();
		data._ID = cursor.getLong(0);
		data.bookId = cursor.getInt(cursor.getColumnIndex("bookId"));
		data.bookName = cursor.getString(cursor.getColumnIndex("bookName"));
		data.bookPath = cursor.getString(cursor.getColumnIndex("bookPath"));
		data.content = cursor.getString(cursor.getColumnIndex("content"));
		data.markType = cursor.getInt(cursor.getColumnIndex("markType"));
		data.scrollPosition = cursor.getInt(cursor.getColumnIndex("scrollPosition"));
		data.lineCount = cursor.getInt(cursor.getColumnIndex("lineCount"));
		data.lineHeight = cursor.getInt(cursor.getColumnIndex("lineHeight"));
		data.userName = cursor.getString(cursor.getColumnIndex("userName"));
		data.saveDate = cursor.getLong(cursor.getColumnIndex("saveDate"));
		data.saveReason = cursor.getString(cursor.getColumnIndex("saveReason"));
		return data;
	}
	
	/**
	 * 删除书签
	 */
	public void delete()
	{
		SQLiteDatabase db = SQLiteManager.getWritableDatabase();
		db.delete(Constant.USER_PROGRESS_TABLENAME, "_ID=?", new String[]{String.valueOf(_ID)});
	}
	
	public static void delete(long id)
	{
		SQLiteDatabase db = SQLiteManager.getWritableDatabase();
		db.delete(Constant.USER_PROGRESS_TABLENAME, "_ID=?", new String[]{String.valueOf(id)});
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return (bookId+" " + scrollPosition).hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this._ID);
		dest.writeString(this.userName);
		dest.writeString(this.bookName);
		dest.writeString(this.bookPath);
		dest.writeInt(this.bookId);
		dest.writeInt(this.scrollPosition);
		dest.writeInt(this.markType);
		dest.writeInt(this.lineCount);
		dest.writeInt(this.lineHeight);
		dest.writeString(this.content);
		dest.writeLong(this.saveDate);
		dest.writeString(this.saveReason);
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}
	
	 public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { 
	        public UserProgressData createFromParcel(Parcel in) 
	        { 
	        	UserProgressData upd=new UserProgressData(); 
	        	upd._ID = in.readLong();
	        	upd.userName= in.readString();
	        	upd.bookName=in.readString();
	        	upd.bookPath=in.readString();
	        	upd.bookId= in.readInt();
	        	upd.scrollPosition= in.readInt();
	        	upd.markType= in.readInt();
	        	upd.lineCount =in.readInt();
	        	upd.lineHeight= in.readInt();
	        	upd.content= in.readString();
	        	upd.saveDate = in.readLong();
	        	upd.saveReason = in.readString();
	            return upd; 
	        } 

	        public UserProgressData[] newArray(int size) 
	        { 
	            return new UserProgressData[size]; 
	        } 
	    };

	   /**
	    * 保存为自动类型
	    */
	public void saveAuto(int type) {
		SQLiteDatabase db = SQLiteManager.getWritableDatabase();
		ContentValues cv = new ContentValues();
		markType = type;
		cv.put("bookName", bookName);
		cv.put("bookPath", bookPath);
		cv.put("bookId", bookId);
		cv.put("scrollPosition", scrollPosition);
		cv.put("markType", markType);
		cv.put("content", content);
		cv.put("lineCount", lineCount);
		cv.put("lineHeight", lineHeight);
		cv.put("userName", userName);
		cv.put("saveDate", saveDate);
		
		if (markType == AUTO_SAVE) {
			cv.put("saveReason", ReadApplication.getAppContext().getString(R.string.last_read));
			int i = db.update(Constant.USER_PROGRESS_TABLENAME, cv, "markType=?", new String[] { String.valueOf(UserProgressData.AUTO_SAVE) });
			if (i == 0) {
				_ID = db.insert(Constant.USER_PROGRESS_TABLENAME, null, cv);
			}
		} else if(markType == BOOK_SAVE){
			cv.put("saveReason", ReadApplication.getAppContext().getString(R.string.last_book_read));
			int i = db.update(Constant.USER_PROGRESS_TABLENAME, cv, "markType=? and bookId=?", new String[] { String.valueOf(UserProgressData.BOOK_SAVE),String.valueOf(bookId) });
			if (i == 0) {
				_ID = db.insert(Constant.USER_PROGRESS_TABLENAME, null, cv);
			}
		}
	}


	public long getSaveDate() {
		return saveDate;
	}



	public String getSaveReason() {
		return saveReason;
	}

	public void setSaveDate(long date)
	{
		this.saveDate = date;
	}
	
	public void setSaveReason(String reason)
	{
		this.saveReason = reason;
	}

}