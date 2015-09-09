package com.win16.reader.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.win16.data.GlobalDataManager;
import com.win16.reader.annebabytran.data.Constant;

/**
 * 用户配置文件
 * @author Rex
 *
 */
public class Configuration implements Parcelable {

	private static Configuration instance = null;
	
//	public static final String KEY_READBOOK = "KEY_READBOOK";
	
	public static final String KEY_FONTSIZE = "KEY_FONTSIZE";
	
//	public static final String KEY_SCROLLPOSITION = "KEY_SCROLLPOSITION";
	
	public static final String KEY_SCROLL_SPEED = "KEY_SCROLL_SPEED";
	
	public static final String KEY_LINE_HEIGHT = "KEY_LINE_HEIGHT";
	
	public static final String KEY_AUTOSCROLL = "KEY_AUTOSCROLL";
	
	public static final String KEY_AUTOSAVE = "KEY_AUTOSAVE";
	
	public static final String KEY_SCREEN_LIGHT = "KEY_SCREEN_LIGHT";
	
	public static final String KEY_DISABLE_JUMP = "KEY_DISABLE_JUMP";
	
	public static final String KEY_HIDE_AD = "KEY_HIDE_AD";
	
	public static final String KEY_FULL_SCREEN = "KEY_FULL_SCREEN";
	
	public static final String KEY_LAND_PORT = "KEY_LAND_PORT";
//	/**
//	 * 当前看的书
//	 */
//	private int currentReadBook;
	
	/**
	 * 当前的字体大小
	 */
	private int fontSize;
	
//	/**
//	 * 当前窗口位置
//	 */
//	private int scrollPosition;
	
	
	/**
	 * 自动滚屏速度
	 */
	private int scrollSpeed;
	
	/**
	 * 行高
	 */
	private int  lineSpace;
	
	/**
	 * 禁用菜单
	 */
	private boolean disableJump;
	
	/**
	 * 自动存档
	 */
	private boolean autoSave = true;
	
	/**
	 * 屏幕亮度
	 */
	private int screenLight = -1;
	
	/**
	 * 隐藏广告
	 */
	private boolean hideAD = false;
	
	/**
	 * 是否模竖屏
	 */
	private String landOrPort = "port";
	
	private boolean isFullScreen = false;
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void setFontSize(int fontSize) {
		if( this.fontSize!= fontSize)
		{
			this.fontSize = fontSize;
			GlobalDataManager.getInstance().setIntegerData(KEY_FONTSIZE, getFontSize());
		}
	}

	public int getFontSize() {
		return fontSize;
	}

	/**
	 * 横竖屏设置
	 * @param dir land为横屏，port为竖屏
	 */
	public void setScreenDir(String dir)
	{
		if( !landOrPort.equalsIgnoreCase(dir))
		{
			landOrPort = dir;
			GlobalDataManager.getInstance().setStringData(KEY_LAND_PORT, getLandPort());
		}
	}
	
