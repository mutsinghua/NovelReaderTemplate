package com.win16.reader.ui;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;
import com.win16.data.GlobalDataManager;
import com.win16.reader.data.Configuration;
import com.win16.reader.annebabytran.R;
import com.win16.reader.annebabytran.ReadApplication;
import com.win16.reader.annebabytran.data.Constant;
import com.win16.utils.Tools;

public class AdvSetupActivity extends PreferenceActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);		
		if( Tools.setScreenDir(this))
		{
			return;
		}
		setPreferenceScreen(createPreferenceHierarchy());
	}

	private PreferenceScreen createPreferenceHierarchy()
	{
		// Root
		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
		Intent i = null;
		// Inline preferences
		PreferenceCategory inlinePrefCat = new PreferenceCategory(this);
		inlinePrefCat.setTitle(R.string.adv_setup);
		root.addPreference(inlinePrefCat);

		// 隐藏广告
		// CheckBoxPreference hidead = new CheckBoxPreference(this);
		// hidead.setKey(Configuration.KEY_HIDE_AD);
		// hidead.setTitle(R.string.set_hide_ad);
		// hidead.setSummary(R.string.summary_hide_ad);
		// hidead.setDefaultValue(Configuration.getInstance().isHideAD());
		// inlinePrefCat.addPreference(hidead);

		// 自动存档
		CheckBoxPreference togglePref = new CheckBoxPreference(this);
		togglePref.setKey(Configuration.KEY_AUTOSAVE);
		togglePref.setTitle(R.string.set_auto_save);
		togglePref.setSummary(R.string.summary_auto_save);
		togglePref.setDefaultValue(Configuration.getInstance().isAutoSave());
		inlinePrefCat.addPreference(togglePref);

		// 行间距
		// EditTextPreference editTextPref = new EditTextPreference(this);
		// editTextPref.setDialogTitle(R.string.set_line_height);
		// editTextPref.setKey(Configuration.KEY_LINE_HEIGHT);
		// editTextPref.setTitle(R.string.set_line_height);
		// editTextPref.setSummary(R.string.summary_line_height);
		// editTextPref.setDefaultValue(String.valueOf(Configuration.getInstance().getLineHeight()));
		// editTextPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		// editTextPref.getEditText().setSingleLine();

		// inlinePrefCat.addPreference(editTextPref);

		// 滚屏速度
		// EditTextPreference scrollSpeed = new EditTextPreference(this);
		// scrollSpeed.setDialogTitle(R.string.set_scroll_speed);
		// scrollSpeed.setKey(Configuration.KEY_SCROLL_SPEED);
		// scrollSpeed.setTitle(R.string.set_scroll_speed);
		// scrollSpeed.setSummary(R.string.summary_scroll_speed);
		// scrollSpeed.setDefaultValue(String.valueOf(Configuration.getInstance().getScrollSpeed()));
		// scrollSpeed.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		// scrollSpeed.getEditText().setSingleLine();
		// inlinePrefCat.addPreference(scrollSpeed);

		// lightSetting = new EditTextPreference(this);
		// lightSetting.setDialogTitle(R.string.set_light);
		// lightSetting.setKey(Configuration.KEY_SCREEN_LIGHT);
		// lightSetting.setTitle(R.string.set_light);
		// lightSetting.setSummary(R.string.summary_light);
		// lightSetting.setDefaultValue(String.valueOf(getScreenLight()));
		// lightSetting.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		// lightSetting.getEditText().setSingleLine();
		// inlinePrefCat.addPreference(lightSetting);

		// 横竖屏
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm );
		if( dm.widthPixels >= 480) //大于480的才支持横竖屏
		{
		ListPreference landPort = new ListPreference(this);
		landPort.setKey(Configuration.KEY_LAND_PORT);
		landPort.setTitle(R.string.set_land_port);
		landPort.setSummary(R.string.summary_land_port);
		landPort.setEntries(R.array.land_port_list_preference);
		landPort.setEntryValues(R.array.land_port_value_list_preference);
		landPort.setDialogTitle(R.string.set_land_port);
		landPort.setDefaultValue(Configuration.getInstance().getLandPort());
		inlinePrefCat.addPreference(landPort);
		}
		// 全屏
		CheckBoxPreference fullScreen = new CheckBoxPreference(this);
		fullScreen.setKey(Configuration.KEY_FULL_SCREEN);
		fullScreen.setTitle(R.string.set_full_screen);
		fullScreen.setSummary(R.string.summary_full_screen);
		fullScreen.setDefaultValue(Configuration.getInstance().isFullScreen());
		inlinePrefCat.addPreference(fullScreen);

		// 禁用跳转菜单
		CheckBoxPreference disableJump = new CheckBoxPreference(this);
		disableJump.setKey(Configuration.KEY_DISABLE_JUMP);
		disableJump.setTitle(R.string.set_disable_jump_menu);
		disableJump.setSummary(R.string.summary_disable_jump_menu);
		disableJump.setDefaultValue(Configuration.getInstance().isEnableJump());
		inlinePrefCat.addPreference(disableJump);

		// 恢复默认值
		PreferenceScreen intentPref = getPreferenceManager().createPreferenceScreen(this);
		i = new Intent();
		i.setClass(this, this.getClass());
		i.putExtra("target", "restore");
		intentPref.setIntent(i);
		intentPref.setTitle(R.string.set_defalut_value);
		intentPref.setSummary(R.string.summary_defalut_value);
		inlinePrefCat.addPreference(intentPref);

		// 清除缓存
		PreferenceScreen clearCache = getPreferenceManager().createPreferenceScreen(this);
		i = new Intent();
		i.setClass(this, this.getClass());
		i.putExtra("target", "clearCache");
		clearCache.setIntent(i);
		clearCache.setTitle(R.string.set_clear_cache);
		clearCache.setSummary(R.string.summary_clear_cache);
		inlinePrefCat.addPreference(clearCache);

		// 反馈建议
		PreferenceScreen adviser = getPreferenceManager().createPreferenceScreen(this);
		i = new Intent();
		i.setClass(this, this.getClass());
		i.putExtra("target", Constant.ADVISER);
		adviser.setIntent(i);
		adviser.setTitle(R.string.adviser);
		adviser.setSummary(R.string.summary_adviser);
		inlinePrefCat.addPreference(adviser);

		// 联系我们
		PreferenceScreen contactUs = getPreferenceManager().createPreferenceScreen(this);
		i = new Intent();
		i.setClass(this, this.getClass());
		i.putExtra("target", Constant.CONTACT_URI);
		contactUs.setIntent(i);
		contactUs.setTitle(R.string.set_contact_us);
		contactUs.setSummary(R.string.summary_contact_us);
		inlinePrefCat.addPreference(contactUs);

		// 访问网站
		PreferenceScreen visitorUs = getPreferenceManager().createPreferenceScreen(this);
		i = new Intent();
		i.setClass(this, this.getClass());
		i.putExtra("target", Constant.VISIT_URI);
		visitorUs.setIntent(i);
		visitorUs.setTitle(R.string.set_visit_website);
		visitorUs.setSummary(R.string.summary_visit_website);
		inlinePrefCat.addPreference(visitorUs);

		// 分享
		PreferenceScreen share = getPreferenceManager().createPreferenceScreen(this);
		i = new Intent();
		i.setClass(this, this.getClass());
		i.putExtra("target", Constant.SHARE_URI);
		share.setIntent(i);
		share.setTitle(R.string.set_share);
		share.setSummary(R.string.summary_share);
		inlinePrefCat.addPreference(share);
		
		// 关于
		PreferenceScreen about = getPreferenceManager().createPreferenceScreen(this);
		i = new Intent();
		i.setClass(this, this.getClass());
		i.putExtra("target", Constant.ABOUT_URI);
		about.setIntent(i);
		about.setTitle(R.string.set_about);
		about.setSummary(R.string.summary_about);
		inlinePrefCat.addPreference(about);

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ReadApplication.getAppContext());
		sp.registerOnSharedPreferenceChangeListener(preChangerListener);
		return root;
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ReadApplication.getAppContext());
		sp.unregisterOnSharedPreferenceChangeListener(preChangerListener);

		super.onDestroy();
	}

	private OnSharedPreferenceChangeListener preChangerListener = new OnSharedPreferenceChangeListener()
	{

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
		{
			// Log.i("Reader", "onSharedPreferenceChanged1" + key);
			// if (key.equalsIgnoreCase(Configuration.KEY_SCREEN_LIGHT)) {
			// String light =
			// sharedPreferences.getString(Configuration.KEY_SCREEN_LIGHT, "0");
			// try {
			// setScreenLight(Integer.parseInt(light));
			// } catch (Exception ex) {
			// ex.printStackTrace();
			// setScreenLight(0);
			// Toast toast = Toast.makeText(AdvSetupActivity.this, "屏幕亮度有误",
			// Toast.LENGTH_LONG);
			// toast.show();
			// }
			// } else

			if (key.equalsIgnoreCase(Configuration.KEY_AUTOSAVE))
			{
				boolean autoSave = sharedPreferences.getBoolean(Configuration.KEY_AUTOSAVE, true);
				Configuration.getInstance().setAutoSave(autoSave);
				// if( !autoSave)
				// {
				// UserProgressManager.getAutoProgress().delete();
				// }
				// } else if
				// (key.equalsIgnoreCase(Configuration.KEY_SCROLL_SPEED)) {
				// String scrollSpeed =
				// sharedPreferences.getString(Configuration.KEY_SCROLL_SPEED,
				// String.valueOf(Configuration.getInstance().getScrollSpeed()));
				// try {
				// int sp = Integer.parseInt(scrollSpeed.trim());
				// if (sp <= Constant.MAX_SCROLL_SPEED && sp >=
				// Constant.MIN_SCROLL_SPEED) {
				// Configuration.getInstance().setScrollSpeed(sp);
				// } else {
				// Toast toast = Toast.makeText(AdvSetupActivity.this, "滚动速度只能在"
				// + Constant.MIN_SCROLL_SPEED + "和" + Constant.MAX_SCROLL_SPEED
				// + "之间", Toast.LENGTH_LONG);
				// toast.show();
				// }
				// } catch (Exception e) {
				// Toast toast = Toast.makeText(AdvSetupActivity.this, "滚动速度只能在"
				// + Constant.MIN_SCROLL_SPEED + "和" + Constant.MAX_SCROLL_SPEED
				// + "之间", Toast.LENGTH_LONG);
				// toast.show();
				// }

				// } else if
				// (key.equalsIgnoreCase(Configuration.KEY_LINE_HEIGHT)) {
				// String lineHight =
				// sharedPreferences.getString(Configuration.KEY_LINE_HEIGHT,
				// String.valueOf(Configuration.getInstance().getLineHeight()));
				// try {
				// int sp = Integer.parseInt(lineHight.trim());
				// if (sp <= Constant.MAX_LINE_HEIGHT && sp >=
				// Constant.MIN_LINE_HEIGHT) {
				// Configuration.getInstance().setScrollSpeed(sp);
				// } else {
				// Toast toast = Toast.makeText(AdvSetupActivity.this, "行间距只能在"
				// + Constant.MIN_LINE_HEIGHT + "和" + Constant.MAX_LINE_HEIGHT +
				// "之间", Toast.LENGTH_LONG);
				// toast.show();
				// }
				// } catch (Exception e) {
				// Toast toast = Toast.makeText(AdvSetupActivity.this, "行间距只能在"
				// + Constant.MIN_LINE_HEIGHT + "和" + Constant.MAX_LINE_HEIGHT +
				// "之间", Toast.LENGTH_LONG);
				// toast.show();
				// }

			} else if (key.equalsIgnoreCase(Configuration.KEY_DISABLE_JUMP))
			{
				boolean disableJump = sharedPreferences.getBoolean(Configuration.KEY_DISABLE_JUMP, true);
				Configuration.getInstance().setDisableJump(disableJump);
			} else if (key.equalsIgnoreCase(Configuration.KEY_FULL_SCREEN))
			{
				boolean fullScreen = sharedPreferences.getBoolean(Configuration.KEY_FULL_SCREEN, false);
				Configuration.getInstance().setFullScreen(fullScreen);
			} else if (key.equalsIgnoreCase(Configuration.KEY_HIDE_AD))
			{
				boolean hideAd = sharedPreferences.getBoolean(Configuration.KEY_HIDE_AD, false);
				Configuration.getInstance().setHideAD(hideAd);
			} else if (key.equalsIgnoreCase(Configuration.KEY_LAND_PORT))
			{
				Configuration.getInstance().setScreenDir(sharedPreferences.getString(Configuration.KEY_LAND_PORT, Constant.DEFAULT_LAND_SETTING));
				Tools.setScreenDir(AdvSetupActivity.this);
			}
		}
	};

	@Override
	protected void onNewIntent(Intent intent)
	{
		// TODO Auto-generated method stub
		super.onNewIntent(intent);

		String target = intent.getStringExtra("target");
		if (Constant.ABOUT_URI.equals(target))
		{
			showAbout();
		} else if ("restore".equals(target))
		{ // 默认值
			AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(R.string.warning).setPositiveButton(R.string.ok, new OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					BaseActivity.makeToast(0, getResources().getString(R.string.restored), Toast.LENGTH_LONG, AdvSetupActivity.this).show();
					Configuration.getInstance().restoreToDefault();
					GlobalDataManager.getInstance().setBooleanData("COPYED", false);
					setResult(1);
					finish();
				}
			}).setMessage(R.string.restore_to_default).setNegativeButton(R.string.cancel, null).create();
			alertDialog.show();
		} else if ("clearCache".equals(target))
		{ // 清除缓存
			Tools.clearCache(this);
			BaseActivity.makeToast(0, getResources().getString(R.string.cleared_cache), Toast.LENGTH_LONG, AdvSetupActivity.this).show();
		} else if (Constant.ADVISER.equals(target)) // 意见反馈
		{
			MobclickAgent.openFeedbackActivity(this);

		} else if (Constant.CONTACT_URI.equals(target)) // 联系我们
		{
			Intent returnIt = new Intent(Intent.ACTION_SEND);
			String[] tos = { Constant.EMAIL_ADDRESS };
			returnIt.putExtra(Intent.EXTRA_EMAIL, tos);
			returnIt.putExtra(Intent.EXTRA_SUBJECT, android.os.Build.MODEL + "-"+ android.os.Build.VERSION.SDK + "-"+ android.os.Build.VERSION.RELEASE +":"+ "《" + getString(R.string.app_name) + "》" + getString(R.string.adviser));
			returnIt.setType("message/rfc882");
			Intent in = Intent.createChooser(returnIt, getString(R.string.choose_mail_client));
			if (in != null)
			{
				startActivity(in);
			}

		} else if (Constant.VISIT_URI.equals(target))
		{
			Uri myWebSiteUri = Uri.parse(Constant.WEB_SITE);

			Intent returnIt = new Intent(Intent.ACTION_VIEW, myWebSiteUri);
			if (returnIt != null)
			{
				startActivity(returnIt);
			}

		}
		else if( Constant.SHARE_URI.equals(target))
		{
			String s = getPackageName();
			s = s.replace('.', '_');
			String[] name = s.split("_");
			String dir = "";
			if( name.length>1)
			{
				dir = name[name.length-1];
			}
			String path = null;
//			String path = null;
			path = Tools.getStorePath(this, Constant.FILE_PATH);
			path = path + "/device.jpg";
			File file = new File(path);
			Intent sharei = new Intent(Intent.ACTION_SEND);
			sharei.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_title));
			
			sharei.setType("image/*");
