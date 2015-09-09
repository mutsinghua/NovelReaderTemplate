package com.win16.reader.ui;




import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.win16.reader.data.UserProgressData;
import com.win16.reader.data.UserProgressManager;
import com.win16.reader.annebabytran.R;
import com.win16.reader.annebabytran.data.Constant;
import com.win16.utils.Tools;
import com.win16.utils.db.SQLiteManager;

public class MarkListActivity extends ListActivity {

	MarkListViewApdater markApdater;
	
	private int currentType = -1;
	
	SimpleDateFormat sdfTime = new SimpleDateFormat("", Locale.CHINESE);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		if( Tools.setScreenDir(this))
		{
			return;
		}
		getData(UserProgressData.USER_SAVE);
		sdfTime.applyPattern("yyyy年MM月dd日 HH:mm:ss");	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, R.string.menu_all_mark, 0, R.string.menu_all_mark);
		menu.add(Menu.NONE, R.string.menu_auto_mark, 0, R.string.menu_auto_mark);
		menu.add(Menu.NONE, R.string.menu_user_mark, 0, R.string.menu_user_mark);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 设置书签类型
	 * @param type see UserProgressData
	 */
	private void setDataType(int type)
	{
		if( marks != null)
		{
			marks.close();
		}
		switch(type)
		{
		case UserProgressData.AUTO_SAVE:
			marks = SQLiteManager.getWritableDatabase().query(Constant.USER_PROGRESS_TABLENAME, null, "markType=? or markType=?", new String[]{String.valueOf(UserProgressData.AUTO_SAVE),String.valueOf(UserProgressData.BOOK_SAVE)}, null, null, "markType");
			break;
		case UserProgressData.USER_SAVE:
			marks = SQLiteManager.getWritableDatabase().query(Constant.USER_PROGRESS_TABLENAME, null, "markType=?", new String[]{String.valueOf(UserProgressData.USER_SAVE)}, null, null, "_ID desc");
			break;
		default:
			marks = SQLiteManager.getWritableDatabase().query(Constant.USER_PROGRESS_TABLENAME, null, null, null, null, null, "markType");
		}
		if( marks!=null && marks.isAfterLast())
		{
			BaseActivity.makeToast(0, getResources().getString(R.string.no_mark_available), Toast.LENGTH_LONG, this).show();
		}
	}
	
	private void getData(int type)
	{
		currentType = type;
		getListView().setAdapter(null);
		setDataType(type);
		markApdater = new MarkListViewApdater(this, marks);
		getListView().setAdapter(markApdater);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 
		if( keyCode == KeyEvent.KEYCODE_BACK)
		{
			setResult(-1);
			finish();
			return true;
		}
		else
		{
			return super.onKeyDown(keyCode, event);
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case R.string.menu_user_mark:
			getData(UserProgressData.USER_SAVE);
			break;
		case R.string.menu_auto_mark:
			getData(UserProgressData.AUTO_SAVE);
			break;
		case R.string.menu_all_mark:
			getData(UserProgressData.AUTO_SAVE|UserProgressData.USER_SAVE|UserProgressData.BOOK_SAVE);
			break;
			
		}
		
		return true;
	}



	class MarkListViewApdater extends  CursorAdapter
	{

		LayoutInflater mInflater ;
		public MarkListViewApdater(Context context, Cursor c) {
			super(context, c);
			mInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			if( cursor!= null)
			{
				long countLine = cursor.getLong(cursor.getColumnIndex("lineCount"));
				long lineHeight = cursor.getLong(cursor.getColumnIndex("lineHeight"));
				long curpos = cursor.getLong(cursor.getColumnIndex("scrollPosition"));
				float progress =(curpos * 100.0f / (countLine * lineHeight));
				
				long _ID = cursor.getLong(0);
				//书名
				View layout = view.findViewById(R.id.mark_touch);
				layout.setTag(Long.valueOf(_ID));
				layout.setOnClickListener(clickListenertext);
				registerForContextMenu(layout);
				
				String name = cursor.getString(cursor.getColumnIndex("bookName"));
				TextView tv = (TextView) view.findViewById(R.id.book_name);
				tv.setText(name);
//				tv.setTag(Long.valueOf(_ID));
//				tv.setOnClickListener(clickListenertext);
						
				//保存原因
				String reason = cursor.getString(cursor.getColumnIndex("saveReason"));
				tv = (TextView) view.findViewById(R.id.save_reason);
				tv.setText(reason);
				
				//进度
				tv = (TextView) view.findViewById(R.id.book_progress);
				tv.setText(String.format("%1$3.2f", progress)+"%");
//				tv.setTag(Long.valueOf(_ID));
//				tv.setOnClickListener(clickListenertext);
				Button bt = (Button) view.findViewById(R.id.delete_marked);
				bt.setTag(Long.valueOf(_ID));
				bt.setOnClickListener(clickListener);
				
				//日期
				long date = cursor.getLong(cursor.getColumnIndex("saveDate"));
				tv = (TextView) view.findViewById(R.id.save_date);
				if( date == 0)
				{
					tv.setText(R.string.longlongago);
				}
				else
				{
					tv.setText(sdfTime.format(date));
				}
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return mInflater.inflate(R.layout.mark_list_item, null);
			
		}
		
	}
	
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			long id = (Long) v.getTag();
			UserProgressManager.deleteByID(id);
			getData(currentType);
		}
	};

	private OnClickListener clickListenertext = new OnClickListener() {

		@Override
		public void onClick(View view) {
			view.setBackgroundColor(Color.DKGRAY);
			Intent i = new Intent();
			
			long _id = (Long) view.getTag();
			i.putExtra("markID", _id);
			setResult(0,i);
			finish();
		}
	};
	
		private Cursor marks;
	
	@Override
	protected void onDestroy() {
		
		if( marks!=null)
		{
			marks.close();
		}
		super.onDestroy();
		
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch(item.getItemId())
		{
		case R.string.delete_all:
			AlertDialog dg= new AlertDialog.Builder(this).setTitle(R.string.warning).setMessage(R.string.delete_confirm).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch(currentType)
					{
					case UserProgressData.AUTO_SAVE:
						UserProgressManager.deleteByType(UserProgressData.AUTO_SAVE);
						UserProgressManager.deleteByType(UserProgressData.BOOK_SAVE);
						
						break;
					case UserProgressData.USER_SAVE:
						UserProgressManager.deleteByType(UserProgressData.USER_SAVE);
						
						break;
					default:
						UserProgressManager.deleteByType(UserProgressData.AUTO_SAVE);
						UserProgressManager.deleteByType(UserProgressData.BOOK_SAVE);
						UserProgressManager.deleteByType(UserProgressData.USER_SAVE);
					}
					getData(currentType);				
				}
			}).setNegativeButton(R.string.cancel, null).create();
			dg.show();
			break;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(Menu.NONE, R.string.delete_all, 2, R.string.delete_all);
		menu.setHeaderTitle(R.string.menu_imply);
		
	}
}