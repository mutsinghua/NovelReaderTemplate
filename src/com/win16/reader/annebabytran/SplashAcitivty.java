package com.win16.reader.annebabytran;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mobclick.android.MobclickAgent;
import com.win16.data.GlobalDataManager;
import com.win16.reader.annebabytran.R;
import com.win16.reader.ui.BaseActivity;
import com.win16.reader.ui.BookListActivity;
import com.win16.reader.ui.CategoryActivity;
import com.win16.reader.ui.SplashLogoActivity;
import com.win16.reader.annebabytran.data.Constant;
import com.win16.utils.Tools;

public class SplashAcitivty extends BaseActivity {
    

	private long time = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		  getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
    
        splashview = (ImageView) findViewById(R.id.splash_image);
        handler.sendEmptyMessageDelayed(SEND_SPLASH, 2000);
        MobclickAgent.update(this);
    }
    
    /**
     * 进度对话框
     */
    private ProgressDialog pd ;
    
    private Thread thread = new Thread(){
    	public void run()
    	{
    		copyFile();
    	}

		
    };
    
    /**
     * 进度报告
     */
    private static final byte PROCESS_REPORT = 1;
    
    /**
     * 复制完成
     */
    private static final byte PROCESS_DONE = 2;  
    
    /**
     * 闪屏时间到
     */
    private static final byte TIMES_UP = 3;

    /**
     * 第二次闪屏
     */
	protected static final int SEND_SPLASH = 0;
    
    
    private Handler handler = new Handler()
    {
    	public void handleMessage(Message msg)
    	{
    		
    		switch( msg.what)
    		{
    		case SEND_SPLASH:
    			 
    		           
    				    int currentFlash = Constant.SPLASH_PIC[(int) (System.currentTimeMillis()%Constant.SPLASH_PIC.length)];
    			        flash = getResources().getDrawable(currentFlash);
//    		            splashview.setBackgroundColor(color);
    		            splashview.setBackgroundDrawable(flash);
    		        
//    		        doCheck();
    		        time = System.currentTimeMillis();
    		        handler.sendEmptyMessageDelayed(TIMES_UP, 1000);
    			break;
    		case PROCESS_REPORT:
    			pd.setProgress(msg.arg1);
    			break;
    		case PROCESS_DONE: //消失后，继续执行
//    			Log.i("Reader", "PROCESS_DONE");
    			GlobalDataManager.getInstance().setBooleanData("COPYED", true);
    			try
    			{
    				pd.dismiss();
    			}
    			catch(Exception e )
    			{
    				e.printStackTrace();
    			}
    			long time2 = System.currentTimeMillis() - time;
    			if( time2 < 1000 )
    			{
//    				Log.i("Reader", "PROCESS_DONE2");
    				handler.sendEmptyMessageDelayed(TIMES_UP, 1000-time2);
    				
    			}
    			else
    			{
    				 Intent i = new Intent(SplashAcitivty.this, SplashLogoActivity.class);
    	    	      i.putExtra("normal", true);
    	    	        startActivityForResult(i, 0);
    			}
    			
    			break;
    		case TIMES_UP:
//    			Log.i("Reader", "TIMES_UP1");
//    			if(!GlobalDataManager.getInstance().getBooleanData("COPYED", false) )
//    			{
////    				Log.i("Reader", "TIMES_UP3");
//    				return;
//    			}
//    			Log.i("Reader", "TIMES_UP2");
    	        Intent i = new Intent(SplashAcitivty.this, SplashLogoActivity.class);
    	        i.putExtra("normal", true);
    	        startActivityForResult(i, 0);
//    	        overridePendingTransition(R.anim.fade, 0);
//    			startShowTitle();
    			break;
    		}
    	}
    };

	private Drawable flash;

	private ImageView splashview;
    
    
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}

	/**
     * 检查是否需要复制文件 
     */
    private void doCheck()
    {
    	
    	boolean copyed = GlobalDataManager.getInstance().getBooleanData("COPYED", false);
    	if( !copyed)
    	{
    		pd = new ProgressDialog(this);
    		pd.setCancelable(false);
    		pd.setMax(Constant.FILE_LIST.length);
    		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    		pd.setMessage("程序第一次启动，初始化中...");
    		pd.show();
    		thread.start();
    	}
    	handler.sendEmptyMessageDelayed(TIMES_UP, 1000);
    }
    
    /**
     * 开始复制文件
     */
    private void copyFile() {
		for( int i=0;i< Constant.FILE_LIST.length;i++)
		{
			Tools.copyFile(i);
			Message msg = new Message();
			msg.what = PROCESS_REPORT;
			msg.arg1 = i+1;
			handler.sendMessage(msg);
		}
		Message msg = new Message();
		msg.what = PROCESS_DONE;
		
		handler.sendMessage(msg);
	}
    
    

}