//			String url = Constant.WEB_SITE+"product/"+dir+"/device1.png";
			Uri uri = Uri.fromFile(file);
			sharei.putExtra(Intent.EXTRA_STREAM, uri);
//			sharei.put
			String shareText = getString(R.string.share_text);
			shareText = shareText.replace("BOOK_NAME", getString(R.string.app_name));
			shareText = shareText + Constant.WEB_BOOK_BY_PACKAGE_NAME + "?dir="+ dir; 
			sharei.putExtra(Intent.EXTRA_TEXT, shareText);
			sharei.putExtra("sms_body", shareText);
			Intent dd = Intent.createChooser(sharei,  getString(R.string.share_title));
			startActivity(dd);
			
		}
	}

	private void showAbout()
	{
		LayoutInflater factory = LayoutInflater.from(this);
		View textEntryView = factory.inflate(R.layout.about, null);
		AlertDialog alert = new AlertDialog.Builder(this).setTitle(R.string.set_about).setView(textEntryView).create();
		alert.show();
	}
	// private float oldLight = 0;
	//	
	// private int getScreenLight() {
	// WindowManager.LayoutParams lp = getWindow().getAttributes();
	// oldLight = lp.screenBrightness;
	// return (int) (lp.screenBrightness * 255.0f);
	// }
	//
	// private void setScreenLight(int light) {
	//
	// WindowManager.LayoutParams lp = getWindow().getAttributes();
	// lp.screenBrightness = light/255.0f;
	// getWindow().setAttributes(lp);
	// alert = new
	// AlertDialog.Builder(this).setTitle(R.string.summary_light).setMessage("您确定要设置屏幕亮度？ "+10).setPositiveButton(R.string.ok,
	// new OnClickListener() {
	//			
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// handle.removeMessages(0);
	// WindowManager.LayoutParams readerlp = ReaderActivity.getInstance().
	// getWindow().getAttributes();
	// WindowManager.LayoutParams lp = getWindow().getAttributes();
	// readerlp.screenBrightness = lp.screenBrightness;
	// ReaderActivity.getInstance(). getWindow().setAttributes(readerlp);
	//				
	// }
	// }).setNegativeButton(R.string.cancel, new OnClickListener() {
	//			
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// WindowManager.LayoutParams lp = getWindow().getAttributes();
	// lp.screenBrightness = oldLight;
	// getWindow().setAttributes(lp);
	// handle.removeMessages(0);
	//				
	// }
	// }).setOnCancelListener(new OnCancelListener() {
	//			
	// @Override
	// public void onCancel(DialogInterface dialog) {
	// WindowManager.LayoutParams lp = getWindow().getAttributes();
	// lp.screenBrightness = oldLight;
	// getWindow().setAttributes(lp);
	// handle.removeMessages(0);
	//				
	// }
	// }).create();
	// counting = 10;
	// alert.show();
	// handle.sendEmptyMessageDelayed(0, 1000);
	// }
	//
	// private int counting = 10;
	//	
	// Handler handle = new Handler()
	// {
	// public void handleMessage(Message msg)
	// {
	//			
	// alert.setMessage("您确定要设置屏幕亮度？ " + counting--);
	// if( alert!= null && alert.isShowing() && counting ==0)
	// {
	// alert.cancel();
	// }
	// else
	// {
	// handle.sendEmptyMessageDelayed(0, 1000);
	// }
	// }
	// };
	// private AlertDialog alert;
	//
	// private EditTextPreference lightSetting;
}