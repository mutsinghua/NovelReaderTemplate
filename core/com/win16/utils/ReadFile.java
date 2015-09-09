/**
 * <This class for read a file.>
 *  Copyright (C) <2009>  <Wang XinFeng,ACC http://androidos.cc/dev>
 *
 *   This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package com.win16.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.win16.reader.annebabytran.data.Constant;

import android.content.res.AssetFileDescriptor;
import android.util.Log;

/**
 * 阅读文件
 * 
 * @author rexzou
 * @version 1.0
 * 
 * 
 */
public class ReadFile {

	/** 输入流 */
	private DataInputStream mDataInputStream;
	/** 文件路径 */
	private String mFilePath;
	/** buffer */
	final static int mSkipBuffer = 16384;
	/** the buffer size */
	final static int mBUFFER = 16384;
	/** file */
	private File mFile;
	/** offset */
	private int mOffset = 0;

	private int fileLenth = 0;

	private AssetFileDescriptor currentFD = null;
	private InputStreamReader fileread;

	/**
	 * constructor a new read file instance
	 * 
	 * @param filePath
	 *            file path
	 * @throws UnsupportedEncodingException 
	 */
	public ReadFile(String filePath) throws FileNotFoundException, UnsupportedEncodingException {
		mFilePath = filePath;
		currentFD = null;
		mOffset = 0;
		mFile = new File(mFilePath);
		// open();
		openStringFile();
	}

