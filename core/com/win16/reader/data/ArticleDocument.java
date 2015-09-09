package com.win16.reader.data;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.util.Log;

import com.win16.reader.annebabytran.R;
import com.win16.reader.annebabytran.ReadApplication;
import com.win16.utils.ReadFile;

/**
 * 文章管理类
 * 
 * @author rexzou
 * 
 */
public class ArticleDocument {

	/**
	 * 当前的buffer字节数
	 */
	private long currentPositionInFile;

	/**
	 * BUFFER大小
	 */
	private final static int BUFFER_SIZE = 600*1024; //0.5M

	/**
	 * 文字缓冲区
	 */
//	private byte[] byteBuffer = new byte[BUFFER_SIZE];

	private String charset = "gb2312";

	private byte[] dataSliceHalfBaked;

	/**
	 * 读文件器
	 */
	private ReadFile readFile;
	/**
	 * 当前打开的文件
	 */
	private String currentFile;

	private AssetFileDescriptor cuurentFD = null;
	
	public String getFilePath()
	{
		return currentFile;
	}
	
	/**
	 * 是否打开了文件
	 */
	private boolean isOpened = false;

	/**
	 * 打开文件
	 */
	public boolean openFile(String filePath) {
		
		return openFile(filePath, 0);
	}

	public boolean openFile(AssetFileDescriptor fd)
	{
		return openFile(fd,0);
	}
	
	public boolean openFile(AssetFileDescriptor fd, int skipChars)
	{
		cuurentFD = fd;
		currentFile = null;
			if (isOpened) {
				try {
					readFile.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				
				readFile = new ReadFile(fd);
				currentFile = fd.toString();
				if (skipChars > 0) {
					readFile.skip(skipChars);
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		
	}
	
	
	/**
	 * 打开文件,并跳过多少字符
	 */
	public boolean openFile(String filePath, int skipChars) {
		cuurentFD = null;
		currentFile = filePath;
//		Log.i("Reader", "open path:"+currentFile);
		if (isOpened && readFile != null) {
			try {
				readFile.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			File file = new File(filePath);
			if (!file.exists() || !file.isFile()) // 如果不存在或是不是文件
			{
				return false;
			}
			readFile = new ReadFile(filePath);
			currentFile = filePath;
			if (skipChars > 0) {
				readFile.skip(skipChars);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 关闭文件
	 */
	public void closeFile()
	{
		if(readFile!=null)
		{
			readFile.close();
		}
		currentFile = null;
		
	}
	
	/**
	  * 获取一下段文字
	  * 
	  * @return
	  */
	 public String getNext() {
//		 Log.i("Reader","getNext: readFile" + readFile);
		 if(readFile != null)
		 {
			 return readFile.readString((int) readFile.fileSize());
		 }
		 else
		 {
			 return ReadApplication.getAppContext().getString(R.string.read_error);
		 }

//		byteBuffer = readFile.read(byteBuffer.length);
//		if (charset.equalsIgnoreCase("gb2312")) {
//			try {
//				return new String(byteBuffer, "gb2312");
//			} catch (IOException ex) {
//				try {
//					return new String(byteBuffer, "GB2312");
//				} catch (IOException eex) {
//
//					return null;
//				}
//			}
////			return getGBString(byteBuffer);
//		} else {
////			return getUTF8String(byteBuffer);
//			try {
//				return new String(byteBuffer, "utf-8");
//			} catch (IOException ex) {
//				try {
//					return new String(byteBuffer, "UTF-8");
//				} catch (IOException eex) {
//
//					return null;
//				}
//			}
//		}
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	private String getGBString(byte[] srco) {
		
		byte[] src = srco;

		if (dataSliceHalfBaked != null && dataSliceHalfBaked.length > 0) {
			src = new byte[srco.length + dataSliceHalfBaked.length];
			System.arraycopy(dataSliceHalfBaked, 0, src, 0,
					dataSliceHalfBaked.length);
			System.arraycopy(srco, 0, src, dataSliceHalfBaked.length,
					srco.length);
			dataSliceHalfBaked = null;
		}

		int length;

		length = getGBHalfBakedIndex(src);

		byte data[];
		data = new byte[length];
		System.arraycopy(src, 0, data, 0, length);
		try {
			return new String(data, "gb2312");
		} catch (IOException ex) {
			try {
				return new String(data, "GB2312");
			} catch (IOException eex) {

				return null;
			}
		}
	}

	private int getGBHalfBakedIndex(byte[] src) {

		int i = src.length - 1;
		while (true) {
			byte b = src[i];

			if ((b & 0x00ff) < 0x81) {
				if (i < (src.length - 1)) {
					dataSliceHalfBaked = new byte[src.length - i - 1];
					System.arraycopy(src, i + 1, dataSliceHalfBaked, 0,
							dataSliceHalfBaked.length);
				}
				return i + 1;
			}
			i--;

		}

	}

	private int getUTF8HalfBakedIndex(byte[] src) {

		int i = src.length - 1;
		while (true) {
			byte b = src[i];

			if ((b & 0x80) == 0x0) {
				dataSliceHalfBaked = new byte[src.length - i - 1];
				System.arraycopy(src, i, dataSliceHalfBaked, 0,
						dataSliceHalfBaked.length);
				return i + 1;
			}
			if (((b & 0xE0) == 0xc0) || ((b & 0xF0) == 0xE0)) {
				dataSliceHalfBaked = new byte[src.length - i];
				System.arraycopy(src, i, dataSliceHalfBaked, 0,
						dataSliceHalfBaked.length);
				return i;
			}
			i--;
		}

	}

	private String getUTF8String(byte[] srco) {

		byte[] src = srco;
		if (dataSliceHalfBaked != null && dataSliceHalfBaked.length > 0) {
			src = new byte[srco.length + dataSliceHalfBaked.length];
			System.arraycopy(dataSliceHalfBaked, 0, src, 0,
					dataSliceHalfBaked.length);
			System.arraycopy(srco, 0, src, dataSliceHalfBaked.length,
					srco.length);
			dataSliceHalfBaked = null;
		}

		int length;

		length = getUTF8HalfBakedIndex(src);

		byte data[];
		data = new byte[length + 2];
		data[0] = (byte) (length >> 8);
		data[1] = (byte) (length);
		System.arraycopy(src, 0, data, 2, length);
		ByteArrayInputStream strmBytes = new ByteArrayInputStream(data);
		DataInputStream strmDataType = new DataInputStream(strmBytes);
		try {
			return strmDataType.readUTF();
		} catch (IOException ex) {
			ex.printStackTrace();

			return null;
		}
	}
}