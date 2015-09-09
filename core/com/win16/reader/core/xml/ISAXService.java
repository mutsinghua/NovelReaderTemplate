package com.win16.reader.core.xml;

import java.io.InputStream;

import android.os.Handler;

public interface ISAXService
{
	public void parse(InputStream is, Handler callback);
}