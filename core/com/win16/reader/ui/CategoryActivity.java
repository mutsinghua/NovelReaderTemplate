package com.win16.reader.ui;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import com.win16.reader.MainController;
import com.win16.reader.core.xml.ISAXConstant;
import com.win16.reader.core.xml.SAXBookService;
import com.win16.reader.core.xml.SAXCategoryService;
import com.win16.reader.core.xml.data.WinBook;
import com.win16.reader.core.xml.data.WinCategory;
import com.win16.reader.net.IHttpListener;
import com.win16.reader.net.WinHttpRequest;
import com.win16.reader.annebabytran.R;
import com.win16.reader.annebabytran.data.Constant;
import com.win16.reader.ui.adpater.CategoryAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CategoryActivity extends BaseActivity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_layout);

		setTitle(R.string.ebook_category);
		
		catList = (ListView) findViewById(R.id.listView_category);
		dialog = ProgressDialog.show(this, "",
				getString(R.string.downloading), true,true);
		
		
		request = new WinHttpRequest();
		request.listener = listener;
		request.url = Constant.WEB_CATEGORY;
		MainController.getInstance().send(request);
		catList.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Intent i = new Intent(CategoryActivity.this, BookListActivity.class);
				i.putExtra("CATEGORY_TYPE", id);
				startActivity(i);
				
			}
		});
		initSearchView();
	}

	private void initSearchView()
	{
		searchText = (EditText) findViewById(R.id.searchText);
		
		Button searchBt = (Button) findViewById(R.id.search_bt);
//		searchText.setSelected(false);
//		searchBt.setSelected(true);
//		searchText.clearFocus();
		

		
		searchBt.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				String keyword = searchText.getText().toString();
				
				if( keyword == null || keyword.length()==0)
				{
					makeToast(0, R.string.search_txt_is_null, Toast.LENGTH_LONG, CategoryActivity.this).show();
				}
				else
				{
					searchText.setSelection(0, keyword.length()); //搜索后选 中所有
				}
				Intent i = new Intent(CategoryActivity.this, BookListActivity.class);
				i.putExtra("SEARCH_KEY", keyword);
				startActivity(i);
			}
		});
	}
	
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case ISAXConstant.PARSE_FINISH:
					CategoryAdapter caa = new CategoryAdapter(CategoryActivity.this, (List<WinCategory>) msg.obj);
					catList.setAdapter(caa);
					dialog.dismiss();
					break;
					
				case IHttpListener.NET_ERROR:
					if( dialog.isShowing() && !isFinishing())
					{
						makeToast(0, R.string.net_error, Toast.LENGTH_LONG, CategoryActivity.this).show();
						dialog.dismiss();
					}
					break;
			}
		}
	};

	private IHttpListener listener = new IHttpListener()
	{

		@Override
		public void onFinish( WinHttpRequest request)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(int errorCode, WinHttpRequest request)
		{
			handler.sendEmptyMessage(IHttpListener.NET_ERROR);
		
		}

		@Override
		public void handleData(HttpEntity res, WinHttpRequest req)
		{
			try
			{
				if (request == req)
				{

					Header head = res.getContentEncoding();
					if (head != null)
					{
						String value = head.getValue();
//						Log.v("TEST", value);
					}
					head = res.getContentType();
					if (head != null)
					{
						String type = head.getValue();
//						Log.v("TEST", type);
					}
					// BufferedInputStream is = new
					// BufferedInputStream(res.getContent());
					// byte[] buf = new byte[4048];
					// ByteArrayOutputStream baos = new ByteArrayOutputStream();
					// int n = 0;
					// while( (n=is.read(buf)) >0)
					// {
					// baos.write(buf, 0, n);
					// }
					// byte[] retdata = baos.toByteArray();
					// String s = new String(retdata, "utf-8");
					// Log.v("Test", s);
					final InputStream is = res.getContent();
					Thread t = new Thread()
					{
						public void run()
						{
							SAXCategoryService service = new SAXCategoryService();
							service.parse(is,handler);
						}
					};
					t.start();

				}
				// str = new String(b, "utf-16");
			} catch (UnsupportedEncodingException e)
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
	

	private WinHttpRequest request;

	private ListView catList;

	private ProgressDialog dialog;

	private EditText searchText;

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}