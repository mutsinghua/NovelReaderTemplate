package com.win16.reader.ui.adpater;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.win16.reader.core.xml.data.WinBook;
import com.win16.reader.core.xml.data.WinCategory;
import com.win16.reader.net.IHttpListener;
import com.win16.reader.net.WinHttpRequest;
import com.win16.utils.Tools;

public class CategoryAdapter extends BaseAdapter
{
	
	private List<WinCategory> category;
	
	private Context context;
	
	public CategoryAdapter(Context context, List<WinCategory> category)
	{
		this.context = context;
		this.category = category;
	}
	
	
	/**
	 *{@link}http://v.qq.com/video/play.html?vid=Z0090aP14iA&ADTAG=INNER.MUSIC.MINIPORTAL
	 * @see android.widget.Adapter#getCount()
	 */
	
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return category.size();
	}
	
	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return category.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return category.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if( convertView == null)
		{
			convertView = ((Activity)context).getLayoutInflater().inflate(R.layout.simple_list_item_1, null);
		}
		
		TextView view = (TextView) convertView.findViewById(android.R.id.text1);
		view.setText(( (WinCategory) getItem(position)).name);
		return convertView;
	}

	private IHttpListener iconHandle = new IHttpListener()
	{
		
		@Override
		public void onFinish( WinHttpRequest request)
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onError(int errorCode, WinHttpRequest request)
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void handleData(HttpEntity res, WinHttpRequest request)
		{
			try
			{
				byte[] imgData= Tools.readByteFromInputStream(res.getContent());
				Bitmap icon = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
				
			} catch (IllegalStateException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}; 
	
	
}