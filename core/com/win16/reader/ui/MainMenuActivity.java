package com.win16.reader.ui;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.win16.data.GlobalDataManager;
import com.win16.reader.data.Configuration;
import com.win16.reader.data.UserProgressData;
import com.win16.reader.data.UserProgressManager;
import com.win16.reader.annebabytran.R;
import com.win16.reader.ui.adpater.ListViewApater;
import com.win16.reader.ui.widget.CustomerMenu;
import com.win16.reader.annebabytran.SplashAcitivty;
import com.win16.reader.annebabytran.data.Constant;
import com.win16.utils.Tools;

public class MainMenuActivity extends BaseActivity {

	
	

	/** 菜单图片 **/
	int[] menu_image_array = { R.drawable.ylly, R.drawable.fkjz, R.drawable.fhml, R.drawable.jrqq, R.drawable.hdcp, R.drawable.hyjz, R.drawable.sjcp, R.drawable.jgcp };

	/** 菜单文字 **/
	String[] menu_name_array = new String[8]; 
	private CustomerMenu qqmenu;
	
	private static MainMenuActivity instance = null;
	private static final int REQUEST_MENU = 1;
	private ListView listView;

	private int currentSelectPosition = 0;
	private AlertDialog dialog;
	private ListViewApater la;
	private View textEntryView;

	private View seekView;

	private AlertDialog seekDialog;

	private int oldScreenLight;

	private View rootView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if( Tools.setScreenDir(this))
		{
			return;
		}
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		  getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		menu_name_array[0]=getString(R.string.search_category);
		menu_name_array[1]=getString(R.string.font_setup);
		menu_name_array[2]=getString(R.string.advanced_setup);
		menu_name_array[3]=getString(R.string.more_novel);
		menu_name_array[4]=getString(R.string.read_mark);
		menu_name_array[5]=getString(R.string.light_setup);
		menu_name_array[6]=getString(R.string.scroll_speed);
		menu_name_array[7]=getString(R.string.exit_program);
		getWindow().getDecorView().setDrawingCacheEnabled(true);
		setContentView(R.layout.book_content);
		rootView = findViewById(R.id.main_menu_layout);
		int currentFlash = Constant.SPLASH_PIC[(int) (System.currentTimeMillis()%Constant.SPLASH_PIC.length)];
        Drawable flash = getResources().getDrawable(currentFlash);
//        splashview.setBackgroundColor(color);
        rootView.setBackgroundDrawable(flash);
		instance = this;
		listView = (ListView) findViewById(R.id.bookContent);
		
		la = new ListViewApater(this);
		listView.setAdapter(la);
		initMenu();
		ImageView splashLogo = (ImageView) findViewById(R.id.splash_logo);
		splashLogo.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				qqmenu.showQqMenuDialog();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {  
				getScreen();
				UserProgressData data = UserProgressManager.getBooKProgress(position);
				
				Intent i = new Intent(MainMenuActivity.this, ReaderActivity.class);
				i.putExtra("ARTICLE_ID", position);
				i.putExtra("UserProgressData", data);
				startActivity(i);
				finish();
			}
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				currentSelectPosition = arg2;
				createContextMenu();
				return true;
			}
		});
	}

	private void createContextMenu()
	{
		LayoutInflater factory = LayoutInflater.from(this);
		textEntryView = factory.inflate(R.layout.context_menu_on_main, null);
		 dialog = new AlertDialog.Builder(this).setTitle(R.string.mark).setPositiveButton(R.string.ok, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dg, int which) {
				
				RadioGroup rg = (RadioGroup) textEntryView.findViewById(R.id.RadioGroup);
				 int checkedID = rg.getCheckedRadioButtonId();
				 CheckBox all = (CheckBox) textEntryView.findViewById(R.id.checkbox_all);
				 boolean checkedAll = all.isChecked();
				 int status = 0;
				 switch(checkedID)
				 {
				 case R.id.radio_unread:
					 status = Constant.UNREAD;
					 break;
				 case R.id.radio_reading:
					 status = Constant.READING;
					 break;
				 case R.id.radio_readed:
					 status = Constant.READED;
					 break;
				 }
				 if ( checkedAll)
				 {
					 for( int i=0;i< Constant.BOOK_NAME.length;i++)
					 {
						 GlobalDataManager.getInstance().setIntegerData(Constant.READSTATUS+i, status);
					 }
				 }
				 else
				 {
					 GlobalDataManager.getInstance().setIntegerData(Constant.READSTATUS+currentSelectPosition, status);
				 }
				la.notifyDataSetChanged();
			}
		}).setView(textEntryView).setNegativeButton(R.string.cancel, null).create();
		
		 int readstatus = GlobalDataManager.getInstance().getIntegerData(Constant.READSTATUS+currentSelectPosition, Constant.UNREAD);
		 RadioButton rbUnread = (RadioButton) textEntryView.findViewById(R.id.radio_unread);
		 RadioButton rbReading = (RadioButton) textEntryView.findViewById(R.id.radio_reading);
		 RadioButton rbReaded = (RadioButton) textEntryView.findViewById(R.id.radio_readed);
		 switch(readstatus)
		 {
		 case Constant.UNREAD:
			 rbUnread.setChecked(true);
			 break;
		 case Constant.READING:
			 rbReading.setChecked(true);
			 break;
		 case Constant.READED:
			 rbReaded.setChecked(true);
			 break;
		 }
		 dialog.show();
	}
	
