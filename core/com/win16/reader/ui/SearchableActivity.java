package com.win16.reader.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.win16.reader.data.UserProgressData;
import com.win16.reader.data.UserProgressManager;
import com.win16.reader.annebabytran.R;
import com.win16.reader.annebabytran.data.Constant;
import com.win16.reader.annebabytran.data.SearchContentProvider;
import com.win16.utils.Tools;

public class SearchableActivity extends Activity
{

	private ListView listView;

	private TextView textView;


	private ArrayList<String> chapters;

	private ArrayAdapter<String> la;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		 requestWindowFeature(0);
		if (Tools.setScreenDir(this)) {
			return;
		}
		setContentView(R.layout.search);
		listView = (ListView) findViewById(R.id.searchlist);
		textView = (TextView) findViewById(R.id.no_result);
		textView.setVisibility(View.GONE);
		handleIntent(getIntent());
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				TextView tv = (TextView) arg1;	
				String item = tv.getText().toString();
				for (int i = 0; i < Constant.BOOK_NAME.length; i++)
				{
					if (Constant.BOOK_NAME[i].equals(item))
					{
						UserProgressData data = UserProgressManager.getBooKProgress(i);
						
						Intent intent = new Intent(SearchableActivity.this, ReaderActivity.class);
						intent.putExtra("ARTICLE_ID", i);
						intent.putExtra("UserProgressData", data);
						intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
						startActivity(intent);
						finish();
						if( MainMenuActivity.getInstance()!=null)
						{
							MainMenuActivity.getInstance().finish();
						}
						break;
					}
				}
				
			}
		});
	}

	@Override
	protected void onNewIntent(Intent intent)
	{

		handleIntent(intent);
	}

	private void handleIntent(Intent intent)
	{
		if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction()))
		{
			String query = intent.getStringExtra(SearchManager.QUERY);
			showResults(query);
		}
		else if (Intent.ACTION_VIEW.equalsIgnoreCase(intent.getAction()))
		{
			finish();
//			Intent in = new Intent(this, ReaderActivity.class);
			String uri = intent.getData().toString();
			int Uin = Integer.parseInt(uri
					.substring(SearchContentProvider.CONTENT_URI.toString()
							.length() + 1));
//			Log.i("Reader", "uin"+Uin);
			UserProgressData data = UserProgressManager.getBooKProgress((int) Uin);
			
			Intent in = new Intent(SearchableActivity.this, ReaderActivity.class);
			in.putExtra("ARTICLE_ID", Uin);
			if( data != null)
			{
				in.putExtra("UserProgressData", data);
			}
			startActivity(in);
			
			if( MainMenuActivity.getInstance()!=null)
			{
				MainMenuActivity.getInstance().finish();
			}
		}

	}

	private void showResults(String query)
	{
		if (chapters == null)
		{
			chapters = new ArrayList<String>();
		} else
		{
			chapters.clear();
		}

		for (int i = 0; i < Constant.BOOK_NAME.length; i++)
		{
			if (Constant.BOOK_NAME[i].indexOf(query) >= 0)
			{
				chapters.add(Constant.BOOK_NAME[i]);
			}
		}
		if (chapters.size() == 0)
		{
			listView.setVisibility(View.GONE);
			textView.setVisibility(View.VISIBLE);
		} else
		{
			listView.setVisibility(View.VISIBLE);
			textView.setVisibility(View.GONE);
		}

		if (la == null)
		{
			la = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, chapters);
			listView.setAdapter(la);
		}
		la.notifyDataSetChanged();

	}
}