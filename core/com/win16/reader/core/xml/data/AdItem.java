package com.win16.reader.core.xml.data;


public class AdItem
{
	public String text;
	public String link;
	public String company;
	public AdItem(String text, String link, String company)
	{
		super();
		this.text = text;
		this.link = link;
		this.company = company;
	}
	
	public AdItem()
	{
		super();
		this.text = "";
		this.link = "";
		this.company = "";
	}
}