package com.win16.reader.ui.adpater;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.win16.data.GlobalDataManager;
import com.win16.reader.annebabytran.R;
import com.win16.reader.annebabytran.data.Constant;

/**
 * 书目录生成
 * @author Rex
 *
 */
public class ListViewApater extends BaseAdapter {

	private Context context;
	
	public ListViewApater (Context context)
	{
		this.context = context;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Constant.BOOK_NAME.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return Constant.BOOK_NAME[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if( convertView != null)
		{
			View view  = (View) convertView.getTag();
			TextView tv = (TextView) view.findViewById(R.id.book_content_text_text);
			tv.setText(getItem(position).toString());
			tv.setTextSize(22);
			tv.setSelected(true);
			View 
			 tvv = (View) view.findViewById(R.id.book_content_text);
			setBackgroud(position,tvv);
			return convertView;
		}
		else
		{
			LayoutInflater mli = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = mli.inflate(R.layout.book_content_item, null);
			TextView tv = (TextView) view.findViewById(R.id.book_content_text_text);
			tv.setText(getItem(position).toString());
			tv.setTextSize(22);
			tv.setSelected(true);
			View 
			 tvv = (View) view.findViewById(R.id.book_content_text);
			setBackgroud(position,tvv);
			view.setTag(view);
			
			return view;
		}
		
	}

	private void setBackgroud(int position, View tv)
	{
		int readstatus = GlobalDataManager.getInstance().getIntegerData(Constant.READSTATUS+position, Constant.UNREAD);
		switch( readstatus)
		{
			case Constant.READING:
				tv.setBackgroundResource(R.drawable.content_reading);
				break;
			case Constant.READED:
				tv.setBackgroundResource(R.drawable.content_read);
				break;
			default:
				tv.setBackgroundResource(R.drawable.content);
				break;
		}
	}
	
}