package net.cineol.xml;

import java.io.InputStream;
import java.util.ArrayList;

import net.cineol.model.News;
import net.cineol.model.Post;
import net.cineol.utils.CINeolUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class XMLParserSingleNews extends XMLParserNews {

	public static final String PARSE_TYPE = "XMLParserSingleNews";
	private static final String TAG = PARSE_TYPE;
	
	// XML Tags.	
	public static final String 	kXMLTagVisits		= "visitas";
	public static final String 	kXMLTagAuthor		= "autor";
	public static final String 	kXMLTagDate			= "fecha";
	public static final String 	kXMLTagContent		= "contenido";
	public static final String 	kXMLTagURLCINeol	= "enlace_foro";
	
	
	public XMLParserSingleNews(Context context, InputStream data, XMLParserDelegate delegate) {
		super(context, data, delegate);
	}
	
	public News getSingleNews() {
		return tempNews;
	}
	
	@Override
	protected void parserDidEndDocument(XmlPullParser parser) {
		tempNews.setUrlCINeol(CINeolUtils.urlForNews(tempNews.getId(), tempNews.getTitle()));
		this._xmlParserDidEndDocument(this);		
	}
	
	@Override
	protected void parserDidEndTag(XmlPullParser parser, String element) {
		return;
	}

	@Override
	protected void parserFoundCharacters(XmlPullParser parser, String data) {		

		//if (lastTag.equals(kXMLTagURLCINeol))
		//	tempNews.setUrlCINeol(data);
		
		if (lastTag.equals(kXMLTagVisits))
			tempNews.setNumberOfVisits(Integer.valueOf(data));	
		
		else if (lastTag.equals(kXMLTagAuthor))
			tempNews.setAuthor(data);

		else if (lastTag.equals(kXMLTagDate))
			tempNews.setDate(CINeolUtils.dateFromString(data, "yyyy-MM-dd"));
		
		else if (lastTag.equals(kXMLTagContent))
			tempNews.setContent(data);
		
		else if (lastTag.equals(kXMLTagComments))
			tempNews.setNumberOfComments(Integer.valueOf(data));	
		
		else
			super.parserFoundCharacters(parser, data);
		
		lastTag = "";
	}
	
	@Override
	public String parserType() {
		return PARSE_TYPE;
	}
}