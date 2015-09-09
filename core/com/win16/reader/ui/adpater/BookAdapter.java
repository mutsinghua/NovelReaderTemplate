package com.win16.reader.ui.adpater;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.win16.reader.core.ImageCacheManager;
import com.win16.reader.core.xml.data.WinBook;
import com.win16.reader.net.IHttpListener;
import com.win16.reader.net.WinHttpRequest;
import com.win16.reader.annebabytran.R;
import com.win16.reader.annebabytran.data.Constant;
import com.win16.reader.ui.IMsgContant;
import com.win16.utils.Tools;

public class BookAdapter extends BaseAdapter
{
	
	private List<WinBook> books;
	
	private Context context;
	
	public BookAdapter(Context context, List<WinBook> books)
	{
		this.context = context;
		this.books = books;
	}
	
	
	/**
	 *{@link}http://v.qq.com/video/play.html?vid=Z0090aP14iA&ADTAG=INNER.MUSIC.MINIPORTAL
	 * @see android.widget.Adapter#getCount()
	 */
	
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return books.size();
	}
	
	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return books.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return books.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if( convertView == null)
		{
			convertView = ((Activity)context).getLayoutInflater().inflate(R.layout.book_item, null);
		}
		
		TextView view = (TextView) convertView.findViewById(R.id.book_name);
		view.setText(( (WinBook) getItem(position)).name);
		
		ImageView iconView = (ImageView) convertView.findViewById(R.id.book_icon);
		
		Bitmap bitmap = ImageCacheManager.getInstance().get(( (WinBook) getItem(position)).iconUrl, handler);
		
		Button downloadbt = (Button) convertView.findViewById(R.id.book_download);
		downloadbt.setOnClickListener(downloadbtListener);
		
		downloadbt.setTag(String.valueOf(getItemId(position)));
		if( bitmap != null)
		{
			iconView.setImageBitmap(bitmap);
		}
		else
		{
			iconView.setImageResource(R.drawable.default_book_icon);
		}
		return convertView;
	}

	private OnClickListener downloadbtListener = new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			String bookid = (String) v.getTag();
			String url = Constant.WEB_BOOK_DOWNLOAD + "?id="+bookid+"&file_type=3";
			Tools.callBrowser( context, url);
		}
	};
	
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case IMsgContant.GET_ICON:
					notifyDataSetChanged();
					break;
			}
		}
	};
	
//	private IHttpListener iconHandle = new IHttpListener()
//	{
//		
//		@Override
//		public void onFinish()
//		{
//			// TODO Auto-generated method stub
//			
//		}
//		
//		@Override
//		public void onError(int errorCode)
//		{
//			// TODO Auto-generated method stub
//			
//		}
//		
//		@Override
//		public void handleData(HttpEntity res, WinHttpRequest request)
//		{
//			try
//			{
//				byte[] imgData= Tools.readByteFromInputStream(res.getContent());
//				Bitmap icon = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
//				
//			} catch (IllegalStateException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//		}
//	}; 
}