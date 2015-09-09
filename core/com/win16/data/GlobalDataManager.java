package com.win16.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.win16.reader.annebabytran.data.Constant;

/**
 * 数据存储
 * @author rexzou
 *
 */
public class GlobalDataManager {
	
	private Context context;
	
	private static GlobalDataManager instance = null;
	
	private SharedPreferences preferences;

	public static void init(Context context)
	{
		instance = new GlobalDataManager(context);	
	}
	
	public GlobalDataManager(Context context)
	{
		this.context = context;
		setPreferences(context.getSharedPreferences(Constant.PREF_NAME, 0));
		
	}
	
	public void setBooleanData(String key, boolean value)
	{
		Editor editor = getPreferences().edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public boolean getBooleanData(String key, boolean defaultValue)
	{
		return getPreferences().getBoolean(key, defaultValue);
	}
	
	public void setStringData(String key, String value)
	{
		Editor editor = getPreferences().edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public String getStringData(String key, String defaultValue)
	{
		return getPreferences().getString(key, defaultValue);
	}
	
	public static GlobalDataManager getInstance()
	{
		return instance;
	}
	
	public int getIntegerData(String key, int defaultValue)
	{
		return getPreferences().getInt(key, defaultValue);
	}
	
	public void setIntegerData(String key, int value)
	{
		Editor editor = getPreferences().edit();
		editor.putInt(key, value);
		editor.commit();
	}

	private void setPreferences(SharedPreferences preferences) {
		this.preferences = preferences;
	}

	public final SharedPreferences getPreferences() {
		return preferences;
	}
}