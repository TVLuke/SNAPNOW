package de.lukeslog.snapnow.posting;

import android.app.Activity;
import android.content.Context;

public class TextEntry extends Entry
{

	String text;
	String header;
	
	public TextEntry(String header, String text, Context ctx)
	{
		super(ctx);
		addTag(Entry.TAG_TYPE, "text");
		this.text=text;
		this.header=header;
	}
	
	public String getText()
	{
		return text;
	}
	
	public String getHeader()
	{
		return header;
	}
	
	public void setHeader(String h)
	{
		this.header=h;
	}
	
	public void setText(String t)
	{
		this.text=t;
	}
}