	public ReadFile(AssetFileDescriptor fd) {
		mFile = null;
		mFilePath = null;
		currentFD = fd;
		mOffset = 0;
		try {
			mDataInputStream = new DataInputStream(fd.createInputStream());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * open a input stream
	 */
	public void open() throws FileNotFoundException {
		if (!isDirectory()) {
			mDataInputStream = new DataInputStream(new FileInputStream(mFilePath));

		}

	}

	public void openStringFile() throws FileNotFoundException, UnsupportedEncodingException {
		fileread = new InputStreamReader(new FileInputStream(mFilePath), Constant.DEFAULT_CHARSET);

	}

	public String readString(int len) {
//		len = len/2; //unicode 的
		char[] buf = new char[len]; 
		try
		{
//			Log.i("Reader", fileread.getEncoding());
			int readcount = fileread.read(buf, 0, len);
			char[] buf2 = new char[readcount];
			System.arraycopy(buf, 0, buf2, 0, readcount);
			buf=null;
			String ret = new String(buf2);
			if( ret!=null)
			{
				ret = ret.trim();
			}
//			Log.i("Reader", ret);
			return ret;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * read data
	 * 
	 * @param len
	 *            u wanna read data size
	 * @return
	 */
	public byte[] read(int len) {

		if (isDirectory()) {// is a directory?
			return null;
		}
		byte abyte0[] = null;
		long l = 0L;
		l = fileSize();// get file length
		if (mOffset + len > (int) l) {
			len = (int) (l - mOffset);
		}
		abyte0 = new byte[len];// new a byte array
		if (mBUFFER > len) { // read all data
			try {
				mDataInputStream.read(abyte0);
				mOffset += len;
			} catch (IOException ex1) {
				return null;
			}
		} else { // read the data by paragraph
			int datapos = 0;
			int times = len / mBUFFER;// calculate the buffer count
			for (int i = 0; i < times; i++) {
				byte[] buffer = new byte[mBUFFER];
				try {
					mDataInputStream.read(buffer);

					// copy the data to a byte array
					System.arraycopy(buffer, 0, abyte0, datapos, mBUFFER);

					datapos += mBUFFER;
					mOffset += mBUFFER;
				} catch (IOException ex2) {
					return null;
				}
			}
			int rest = len - datapos;
			// If you read the provisions of the length of the actual reading is
			// greater than the length of the
			// go on read data
			byte[] buffer = new byte[rest];
			try {
				mDataInputStream.read(buffer);
				System.arraycopy(buffer, 0, abyte0, datapos, rest);
				mOffset += rest;
			} catch (IOException ex3) {
			}
			buffer = null;
		}
		System.gc();
		return abyte0;
	}

	/**
	 * Analysis of bytes of data to see if there are half a Chinese phenomenon,
	 * if there is a time in the future to continue bytes
	 * 
	 * @param b
	 *            To analyze the data
	 * @return Completion of the analysis of the data
	 */
	public byte[] analyzeByte(byte[] b) {
		int ii = 0;
		byte[] temp;
		for (int i = 0; i < b.length; i++) {
			byte x = b[i];
			if (x < 0) {
				ii++;// The number of bytes of the characters have been
			}
		}

		if (ii % 2 != 0) {// If the Chinese characters for the odd number of
							// bytes, that there are half Chinese
			temp = new byte[b.length + 1];// Generation than in the past few
											// head of a new array
			System.arraycopy(b, 0, temp, 0, b.length);// The original copy data
														// to the inside
			try {
				temp[b.length] = mDataInputStream.readByte();// In the ensuing
																// read a byte
			} catch (IOException e) {
				e.printStackTrace();
			}
			mOffset++;// A shift in the future will be offset bytes
			return temp;
		} else {

			return b;
		}
	}

	/**
	 * Skip a number of
	 * 
	 * @param len
	 *            int The figures need to skip
	 */
	public void skip(int len) throws FileNotFoundException {
		if (isDirectory()) {
			return;
		}
		mOffset += len;
		if (len < 0) {
			close();
			open();
			fastSkip((int) mOffset);
			return;
		}
		fastSkip(len);
		return;
	}

	/**
	 * documents to the position shift
	 * 
	 * @param i
	 *            int Shift the location of
	 */
	public void locate(int i) throws FileNotFoundException {
		if (i < 0 || i > fileSize() - 1) {
			return;
		}
		int len = i - mOffset;
		skip(len);
	}

	/**
	 * use read instead of read,fast skip
	 * 
	 * @param len
	 *            int the length skiped
	 */
	private void fastSkip(int len) {
		if (len <= 0) {
			return;
		}
		try {
			mDataInputStream.skip(len);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
		// byte[] b;
		// if (mSkipBuffer > len) { // Reading a one-time income
		// b = new byte[len];
		// try {
		// mDataInputStream.read(b);
		// } catch (IOException ex) {
		// }
		// } else { //read the data by paragraph
		// int times = len / mSkipBuffer;
		// for (int i = 0; i < times; i++) {
		// b = new byte[mSkipBuffer];
		// try {
		// mDataInputStream.read(b);
		// } catch (IOException ex1) {
		// }
		// }
		// int rest = len - mSkipBuffer * times;
		// b = new byte[rest];
		// try {
		// mDataInputStream.read(b);
		// } catch (IOException ex2) {
		// }
		// }
		// b = null;
		// System.gc();
	}

	public long fileSize() {
		long l = -1L;
		if (currentFD != null) {
			l = currentFD.getLength();
		} else {
			l = mFile.length();
		}
		return l;
	}

	/**
	 * the current offset
	 * 
	 * @return int
	 */
	public int getMOffset() {
		return mOffset;
	}

	/**
	 * the file is exist?
	 * 
	 * @return exist return true,otherwise false
	 */
	public boolean exists() {
		if (mFile != null) {
			return mFile.exists();
		} else {
			return true;
		}
	}

	/**
	 * is a directory ?
	 * 
	 * @return is a diredctory return true,otherwise false
	 */
	public boolean isDirectory() {
		if (mFile != null) {
			return mFile.isDirectory();
		} else {
			return true;
		}
	}

	/**
	 *close file stream
	 */
	public void close() {
		try {
			if (!isDirectory()) {
				if (mDataInputStream != null) {
					mDataInputStream.close();
				}
				if (fileread != null) {
					fileread.close();
				}
			}
			fileread = null;
			mDataInputStream = null;
		} catch (Exception exception) {
		}
	}

}