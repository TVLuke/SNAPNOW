package de.lukeslog.snapnow.posting;

import android.app.Activity;
import android.content.Context;

public class TextEntry extends Entry
{

	String text;

	public TextEntry(String header, String text, Context ctx)
	{
		super(ctx);
		addTag(Entry.TAG_TYPE, "text");
		setHeader(header);
		this.text=text;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String t)
	{
		this.text=t;
	}
}
