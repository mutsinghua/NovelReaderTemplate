package com.win16.reader;

import android.content.Context;

import com.win16.reader.net.Constant;
import com.win16.reader.net.HttpController;
import com.win16.reader.net.WinHttpRequest;

public class MainController
{
	private static MainController instance;
	
	private Context context;
	
	private HttpController httpController;
	
	
	private MainController (Context context)
	{
		this.context = context;
		
		httpController = new HttpController(Constant.HTTP_THREAD_NUMBER);
	}
	
	public static void initInstance(Context context)
	{
		if( instance == null)
		{
			instance = new MainController(context);
		}
	}
	
	public void send(WinHttpRequest request)
	{
		httpController.send(request);
	}
	
	public static void close()
	{
		if( instance!=null)
		{
			instance.httpController.close();
			
		}
		
		instance = null;
	}
	
	public static MainController getInstance()
	{
		return instance;
	}
}