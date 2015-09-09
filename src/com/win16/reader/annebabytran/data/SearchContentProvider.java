package com.win16.reader.annebabytran.data;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;


/**
 * 用于搜索目录
 * @author Rex
 *
 */
public class SearchContentProvider extends ContentProvider
{
	public static String AUTHORITY = "com.win16.reader.annebabytran.data.SearchContentProvider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/category");
	 
	public static final String BOOK_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
	public static final String BOOK_ID = "_ID";
	public static final String BOOK_SUGGEST = SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID;
	
	private static String[]columns = new String[]{BOOK_ID,BOOK_NAME,BOOK_SUGGEST}; 
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
	{
		if(selectionArgs == null||selectionArgs[0].equals("")){
			return null;
		}
		//selectionargs中各关键字是或的关系
//		ArrayList<BookCategoryDataSource> source = new ArrayList<BookCategoryDataSource>();
		MatrixCursor cursor = new MatrixCursor(columns);
		for (int i = 0; i < Constant.BOOK_NAME.length; i++) //搜索，
		{
			for (int j = 0; j < selectionArgs.length; j++)
			{

				if (Constant.BOOK_NAME[i].indexOf(selectionArgs[j]) >= 0)
				{
//					source.add(new BookCategoryDataSource(Constant.BOOK_NAME[i], i));
					cursor.addRow(new Object[]{i,Constant.BOOK_NAME[i],i});
				}
			}
		}
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}