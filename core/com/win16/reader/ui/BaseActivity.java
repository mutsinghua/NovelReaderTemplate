package com.win16.reader.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;
import com.win16.reader.data.Configuration;
import com.win16.utils.Tools;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

//		 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
			
		  int light = Configuration.getInstance().getScreenLight();
		 if( light>=0)
		 {
			 setScreenLight(light);
		 }
	}

	@Override
	protected void onPause() {
		
		super.onPause();
		MobclickAgent.onResume(this); 
	}

	@Override
	protected void onResume() {
		super.onResume();
        MobclickAgent.onPause(this); 

	}

	public static Toast makeToast(int typeToast, String msg, int last, Context context)
	{
		
//		Toast toast = new Toast(context);
//		View view = ((Activity) context).getLayoutInflater().inflate(R.layout.readertoast, null);
//
//		// set the text in the view
//		TextView tv = (TextView) view.findViewById(R.id.message);
//		tv.setText(msg);
//		
//		toast.setView(view);
//		
//		toast.setDuration(last);
//		toast.show();
		Toast t = Toast.makeText(context, msg, last);
		return t;
	}
	
	public static Toast makeToast(int typeToast, int resID, int last, Context context)
	{
		
//		Toast toast = new Toast(context);
//		View view = ((Activity) context).getLayoutInflater().inflate(R.layout.readertoast, null);
//
//		// set the text in the view
//		TextView tv = (TextView) view.findViewById(R.id.message);
//		tv.setText(msg);
//		
//		toast.setView(view);
//		
//		toast.setDuration(last);
//		toast.show();
		Toast t = Toast.makeText(context, resID, last);
		t.setGravity(Gravity.CENTER, 0, 0);
		return t;
	}
	
	protected void setScreenLight(int light) {

		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = light/255.0f;
		getWindow().setAttributes(lp);
	}
}