package com.win16.reader.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.Selection;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.win16.reader.ui.DrawListener;

public class ReadView extends TextView {

	private DrawListener drawListener;
	private int off;
	
	public ReadView(Context context) {
		super(context);
//		this.setMaxLines(Integer.MAX_VALUE-1);
//		this.setMaxEms(Integer.MAX_VALUE-1);
	}
	

	public ReadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
//		this.setMaxLines(Integer.MAX_VALUE-1);
//		this.setMaxEms(Integer.MAX_VALUE-1);
	}


	public ReadView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		this.setMaxLines(Integer.MAX_VALUE-1);
//		this.setMaxEms(Integer.MAX_VALUE-1);
	}


	public void setOnDrawListener(DrawListener dl)
	{
		drawListener = dl;
	}




//	@Override    
//    public boolean onTouchEvent(MotionEvent event) {    
//        int action = event.getAction();    
//        Layout layout = getLayout();    
//        int line = 0;    
//        switch(action) {    
//        case MotionEvent.ACTION_DOWN:    
//            line = layout.getLineForVertical(getScrollY()+ (int)event.getY());            
//            off = layout.getOffsetForHorizontal(line, (int)event.getX());    
//            Selection.setSelection(gett, off);    
//            break;    
//        case MotionEvent.ACTION_MOVE:    
//        case MotionEvent.ACTION_UP:    
//            line = layout.getLineForVertical(getScrollY()+(int)event.getY());     
//            int curOff = layout.getOffsetForHorizontal(line, (int)event.getX());                
//            Selection.setSelection(getEditableText(), off, curOff);    
//            break;    
//        }    
//        return true;    
//    }    


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		Log.i("Reader","Ondraw");
		if(drawListener != null)
		{
			   
			drawListener.onDrawed();
			
		}
	}

}