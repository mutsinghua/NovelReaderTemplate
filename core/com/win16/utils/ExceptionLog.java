package com.win16.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.zip.GZIPOutputStream;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class ExceptionLog {
	public static String TAG = "except";
	
	public  static final String LOG_FILE= "exception.log.txt";
	public static final String LOG_PATH_SDCARD= "/FeiFeiNet/Reader/log/";
	private static String[] stackTraceFileList = null;
	private static ExceptionLog exceptH = new ExceptionLog();
	private static Context cont;
	
	private static Thread submitTrd;
	/**
	 * Register handler for unhandled exceptions.
	 * need use it only once, calling it more is acceptable also 
	 * @param context
	 */
	public static void register(Context context) {
//		Log.i(TAG, "Registering default exceptions handler");
		// Get information about the Package
		cont = context;
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi;
			// Version
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			G.APP_VERSION = pi.versionName;
			// Package name
			// Files dir for storing the stack traces
			G.FILES_PATH = getStorePath(context);
			// Device model
            G.PHONE_MODEL = android.os.Build.MODEL;
            // Android version
            G.ANDROID_VERSION = android.os.Build.VERSION.RELEASE;
            cont = context.getApplicationContext();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
//		Log.d(TAG, "APP_VERSION: " + G.APP_VERSION);
//		Log.d(TAG, "FILES_PATH: " + G.FILES_PATH);
//		Log.d(TAG, "URL: " + G.URL);
		if (submitTrd != null)
			return;
		submitTrd = new Thread() {
			@Override
			public void run() {
				submitStackTraces();
				final UncaughtExceptionHandler currentHandler = Thread
						.getDefaultUncaughtExceptionHandler();
				if (currentHandler != null) {
//					Log.d(TAG, "current handler class="
//							+ currentHandler.getClass().getName());
				}
				// don't register again if already registered
				if (!(currentHandler instanceof DefaultExceptionHandler)) {
					Thread.setDefaultUncaughtExceptionHandler(exceptH.new DefaultExceptionHandler(
							currentHandler));
					// Register default exceptions handler
				}
			}
		};
       	submitTrd.start();
	}
	
	/**
	 * 有SD卡，先存sd卡
	 * @return
	 */
	private static String getStorePath(Context context) {

		// 获取SdCard状态

		String state = android.os.Environment.getExternalStorageState();

		// 判断SdCard是否存在并且是可用的

		if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {

			if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
				
			File file = new File(android.os.Environment.getExternalStorageDirectory()
					.getPath()+ LOG_PATH_SDCARD);
				if( !file.exists())
				{
					file.mkdirs();
				}
				return file.getAbsolutePath();

			}

		}

		return context.getFilesDir().getAbsolutePath();

	}
	
	/**
	 * Search for stack trace files.
	 * @return
	 */
	private static boolean searchForStackTraces() {
		File dir = new File(G.FILES_PATH);
		dir.mkdirs();

		File f = new File(G.FILES_PATH + "/"+LOG_FILE);
//		// Try to create the files folder if it doesn't exist
//		// Filter for ".stack" files
//		FilenameFilter filter = new FilenameFilter() { 
//			public boolean accept(File dir, String name) {
//				return name.endsWith(".stack"); 
//			} 
//		}; 
//		return (stackTraceFileList = dir.list(filter));
		return f.exists();
	}

	public static byte[] compress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes());
		gzip.close();
		return out.toByteArray();
	}

	/**
	 * Look into the files folder to see if there is "exception.stack" file. If
	 * any is present, submit them to the trace server.
	 */
	public static void submitStackTraces() {
		// 每日提交一次
		if( true )
		{
			return;
		}
//		if (false) 
//		{
//			android.text.format.Time tmtxt = new android.text.format.Time();
//			tmtxt.setToNow();
//			SharedPreferences settings = cont.getSharedPreferences("QQ", 0);
//			if (tmtxt.format("%d-%m").equals(
//					settings.getString("lastLunch", "")))
//				return;
//			SharedPreferences.Editor editor = settings.edit();
//			editor.putString("lastLunch", tmtxt.format("%d-%m"));
//			editor.commit();
//			tmtxt = null;
//		}
//		try {
//			if (searchForStackTraces()) {
//				String filePath = G.FILES_PATH + "/exception.stack";
//				StringBuilder contents = new StringBuilder();
//				BufferedReader input = new BufferedReader(new FileReader(
//						filePath), 4*1024);
//				String line = null;
//				while ((line = input.readLine()) != null) {
//					contents.append(line);
//					contents.append("{L}");
//				}
//				input.close();
//				String stacktrace;
//				stacktrace = contents.toString();
////				Log.d(TAG, "Transmitting stack trace: " + stacktrace);
//				
//				byte[] reqData = compress(stacktrace);
//				
//				byte[] ecrGKey = null;
//				String head = (QQ.getSelfUin()+"|0|5AE81224979F8379");
//				Log.i(TAG, "except head:"+head);
//				ecrGKey = new Cryptor().encrypt(head.getBytes("utf-8"), ConfigManager.encryptKey);
//				URL url = new URL(G.URL + Tools.toHexStr(ecrGKey));
//				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//				conn.setConnectTimeout(6 * 1000);
//				conn.setDoOutput(true);
//				conn.setUseCaches(false);
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Q-UA", "QUA");
//                conn.setRequestProperty("Content-Type", "application/octet-stream");
//				Log.i(TAG, "submit:" + conn.getURL());
//                DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
//				outStream.write(reqData);
//				outStream.flush();
//				outStream.close();
//				int retCode = conn.getResponseCode();
//				Log.i(TAG, "submited:" + retCode);
//
//				//
//				// DefaultHttpClient httpClient = new DefaultHttpClient();
//				//
////				HttpPost httpPost = new HttpPost(G.URL
////						+ Tools.toHexStr(ecrGKey));
////				httpPost.addHeader("Q-UA", "QUA");
////
////				{
////					ByteArrayEntity entry = new ByteArrayEntity(ret);
////					entry.setContentType("application/octet-stream");
////					httpPost.setEntity(entry);
////				}
////				
////				Log.i(TAG, "submit:" + httpPost.getURI());
////				Log.i(TAG, "http content length:"
////						+ httpPost.getEntity().getContentLength() + " org:"
////						+ stacktrace.length());
////				HttpResponse res = httpClient.execute(httpPost);
////				res.getStatusLine().getStatusCode();
//			}
//		} catch (Exception e) {
//			Log.i(TAG, "submite failed!");
//			e.printStackTrace();
//		} finally {
//			Log.i(TAG, "clean except log file!");
//			try {
//				File file = new File(G.FILES_PATH + "/exception.stack");
//				file.delete();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
	}

	class DefaultExceptionHandler implements UncaughtExceptionHandler {

		private UncaughtExceptionHandler defaultExceptionHandler;
		private static final String TAG = "UNHANDLED_EXCEPTION";

		public DefaultExceptionHandler(UncaughtExceptionHandler pDefaultExceptionHandler)
		{
			defaultExceptionHandler = pDefaultExceptionHandler;
		}
		 
		// exception will trap here
		public void uncaughtException(Thread t, Throwable e) {
		    final Writer result = new StringWriter();
		    final PrintWriter printWriter = new PrintWriter(result);
		    e.printStackTrace(printWriter);
//		    Log.d(TAG, "Writing unhandled exception to: " + G.FILES_PATH+"/" + LOG_FILE);
		    
		    try {
		    	//create crash log append into /data/data/com.tencent.qq/exception.stack
		    	BufferedWriter bos = new BufferedWriter(new FileWriter(G.FILES_PATH+"/" +LOG_FILE, true));
	            bos.write("\t\n**********************\t\n");
	            bos.write("APP_VERSION:"+G.APP_VERSION + "\t\n");
	            bos.write("PHONE_MODEL:"+G.PHONE_MODEL + "\t\n");
	            bos.write("ANDROID_VERSION:"+G.ANDROID_VERSION + "\t\n");
				android.text.format.Time tmtxt = new android.text.format.Time();
				tmtxt.setToNow();
				bos.write(tmtxt.format("%Y-%m-%d %H:%M:%S")+"\n");
	            bos.write(result.toString());
			    bos.flush();
			    bos.close();
		    } catch (Exception ebos) {
		    	//ebos.printStackTrace();
		    }
//			Log.d(TAG, result.toString());	    
			//call original handler  

	    	defaultExceptionHandler.uncaughtException(t, e);    
		    android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);

		}
	}

	static class G {
		public static String FILES_PATH 				= null;
		public static String APP_VERSION 				= "unknown";
	    public static String PHONE_MODEL 				= "unknown";
	    public static String ANDROID_VERSION            = "unknown";
	    //release url
//		public static String URL						= "http://kiss.3g.qq.com/activeQQ/report/debug/?hexHead=";
		//test url
//	    public static String URL						= "http://eurekachu.kf0309.3g.qq.com/activeQQ/report/debug/?hexHead=";
	  //  public static String URL						= "http://10.3.136.76/activeQQ/report/debug/?hexHead=";
	}
}