//	@Override
//	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		switch (keyCode) {
//		case KeyEvent.KEYCODE_MENU:
////			showCustomerMenu();
//			
//				if (qqmenu.isShowing())
//				{
//					qqmenu.dismiss();
//					return true;
//				}
//			
//			break;
////		case KeyEvent.KEYCODE_BACK:
////			UserProgressData data = getCurrentReadProgress();
////			data.saveAuto();
////			android.os.Process.killProcess(android.os.Process.myPid());
////			break;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

//	private void showCustomerMenu() {
//		Intent i = new Intent(this, OptionMenuActivity.class);
//		i.putExtra("ComeFrom", "MainMenuActivity");
//		startActivityForResult(i,REQUEST_MENU );		
//	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case ReaderActivity.LOAD_MARK:
			if (data == null) {
				break;
			}
			long bookID = data.getLongExtra("markID", -1);
			if (bookID == -1) {
				break;
				// do nothing
			}
			UserProgressData bkdata = UserProgressManager.getProgress(bookID);
			
			if( bkdata != null)
			
			{
				String path = Tools.getBookPath(this, bkdata.getBookId());
				boolean error = false;
				if(!ReaderActivity.getDoc().openFile(path) ) //如果没有找到文件
				{
//					Log.i("Reader", "file not found 1 " + path);
					//临时复制一个
					 path = Tools.copyFile(bkdata.getBookId());
					 if(!ReaderActivity.getDoc().openFile(path)) //还找不到
					 {
//						 Log.i("Reader", "file not found 2 " + path);
						 error = true;
						 AlertDialog  dialog = new AlertDialog.Builder(MainMenuActivity.this).setMessage(R.string.read_file_error).setPositiveButton(R.string.ok, new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								GlobalDataManager.getInstance().setBooleanData("COPYED", false);
								Intent i = new Intent( MainMenuActivity.this, SplashAcitivty.class);
								startActivity(i);
							}
						}).setOnCancelListener(new OnCancelListener() {
							
							@Override
							public void onCancel(DialogInterface dialog) {
								finish();
								android.os.Process.killProcess(Process.myPid());
							}
						}).create();
						 dialog.show();
					 }
					 bkdata.setBookPath(path);
				}
				
				if( error)
				{
					
					finish();
					return;
				}
				ReaderActivity.setArticleContent(ReaderActivity.getDoc().getNext());
				Intent i = new Intent( this, ReaderActivity.class);
				i.putExtra(Constant.MARK, bkdata);
				i.putExtra(Constant.PRE_LOAD, true);
				startActivity(i);
				finish();
			}

			break;
		}
		return;
	}
	
	private void initMenu() {
		qqmenu = new CustomerMenu(this, menu_name_array, menu_image_array, rootView);
		qqmenu.LoadMenuRes();
//		qqmenu.setEnable(0, false);
//		qqmenu.getQqMenuDialog().setOnKeyListener(new OnKeyListener() {
//			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//				if (keyCode == KeyEvent.KEYCODE_MENU)// 监听按键
//				{	dialog.dismiss();
////					pausedScroll = false;
//				}
//				return false;
//			}
//		});
//		
//		qqmenu.getQqMenuDialog().setOnCancelListener(new OnCancelListener() {
//			
//			@Override
//			public void onCancel(DialogInterface dialog) {
////				pausedScroll = false;
//				
//			}
//		});
//		qqmenu.getQqMenuDialog().setOnDismissListener(dismissListener);
		/** 监听menu选项 **/
		qqmenu.getQqMenuGridView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (qqmenu.getQqMenuDialog().isShowing()) {
					qqmenu.getQqMenuDialog().dismiss();
				}
				switch (arg2) {
				case ReaderActivity.FONT_SETUP:
					buildFontDialog();
					break;
				case ReaderActivity.SEARCH_TITLE:
					onSearchRequested();
//					saveUserProgress();
					break;
				case ReaderActivity.LOAD_MARK:
					startActivityForResult(new Intent(MainMenuActivity.this, MarkListActivity.class), ReaderActivity.LOAD_MARK);
					break;
				case ReaderActivity.ADV_SETUP:
					Intent ii = new Intent();
					ii.setClass(MainMenuActivity.this, AdvSetupActivity.class);
					ii.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivityForResult(ii, ReaderActivity.ADV_SETUP);
					break;
				case ReaderActivity.MORE_NOVEL:
//					backToContent();
					Tools.startCategoryActivity(MainMenuActivity.this);
					break;
				case ReaderActivity.LIGHT_SETUP:
					showLightSetup();
					break;
				case ReaderActivity.EXIT_PROGRAME:
					finish();
					break;
				case ReaderActivity.SCROLL_SPEED:
					showScollSpeed();

					break;
				}
			}
		});
	}
	
	private void buildFontDialog() {
//		pausedScroll = true;
		LayoutInflater factory = LayoutInflater.from(this);
		final View fontDialogView = factory.inflate(R.layout.dialog_font_size, null);
		seekDialog = new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_info).setTitle(R.string.font_setup).setView(fontDialogView).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				SeekBar seekbar = (SeekBar) fontDialogView.findViewById(R.id.font_sekkbar);
				int position = seekbar.getProgress();
				seekDialog.dismiss();
				setFont(caculateFontSize(position));

			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				seekDialog.dismiss();
			}
		}).create();

		SeekBar seekbar = (SeekBar) fontDialogView.findViewById(R.id.font_sekkbar);
		seekbar.setMax(15);
		int currentFont = Configuration.getInstance().getFontSize();
		seekbar.setProgress((currentFont - 12) / 2);

		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TextSwitcher textview = (TextSwitcher)
				// fontDialogView.findViewById(R.id.font_switcher);
				TextView tv = (TextView) fontDialogView.findViewById(R.id.font_switcher);
				tv.setTextSize(caculateFontSize(progress));
				tv.setText(caculateFontSize(progress) + getResources().getString(R.string.example));
			}
		});

		TextView tv = (TextView) fontDialogView.findViewById(R.id.font_switcher);
		tv.setTextSize(currentFont);
		tv.setText(currentFont + getResources().getString(R.string.example));
