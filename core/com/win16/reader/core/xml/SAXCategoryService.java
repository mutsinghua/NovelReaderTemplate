package com.win16.reader.core.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.HandlerBase;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.os.Handler;
import android.os.Message;

import com.win16.reader.core.xml.data.WinBook;
import com.win16.reader.core.xml.data.WinCategory;

public class SAXCategoryService implements ISAXService
{
	private  ArrayList<WinCategory> winCat;
	
	private Handler handler;
	
	public void parse(InputStream is, Handler callback)
	{
		this.handler = callback;
		
		winCat = new ArrayList<WinCategory>();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		try
		{
			SAXParser parser = factory.newSAXParser();
			CategoryHandler BookHandler = new CategoryHandler();
			parser.parse(is, BookHandler );
			
		} catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ;
	}
	
	private class CategoryHandler extends DefaultHandler
	{

		@Override
		public void endDocument() throws SAXException
		{
			// TODO Auto-generated method stub
			super.endDocument();
			Message msg = new Message();
			msg.what  = ISAXConstant.PARSE_FINISH;
			msg.obj = winCat;
			handler.sendMessage(msg);
		}

		@Override
		public void startDocument() throws SAXException
		{
			// TODO Auto-generated method stub
			super.startDocument();
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			super.startElement(uri, localName, qName, attributes);
			
			if(localName.equalsIgnoreCase(WinCategory.CATEGORY) )
			{
				WinCategory cat = new WinCategory();
				cat.id = Integer.parseInt(attributes.getValue("", WinCategory.CATEGORY_ID));
				cat.name = (attributes.getValue("", WinCategory.CATEGORY_NAME));
				cat.count = Integer.parseInt(attributes.getValue("", WinCategory.CATEGORY_COUNT));
				winCat.add(cat);
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			// TODO Auto-generated method stub
			super.endElement(uri, localName, qName);
			

			
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			// TODO Auto-generated method stub
			super.characters(ch, start, length);
		}
		
	}
}