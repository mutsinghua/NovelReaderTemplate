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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.win16.data.GlobalDataManager;
import com.win16.reader.data.Configuration;
import com.win16.reader.data.UserProgressData;
import com.win16.reader.data.UserProgressManager;
import com.win16.reader.annebabytran.R;
import com.win16.reader.annebabytran.SplashAcitivty;
import com.win16.reader.annebabytran.data.Constant;
import com.win16.utils.Tools;


public class SplashLogoActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		 getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
	                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		  getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash_logo);
		getWindow().getDecorView().setDrawingCacheEnabled(true);
    	ImageView iv = (ImageView) findViewById(R.id.splash_title);
    	iv.getDrawable().setAlpha(0);
    	Animation fade = AnimationUtils.loadAnimation(this, R.anim.fade);
    	iv.startAnimation(fade);
    	fade.setAnimationListener(new AnimationListener() {
			
			private boolean error;

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				
				UserProgressData data = UserProgressManager.getAutoProgress();
				error = false;
				if( data != null && Configuration.getInstance().isAutoSave())
				
				{
					String path = Tools.getBookPath(getApplicationContext(), data.getBookId());
					if(!ReaderActivity.getDoc().openFile(path) ) //如果没有找到文件
					{
//						Log.i("Reader", "file not found 1 " + path);
						//临时复制一个
						 path = Tools.copyFile(data.getBookId());
						 if(!ReaderActivity.getDoc().openFile(path)) //还找不到
						 {
//							 Log.i("Reader", "file not found 2 " + path);
							 error = true;
							 AlertDialog  dialog = new AlertDialog.Builder(SplashLogoActivity.this).setMessage(R.string.read_file_error).setPositiveButton(R.string.ok, new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									GlobalDataManager.getInstance().setBooleanData("COPYED", false);
									Intent i = new Intent( SplashLogoActivity.this, SplashAcitivty.class);
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
						 data.setBookPath(path);
					}
				
					if( error)
					{
						
						finish();
						return;
					}
					ReaderActivity.setArticleContent(ReaderActivity.getDoc().getNext());
					Intent i = new Intent( SplashLogoActivity.this, ReaderActivity.class);
					i.putExtra(Constant.MARK, data);
					i.putExtra(Constant.PRE_LOAD, true);
					startActivity(i);
					finish();
					return;
					
				}
				
					handler.sendEmptyMessageDelayed(0, 1500);
				
				
			}
		});
		
	}
	

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if( keyCode == KeyEvent.KEYCODE_BACK)
		{
			handler.removeMessages(0);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what)
			{
			case 0:
				Intent i = new Intent( SplashLogoActivity.this, MainMenuActivity.class);
				startActivity(i);
				finish();
				
				break;
			}
		}
		
	};
}