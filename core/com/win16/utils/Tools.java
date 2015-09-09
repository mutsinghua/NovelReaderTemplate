package com.win16.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.TextView;

import com.win16.reader.data.Configuration;
import com.win16.reader.annebabytran.ReadApplication;
import com.win16.reader.annebabytran.data.Constant;
import com.win16.reader.ui.CategoryActivity;

public class Tools
{
	/**
	 * 有SD卡，先存sd卡
	 * 
	 * @return
	 */
	public static String getStorePath(Context context, String path)
	{

		// 获取SdCard状态

		String state = android.os.Environment.getExternalStorageState();

		// 判断SdCard是否存在并且是可用的

		if (android.os.Environment.MEDIA_MOUNTED.equals(state))
		{

			if (android.os.Environment.getExternalStorageDirectory().canWrite())
			{

				File file = new File(android.os.Environment
						.getExternalStorageDirectory().getPath() + File.separator
						+ path);
				if (!file.exists())
				{
					file.mkdirs();
				}
				return file.getAbsolutePath();

			}

		}

		return context.getFilesDir().getAbsolutePath();

	}

	public static String copyFile(int fileID)
	{
		BufferedInputStream dis = null;
		InputStream is = null;
		BufferedOutputStream dos = null;
		String path = "";
		try
		{
			is = ReadApplication.getAppContext().getAssets().open(
					Constant.FILE_LIST[fileID]);

			dis = new BufferedInputStream(is);
			path = getBookPath(ReadApplication.getAppContext(), fileID);
			File file = new File(path);
			if (file.exists())
			{
				file.delete();
			}
			dos = new BufferedOutputStream(new FileOutputStream(path));
			doCopy(dis, dos);
			dis.close();
			dos.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path;
	}

	/**
	 * 复制一个文件
	 */
	private static void doCopy(BufferedInputStream dis, BufferedOutputStream dos)
	{
		byte[] buf = new byte[64 * 1024];
		try
		{
			int r = dis.read(buf);
			while (r != -1)
			{
				dos.write(buf, 0, r);
				r = dis.read(buf);

			}

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 *黑白
	 */
	public static Bitmap getGrayBitmap(Bitmap temp1)
	{
		final Bitmap temp = temp1.copy(Bitmap.Config.ARGB_8888, true);
		int stride = temp.getWidth();
		int[] data = new int[stride];
		for (int i = 0; i < temp.getHeight(); i++)
		{
			temp.getPixels(data, 0, stride, 0, i, stride, 1);
			for (int j = 0; j < stride; j++)
			{
				int a = data[j] & 0xff000000;
				int r = (data[j] >> 16) & 0xff;
				int g = (data[j] >> 8) & 0xff;
				int b = (data[j]) & 0xff;
				byte gray = (byte) ((r * 38 + g * 75 + b * 15) >> 7);
				data[j] = a | (gray * 0x10101);
			}
			temp.setPixels(data, 0, stride, 0, i, stride, 1);
		}
		return temp;
	}

	public static String getBookPath(Context context, int articleId)
	{
		String path = null;
		path = Tools.getStorePath(context, Constant.FILE_PATH);
		path = path + "/" + Constant.FILE_LIST[articleId];
		return path;
	}

	public static void clearCache(Context context)
	{
		for (int i = 0; i < Constant.FILE_LIST.length; i++)
		{
			String path = context.getFilesDir().getAbsolutePath() + "/"
					+ Constant.FILE_LIST[i];
			File file = new File(path);
			if (file.exists())
			{
				file.delete();
			}
		}
		try
		{
			String path = Tools.getStorePath(context, Constant.FILE_PATH);
			File file = new File(path);
			File[] fileList = file.listFiles();
			for (int i = 0; fileList != null && i < fileList.length; i++)
			{
				if (fileList[i].exists())
				{
					fileList[i].delete();
				}
			}
		} catch (Exception e)
		{
			// TODO: handle exception
		}
	}

	/**
	 * 设置屏幕方向
	 * 
	 * @param act
	 */
	public static boolean setScreenDir(Activity act)
	{
		// Log.i("Reader", "setScreenDir");
		boolean ret = false;
		if (Configuration.getInstance().isLand())
		{
			if (act.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
			{
				act
						.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

				ret = true;

			}

		} else
		{
			if (act.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
			{
				act
						.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				ret = true;
			}
		}
		// Log.i("Reader", "setScreenDir"+ret);
		return ret;
	}

	/**
	 * 读取流到数组中,读完会关流
	 * @param is
	 * @return
	 */
	public static byte[] readByteFromInputStream(InputStream is)
	{

		BufferedInputStream bis = new BufferedInputStream(is);
		byte[] buf = new byte[4048];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			int n = 0;

			while ((n = bis.read(buf)) > 0)
			{
				baos.write(buf, 0, n);
			}
			byte[] retdata = baos.toByteArray();
			return retdata;
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally
		{
			try
			{
				if (baos != null)
				{
					baos.close();
				}
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try
			{
				if (bis != null)
				{
					bis.close();
				}
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try
			{
				if (is != null)
				{
					is.close();
				}
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// String s = new String(retdata, "utf-8");
		// Log.v("Test", s);

	}
	
	public static  String tranferUrltoLocalPath(String url)
	{
		Uri uri = Uri.parse(url);
		String icon = uri.getPath();
		icon = icon.replace("/","_");
		icon = icon.replace("\\","_");
		return icon;
		
	}
	
	public static void callBrowser(Context context, String url)
	{
		if (url != null && url.length() > 0)
		{
			Intent i = new Intent();
			i.setAction("android.intent.action.VIEW");
//			i.addCategory("android.intent.category.BROWSABLE");
			i.setData(Uri.parse(url));
			context.startActivity(i);
		}
	}
	
	public static void startCategoryActivity(Context context)
	{
		Intent i = new Intent(context, CategoryActivity.class);
		context.startActivity(i);
//		Intent intent = new Intent(Intent.ACTION_VIEW); 
//		Uri uri = Uri.parse("market://search?q=pub:win16");
//		intent.setData(uri);
//		context.startActivity(intent);
	}
	
	public static void underline(int start,int end, TextView view){  
        SpannableStringBuilder spannable=new SpannableStringBuilder(view.getText().toString());  
        CharacterStyle span=new UnderlineSpan();  
        spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
        view.setText(spannable);  
    }
	
}