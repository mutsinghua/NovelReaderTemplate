package com.win16.reader.annebabytran;

import android.app.Application;
import android.content.Context;

import com.win16.data.GlobalDataManager;
import com.win16.reader.MainController;
import com.win16.reader.data.DynamicConfigure;
import com.win16.reader.annebabytran.data.Constant;
import com.win16.utils.db.SQLiteManager;

/**
 * @version
 * @author Rex
 *
 */
public class ReadApplication extends Application {

	private  static Context appContext;
	

	

	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		appContext = getApplicationContext();
		GlobalDataManager.init(getApplicationContext());
		SQLiteManager.setContext(getApplicationContext());
//		ExceptionLog.register(getApplicationContext());
		MainController.initInstance(appContext);
		//为广告
//		AdManager.init(Constant.YOUMI_ID, Constant.YOUMI_KEY, 30, false); 
		com.madhouse.android.ads.AdManager.setApplicationId(appContext, Constant.SMARTMAD_ID);
		DynamicConfigure.init();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		SQLiteManager.closeDatabase();
		MainController.close();
	}


	public static Context getAppContext() {
		return appContext;
	}
}