//		seekDialog.setOnDismissListener(dismissListener);
		seekDialog.show();

	}
	
	/**
	 * 设置滚动速度
	 */
	private void showScollSpeed() {
		buildSeeKDialog(getResources().getString(R.string.set_scroll_speed), getResources().getString(R.string.fast_slow),

		new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				SeekBar bar = (SeekBar) seekView.findViewById(R.id.seekbar);
				int position = bar.getProgress();
				// 40-440之间
				int scrollSpeed = Constant.MAX_SCROLL_SPEED - position;
				Configuration.getInstance().setScrollSpeed(scrollSpeed);

				dialog.dismiss();
			}
		}, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}, null, new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TextSwitcher textview = (TextSwitcher)
				// fontDialogView.findViewById(R.id.font_switcher);
				TextView tv = (TextView) seekView.findViewById(R.id.currentPos);
				tv.setText(String.valueOf(progress));
			}
		}, Constant.MAX_SCROLL_SPEED - Constant.MIN_SCROLL_SPEED, Constant.MAX_SCROLL_SPEED -Configuration.getInstance().getScrollSpeed());
	}

	/**
	 * 设置屏幕亮度
	 */
	private void showLightSetup() {
		oldScreenLight = getScreenLight();
		int brightness = oldScreenLight;
		if (oldScreenLight < 0) {

			try {
				brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
//				Log.d("Reader", "brightness" + brightness);
			} catch (Exception e) {
				brightness = -255;
			}

			if (brightness < 0) {
				brightness = 125;
			}

		}
		buildSeeKDialog(getResources().getString(R.string.set_light), getResources().getString(R.string.dark_light),

		new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				SeekBar bar = (SeekBar) seekView.findViewById(R.id.seekbar);
				int position = bar.getProgress();
				// 0-255之间
				setScreenLight(position);
				Configuration.getInstance().setScreenLight(position);
				dialog.dismiss();
			}
		}, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				setScreenLight(oldScreenLight);
				dialog.dismiss();
			}
		}, new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				setScreenLight(oldScreenLight);
			}
		}, new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TextSwitcher textview = (TextSwitcher)
				// fontDialogView.findViewById(R.id.font_switcher);
				TextView tv = (TextView) seekView.findViewById(R.id.currentPos);
				tv.setText(String.valueOf(progress));
				setScreenLight(progress);
			}
		}, 255, brightness);
	}
	
	private void buildSeeKDialog(String title, String imply, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener,
			DialogInterface.OnCancelListener onCancelListener, OnSeekBarChangeListener seekListener, int seekMax, int seekInitPos) {
		
		LayoutInflater factory = LayoutInflater.from(this);
		seekView = factory.inflate(R.layout.dialog_size, null);
		seekDialog = new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_info).setTitle(title).setView(seekView).setPositiveButton(R.string.ok, okListener).setNegativeButton(R.string.cancel,
				cancelListener).setOnCancelListener(onCancelListener).create();

		SeekBar seekbar = (SeekBar) seekView.findViewById(R.id.seekbar);
		seekbar.setMax(seekMax);

		seekbar.setProgress(seekInitPos);

		seekbar.setOnSeekBarChangeListener(seekListener);

		Button add = (Button) seekView.findViewById(R.id.plus);
		
		add.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SeekBar seekbar = (SeekBar) seekView.findViewById(R.id.seekbar);
				seekbar.incrementProgressBy(10);
			}
		});

		Button sub = (Button) seekView.findViewById(R.id.minus);
		sub.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SeekBar seekbar = (SeekBar) seekView.findViewById(R.id.seekbar);
				seekbar.incrementProgressBy(-10);
			}
		});

		TextView tv = (TextView) seekView.findViewById(R.id.currentPos);
		tv.setText(String.valueOf(seekInitPos));
		TextView im = (TextView) seekView.findViewById(R.id.imply);
