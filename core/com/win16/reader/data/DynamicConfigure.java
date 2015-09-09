package com.win16.reader.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.win16.reader.MainController;
import com.win16.reader.core.xml.ISAXConstant;
import com.win16.reader.core.xml.ISAXService;
import com.win16.reader.core.xml.data.AdItem;
import com.win16.reader.net.IHttpListener;
import com.win16.reader.net.WinHttpRequest;
import com.win16.reader.annebabytran.ReadApplication;
import com.win16.reader.annebabytran.data.Constant;

/**
 * 网上拉取的
 * @author Rex
 *
 */
public class DynamicConfigure
{
	

	
	public static final String DANGER_360 = "360";
	
	public static final String SP_NAME="DynamicConfigure";
	
	public static final String YOUMI_TIME = "YOUMI_TIME";
	public static final String SMARTMAD_TIME = "SMARTMAD_TIME";
	public static final String ADMOB_TIME = "ADMOB_TIME";
	
	
	//用于文字广告
	private ArrayList<AdItem> adLinkList;
	
	/**
	 * 有米广告时间
	 */
//	private long youmiTime = 30000;
	
	/**
	 * admob时间
	 */
	private long admobTime = 30000;
	/**
	 * 有亿动广告时间
	 */
	private long smartmadTime = 30000;
	
	
	/**
	 * 有效的广告公司,用于过滤
	 */
	private ArrayList<String> adEnableCompany;
	
	private DynamicConfigure()
	{
		adLinkList = new ArrayList<AdItem>();
		adEnableCompany = new ArrayList<String>();
		loadConfig();
		sendGetConfig();
	}
	
	/**
	 * 从sp中读取配置
	 */
	private void loadConfig()
	{
		SharedPreferences sp = com.win16.reader.annebabytran.ReadApplication.getAppContext().getSharedPreferences(SP_NAME, 0);
		smartmadTime = sp.getLong(SMARTMAD_TIME, smartmadTime);
		setAdmobTime(sp.getLong(ADMOB_TIME, getAdmobTime()));

	}
	
	/**
	 * 保存配置
	 */
	private void saveConfig()
	{
		SharedPreferences sp = ReadApplication.getAppContext().getSharedPreferences(SP_NAME, 0);
		Editor editor = sp.edit();
		editor.putLong(SMARTMAD_TIME, smartmadTime);
		editor.putLong(ADMOB_TIME, getAdmobTime());
	}
	
	private static DynamicConfigure instance;
	
	public static DynamicConfigure init()
	{
		if( instance == null)
		{
			instance = new DynamicConfigure();
		}
		return instance;
	}
	
	public AdItem getRandomAvailabeAdItem()
	{
		if( adLinkList.size()> 0)
		{
			return adLinkList.get(new Random().nextInt(adLinkList.size()));
		}
		else
		{
			return new AdItem();
		}
	}
	
	
	
	/**
	 * 调用之前先初始化
	 * @return
	 */
	public static DynamicConfigure getInstance()
	{
		return instance;
	}
	
	private void sendGetConfig()
	{
		WinHttpRequest req = new WinHttpRequest(Constant.WEB_CONFIG, httpListener);
		MainController.getInstance().send(req);
	}
	
	private void sendGetAd()
	{
		WinHttpRequest req = new WinHttpRequest(Constant.WEB_AD, httpListener);
		req.queryString=Constant.ISTRAN;
		MainController.getInstance().send(req);
	}
	
	private void setAdmobTime(long admobTime)
	{
		this.admobTime = admobTime;
	}

	public long getAdmobTime()
	{
		return admobTime;
	}

	private void setSmartmadTime(long smartmadTime)
	{
		this.smartmadTime = smartmadTime;
	}

	public long getSmartmadTime()
	{
		return smartmadTime;
	}

//	public long getAdmobTime()
//	{
//		return admobTime;
//	}
	
	private IHttpListener httpListener = new IHttpListener()
	{
		
		@Override
		public void onFinish(WinHttpRequest req)
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onError(int errorCode, WinHttpRequest req)
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void handleData(HttpEntity res, WinHttpRequest request)
		{
			SAXConfigServer server = new SAXConfigServer();
			try
			{
				server.parse(res.getContent(), handler);
			} catch (IllegalStateException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	};
	
	private class SAXConfigServer implements ISAXService{

		@Override
		public void parse(InputStream is, Handler callback)
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			
			try
			{
				SAXParser parser = factory.newSAXParser();
				ConfigHandler configHandler = new ConfigHandler();
				parser.parse(is, configHandler );
				
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
		
	};
	
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
				case ISAXConstant.PARSE_CONFIG_FINISH:
					saveConfig();
					 if( adEnableCompany.size()> 0)
					 {
						 sendGetAd();
					 }
					 break;
				case ISAXConstant.PARSE_AD_FINISH:
					break;
			}
		}
	};
	
	private class ConfigHandler extends DefaultHandler
	{

		private String curCompanyName;
		@Override
		public void endDocument() throws SAXException
		{
			// TODO Auto-generated method stub
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
			super.startElement(uri, localName, qName, attributes);
			
			if(localName.equalsIgnoreCase("addata") )
			{
				String companyName = (attributes.getValue("", "company"));
				adEnableCompany.add(companyName);
			}
			else if( localName.equalsIgnoreCase("aditem"))
			{
				String cpName = (attributes.getValue("", "name"));
				int cpTime = Integer.parseInt(attributes.getValue("", "time"));
				if( cpName.equalsIgnoreCase(Constant.ADMOB))
				{
					setAdmobTime(cpTime);
					Constant.ADMOB_SUPPORT = Constant.ADMOB_SUPPORT && (cpTime != 0); 
//					Log.v("Test", "setYoumiTime "+cpTime);
				}
				else if( cpName.equalsIgnoreCase(Constant.SMARTMAD))
				{
					setSmartmadTime(cpTime);
//					Log.v("Test", "setSmartmadTime "+cpTime);
					Constant.SMARTMAD_SUPPORT = Constant.SMARTMAD_SUPPORT && (cpTime != 0);
				}
			}
			else if(localName.equalsIgnoreCase("ads") )
			{
				curCompanyName = attributes.getValue("", "company");
			}
			else if(localName.equalsIgnoreCase("ad") && adEnableCompany.contains(curCompanyName) )
			{
				AdItem aditem = new AdItem();
				aditem.text = (attributes.getValue("", "text"));
				aditem.link = (attributes.getValue("", "link"));
				aditem.company = curCompanyName;
				adLinkList.add(aditem);
			}
			
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			// TODO Auto-generated method stub
			super.endElement(uri, localName, qName);
			
			Message msg = new Message();
			if( localName.equalsIgnoreCase("adtexts"))
			{
				msg.what  = ISAXConstant.PARSE_AD_FINISH;	
			}
			else if( localName.equalsIgnoreCase("config") )
			{
				msg.what = ISAXConstant.PARSE_CONFIG_FINISH;
			}
			else if(localName.equalsIgnoreCase("ads") )
			{
				curCompanyName = "";
			}
			handler.sendMessage(msg);
			
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException
		{
			// TODO Auto-generated method stub
			super.characters(ch, start, length);
		}
		
	}
}