package com.win16.reader.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.http.HttpEntity;

import com.win16.reader.MainController;
import com.win16.reader.net.IHttpListener;
import com.win16.reader.net.WinHttpRequest;
import com.win16.reader.annebabytran.ReadApplication;
import com.win16.reader.annebabytran.data.Constant;
import com.win16.reader.ui.IMsgContant;
import com.win16.utils.Tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

public class ImageCacheManager
{
	public HashMap<String, WeakReference<Bitmap>> imageCache;

	public LinkedList<ImageRequest> iconRequest;

	private static ImageCacheManager instance;

	private int[] locker = new int[0];

	private static final String ICON_PATH = "/win16/icon/";
	
	public static ImageCacheManager getInstance()
	{
		if (instance == null)
		{
			instance = new ImageCacheManager();
		}
		return instance;
	}

	private ImageCacheManager()
	{
		imageCache = new HashMap<String, WeakReference<Bitmap>>();
		iconRequest = new LinkedList<ImageRequest>();
	}

	private void put(String url, Bitmap bitmap)
	{
		WeakReference<Bitmap> ref = new WeakReference<Bitmap>(bitmap);
		imageCache.put(url, ref);
	}

	/**
	 * 获取图标
	 * 
	 * @param url
	 *            图标的地址
	 * @param callback
	 *            异常处理网络事件 GET_ICON, GET_ICON_FAILED
	 * @return
	 */
	public Bitmap get(String url, Handler callback)
	{
		WeakReference<Bitmap> ref = imageCache.get(url);

		if (ref != null && ref.get() != null) // 内存有
		{
			return ref.get();
		}

		// 读文件
		File iconFile = new File(getIconFullPath(url));
		byte[] icondata = null;
		if (iconFile.exists())
		{
			try
			{
				FileInputStream fis = new FileInputStream(iconFile);

				icondata = Tools.readByteFromInputStream(fis);
				Bitmap iconTemp = BitmapFactory.decodeByteArray(icondata, 0,
						icondata.length);
				put(url, iconTemp);
				return iconTemp;
			} catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			
		}

		ImageRequest req = new ImageRequest(url, callback);

		// 读网络
		synchronized (locker)
		{
			if (iconRequest.contains(req)) // 如果已经发了请求
			{
				return null;
			} else
			{
				iconRequest.add(req);
//				Log.v("Test", "add" + req.url);
			}
		}
		sendIconRequest(req);

		return null;
	}

	/**
	 * 实际发送拉取icon请求
	 */
	private void sendIconRequest(ImageRequest req)
	{
		WinHttpRequest request = new WinHttpRequest();
		request.listener = iconDownloader;
		request.url = req.url;
		request.tag = req;
//		Log.v("Test", "send" + req.url);
		MainController.getInstance().send(request);
	}

	private IHttpListener iconDownloader = new IHttpListener()
	{

		@Override
		public void onFinish(WinHttpRequest request)
		{
//			Log.v("Test", "onFinish"+ request.url);
			synchronized (locker)
			{
							
				iconRequest.remove(request.tag);
			}
//			sendIconRequest();

		}

		@Override
		public void onError(int errorCode, WinHttpRequest request)
		{
//			Log.v("Test", "onError"+ request.url);
		}

		@Override
		public void handleData(HttpEntity res, WinHttpRequest request)
		{
//			Log.v("Test", "handleData"+ request.url);
			final HttpEntity response = res;
			final WinHttpRequest req = request;
			
			Thread t = new Thread()
			{
				public void run()
				{
					byte[] icondata;
					try
					{
						//放入缓存
						icondata = Tools.readByteFromInputStream(response
								.getContent());

						Bitmap iconTemp = BitmapFactory.decodeByteArray(
								icondata, 0, icondata.length);
						put(req.url, iconTemp);
						
						
						//通知UI
						synchronized (locker)
						{
							ImageRequest ir = (ImageRequest) req.tag;
							if (ir != null && ir.callBack != null)
							{
								ir.callBack
										.sendEmptyMessage(IMsgContant.GET_ICON);
							}
						}
						
						//写入文件

						File iconFile = new File(getIconFullPath(req.url));
						
						if (iconFile.exists())
						{
							iconFile.delete();
						}
						
						FileOutputStream fos = new FileOutputStream(iconFile);
						fos.write(icondata);
						fos.close();
						
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
			t.start();
								
		};

		
	};
	
	private String getIconFullPath(String url)
	{
		String localPath = Tools.tranferUrltoLocalPath(url);
		String absolutePath = Tools.getStorePath(ReadApplication
				.getAppContext(), ICON_PATH );
		return absolutePath + File.separator+ localPath;
	}
}