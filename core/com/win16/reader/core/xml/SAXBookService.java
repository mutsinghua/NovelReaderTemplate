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
import com.win16.reader.annebabytran.data.Constant;

public class SAXBookService implements ISAXService
{
	
	private  ArrayList<WinBook> winBooks;
	
	private Handler handler;
	
	
	public void parse(InputStream is, Handler callback)
	{
		this.handler = callback;
		
		winBooks = new ArrayList<WinBook>();
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		try
		{
			SAXParser parser = factory.newSAXParser();
			BookHandler BookHandler = new BookHandler();
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
	}
	
	private class BookHandler extends DefaultHandler
	{

		@Override
		public void endDocument() throws SAXException
		{
			super.endDocument();
			Message msg = new Message();
			msg.what  = ISAXConstant.PARSE_FINISH;
			msg.obj = winBooks;
			handler.sendMessage(msg);
			super.endDocument();
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
			if(localName.equalsIgnoreCase(WinBook.BOOK) )
			{
				WinBook cat = new WinBook();
				cat.id = Integer.parseInt(attributes.getValue("", WinBook.BOOK_ID));
				cat.name = (attributes.getValue("", WinBook.BOOK_NAME));
				cat.iconUrl = Constant.WEB_SITE+(attributes.getValue("", WinBook.BOOK_ICON_URL));
				winBooks.add(cat);
			}
			super.startElement(uri, localName, qName, attributes);
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