//		seekDialog.setOnDismissListener(dismissListener);
		im.setText(imply);
		seekDialog.show();

	}
	
	private int getScreenLight() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		oldScreenLight = (int) (lp.screenBrightness * 255);
		return oldScreenLight;
	}
	
	private int caculateFontSize(int position) {
		return position * 2 + 12;
	}

	private void setFont(int fontsize) {
		Configuration.getInstance().setFontSize(fontsize);
	}
	
	@Override
	/**
	 * 拦截MENU
	 */
	public boolean onMenuOpened(int featureId, Menu menu) {
		if( qqmenu == null)
		{
			initMenu();
		}
		qqmenu.showQqMenuDialog();
		
		return false;// 返回为true 则显示系统menu
	}
	@Override
	/**
	 * 创建MENU
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");// 必须创建一项
		return super.onCreateOptionsMenu(menu);
	}
	
	private void showAbout()
	{
		LayoutInflater factory = LayoutInflater.from(this);
		View textEntryView = factory.inflate(R.layout.about, null);
		AlertDialog alert = new AlertDialog.Builder(this).setTitle(R.string.set_about).setView(textEntryView).create();
		alert.show();
	}
	
	@Override
	protected void onDestroy()
	{
		instance = null;
		super.onDestroy();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		Tools.setScreenDir(this);
		
	}
	public static MainMenuActivity getInstance()
	{
		return instance;
	}
	public void getScreen()
	{
		String path = null;
		path = Tools.getStorePath(this, Constant.FILE_PATH);
		path = path + "/device.jpg";
		File file = new File(path);
		if( !file.exists())
		{
			Bitmap bit = getWindow().getDecorView().getDrawingCache();	
			FileOutputStream fos = null;
			try
			{
				fos = new FileOutputStream(file);
				bit.compress(Bitmap.CompressFormat.JPEG, 80, fos);
				fos.flush();
			} catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				if( fos != null)
				{
					try
					{
						fos.close();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
	
	}
}