	/**
	 * 是否横屏
	 * @return
	 */
	public boolean isLand()
	{
		if(landOrPort.equalsIgnoreCase("land"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	public static Configuration getInstance ()
	{
		if( instance== null)
		{
			instance = new Configuration();
		}
		return instance;
	}
	
	private Configuration()
	{
		this.isFullScreen=((GlobalDataManager.getInstance().getBooleanData(KEY_FULL_SCREEN, false)));
		this.hideAD=((GlobalDataManager.getInstance().getBooleanData(KEY_HIDE_AD, false)));
		this.fontSize = ( GlobalDataManager.getInstance().getIntegerData(KEY_FONTSIZE, Constant.DEFAULT_FONT_SIZE));
		this.autoSave = (GlobalDataManager.getInstance().getBooleanData(KEY_AUTOSAVE, true));
		this.lineSpace=(GlobalDataManager.getInstance().getIntegerData(KEY_LINE_HEIGHT, Constant.DEFAULT_LINE_HEIGHT));
		this.scrollSpeed=(GlobalDataManager.getInstance().getIntegerData(KEY_SCROLL_SPEED,Constant.DEFAULT_SCROLL_SPEED));
		this.disableJump = (GlobalDataManager.getInstance().getBooleanData(KEY_DISABLE_JUMP, true));
		this.landOrPort = (GlobalDataManager.getInstance().getStringData(KEY_LAND_PORT, Constant.DEFAULT_LAND_SETTING));
	}
	
	public void save()
	{
		GlobalDataManager.getInstance().setBooleanData(KEY_FULL_SCREEN, isFullScreen());
		GlobalDataManager.getInstance().setBooleanData(KEY_HIDE_AD, isHideAD());
		GlobalDataManager.getInstance().setBooleanData(KEY_AUTOSAVE, isAutoSave());
		GlobalDataManager.getInstance().setIntegerData(KEY_FONTSIZE, getFontSize());
		GlobalDataManager.getInstance().setIntegerData(KEY_SCROLL_SPEED, getScrollSpeed());
		GlobalDataManager.getInstance().setIntegerData(KEY_LINE_HEIGHT, getLineHeight());
		GlobalDataManager.getInstance().setBooleanData(KEY_DISABLE_JUMP, disableJump);
		GlobalDataManager.getInstance().setStringData(KEY_LAND_PORT, landOrPort);
	}

	public void setScrollSpeed(int scrollSpeed) {
		if( this.scrollSpeed!= scrollSpeed)
		{
			this.scrollSpeed = scrollSpeed;
			GlobalDataManager.getInstance().setIntegerData(KEY_SCROLL_SPEED, getScrollSpeed());
		}
	}

	public int getScrollSpeed() {
		return scrollSpeed;
	}

	public void setLineHeight(int lineHeight) {
		if( this.lineSpace!= lineHeight)
		{
			this.lineSpace = lineHeight;
			GlobalDataManager.getInstance().setIntegerData(KEY_LINE_HEIGHT, getLineHeight());
		}
	}

	public int getLineHeight() {
		return lineSpace;
	}

	public void setAutoSave(boolean autoSave) {
		if( this.autoSave!= autoSave)
		{
			this.autoSave = autoSave;
			GlobalDataManager.getInstance().setBooleanData(KEY_AUTOSAVE, isAutoSave());
		}
		
	}

	public boolean isAutoSave() {
		return autoSave;
	}

	public void restoreToDefault() {
		setFontSize( Constant.DEFAULT_FONT_SIZE);
		setAutoSave( true);
		setLineHeight(Constant.DEFAULT_LINE_HEIGHT);
		setScrollSpeed(Constant.DEFAULT_SCROLL_SPEED);
		setDisableJump(false);
		setHideAD(false);
		setFullScreen(false);
		setScreenDir(Constant.DEFAULT_LAND_SETTING);
		save();
	}

	public void setDisableJump(boolean disableJump) {
		if( this.disableJump != disableJump)
		{
			this.disableJump = disableJump;
			GlobalDataManager.getInstance().setBooleanData(KEY_DISABLE_JUMP, disableJump);
		}
	}

	public boolean isEnableJump() {
		return disableJump;
	}

	public void setScreenLight(int screenLight) {
		this.screenLight = screenLight;
	}

	public int getScreenLight() {
		return screenLight;
	}

	public void setHideAD(boolean hideAD) {
		if( hideAD != this.hideAD)
		{
			this.hideAD = hideAD;
			GlobalDataManager.getInstance().setBooleanData(KEY_HIDE_AD, isHideAD());
		}
	}

	public boolean isHideAD() {
		return hideAD;
	}

	public void setFullScreen(boolean isFullScreen) {
		if( this.isFullScreen!= isFullScreen)
		{
			this.isFullScreen = isFullScreen;
			GlobalDataManager.getInstance().setBooleanData(KEY_FULL_SCREEN, isFullScreen());
		}
	}

	public boolean isFullScreen() {
		return isFullScreen;
	}

	public void setReadingArticle(int articleId)
	{
		for(int i=0;i<Constant.FILE_LIST.length;i++)
		{
			int readstatus = GlobalDataManager.getInstance().getIntegerData(Constant.READSTATUS+i, Constant.UNREAD);
			if( readstatus == Constant.READING)
			{
				GlobalDataManager.getInstance().setIntegerData(Constant.READSTATUS+i, Constant.READED);
				
			}
			
		}
		GlobalDataManager.getInstance().setIntegerData(Constant.READSTATUS+articleId, Constant.READING);
	}

	/**
	 * 得到横竖屏结果
	 * @return land or port
	 */
	public String getLandPort()
	{
		return landOrPort;
	}
	
	
}