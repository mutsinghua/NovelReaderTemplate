package com.win16.reader.ui.widget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.win16.reader.core.xml.data.AdItem;
import com.win16.reader.data.DynamicConfigure;
import com.win16.reader.annebabytran.R;
import com.win16.reader.annebabytran.ReadApplication;
import com.win16.utils.Tools;

public class CustomerMenu {
	
	private Activity mContext = null;
//	Dialog menuDialog;// menu�˵�Dialog
	PopupWindow menuDialog;
	GridView menuGrid;
	View menuView;
	View rootView;
	String[] mMenuNameArray = null;
	int[] mImageResourceArray = null;
	private TextView menuTime;
	private SimpleDateFormat sdfDay;
	private TextView adText;
	
	public CustomerMenu(Context context, String[] menuNameArray,
			int[] imageResourceArray, View rootView) {
		mContext = (Activity) context;
		mMenuNameArray = menuNameArray;
		mImageResourceArray = imageResourceArray;
		this.rootView = rootView;
	}
	
	public void LoadMenuRes(){
		// �����Զ���menu�˵�
		menuView = View.inflate(mContext, R.layout.gridview_menu, null);
		// ����AlertDialog
//		menuDialog = new AlertDialog.Builder(mContext).create();
		menuDialog = new PopupWindow(mContext);
		menuDialog.setWidth(LayoutParams.WRAP_CONTENT);
		menuDialog.setHeight(LayoutParams.WRAP_CONTENT);
//		menuDialog = new Dialog(mContext,R.style.dialogWithoutFrame);
		menuDialog.setContentView(menuView);
		menuDialog.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.transparent_background));
		menuDialog.setFocusable(true);
		menuDialog.setTouchable(true);
//		menuDialog.setOutsideTouchable(true);
		menuDialog.setAnimationStyle(android.R.style.Animation_Toast);
		menuGrid = (GridView) menuView.findViewById(R.id.gridview);
		menuTime = (TextView)menuView.findViewById(R.id.time_text);
		TextView menuTitle = (TextView)menuView.findViewById(R.id.title_text);
		menuTitle.setText(R.string.app_name);
		adText = (TextView) menuView.findViewById(R.id.ad_text);
		menuGrid.setAdapter(getMenuAdapter(mMenuNameArray, mImageResourceArray));
		menuGrid.setOnKeyListener(new OnKeyListener()
		{
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if( keyCode == KeyEvent.KEYCODE_MENU)
				{
					if( menuDialog.isShowing())
					{
						menuDialog.dismiss();
						return true;
					}
				}
				return false;
			}
		});
		sdfDay = new SimpleDateFormat("", Locale.getDefault());
		sdfDay.applyPattern("yyyy-MM-dd HH:mm");
	}
	
	public boolean isShowing()
	{
		return menuDialog.isShowing();
	}
	public void dismiss()
	{
		if(menuDialog.isShowing())
		{
			menuDialog.dismiss();
		}
	}
	/**
	 * ����˵�Adapter
	 * 
	 * @param menuNameArray
	 *            ���
	 * @param imageResourceArray
	 *            ͼƬ
	 * @return SimpleAdapter
	 */
	public SimpleAdapter getMenuAdapter(String[] menuNameArray,
			int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemText", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(mContext, data,
				R.layout.item_menu, new String[] { "itemImage", "itemText" },
				new int[] { R.id.item_image, R.id.item_text });
		return simperAdapter;
	}
	
	
	public PopupWindow getQqMenuDialog() {
		return menuDialog;
	}
	
	private AdItem curAd ;
	public void showQqMenuDialog() {
		if (menuDialog == null) {
			LoadMenuRes();
		} 
		Date date = new Date(System.currentTimeMillis());
		
		menuTime.setText(sdfDay.format(date)); 
		curAd = DynamicConfigure.getInstance().getRandomAvailabeAdItem();
		adText.setText(curAd.text);
		if( curAd.link != null && curAd.link.length()>0)
		{
			adText.setOnClickListener(adClickListener);
		}
		else
		{
			adText.setOnClickListener(null);
		}
		menuDialog.showAtLocation(rootView, Gravity.CENTER, 0, 0);
		
	}
	
	private OnClickListener adClickListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Tools.callBrowser(mContext, curAd.link);
			
		}
	};
	
	
	public void setEnable(int position, boolean enable)
	{
		View view = menuGrid.getChildAt(position);
		view.setEnabled(enable);
		ImageView image = (ImageView) view.findViewById(R.id.item_image);
		
		image.setImageBitmap(Tools.getGrayBitmap(BitmapFactory.decodeResource(ReadApplication.getAppContext().getResources(), R.drawable.dpjz)));
//		menuGrid.getChildAt(index)
	}
	
	public GridView getQqMenuGridView() {
		return menuGrid;
	}
	
}