package net.cineol.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.cineol.model.News;
import net.cineol.model.Post;
import net.cineol.utils.CINeolUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class XMLParserNews extends XMLParser {

	public static final String PARSE_TYPE = "XMLParserNews";
	private static final String TAG = PARSE_TYPE;
	
	protected boolean processingNews = false;
	
	protected News tempNews = new News();
	protected ConcurrentLinkedQueue<News> news = new ConcurrentLinkedQueue<News>();
	protected String imageURL;
	
	// XML Tags.	
	public static final String 	kXMLTagNews			= "Noticias";
	public static final String 	kXMLTagSingleNews	= "Noticia";
	public static final String	kXMLTagNewsID		= "idnoticia";
	public static final String 	kXMLTagTitle		= "titulo";
	public static final String 	kXMLTagIntroduction	= "introduccion";
	public static final String 	kXMLTagURLImage		= "imagen_grande";
	public static final String 	kXMLTagComments		= "comentarios";
	
	public static final String 	kXMLTagURLCINeol	= "cineol";

	// XML Attributes.
	public static final String kXMLAttributeTotalNews	= "TotalNoticias";
	
	public XMLParserNews(Context context, InputStream data, XMLParserDelegate delegate) {
		super(context, data);
		this.setDelegate(delegate);
	}
	
	public News getNews() {
		return news.poll();
	}
	
	@Override
	protected void parserDidStartDocument(XmlPullParser parser) {
		this._xmlParserDidStartDocument(this);
	}

	@Override
	protected void parserDidEndDocument(XmlPullParser parser) {
		this._xmlParserDidEndDocument(this);		
	}

	@Override
	protected void parserDidEndTag(XmlPullParser parser, String element) {
 		if (element.equals(kXMLTagSingleNews)) {
 			
 			tempNews.setImage(CINeolUtils.posterFromURL(this.context, imageURL));
 			imageURL = "";
 			
 			tempNews.setUrlCINeol(CINeolUtils.urlForNews(tempNews.getId(), tempNews.getTitle()));
 			
 			news.add(tempNews);
 			this._xmlParserDidEndTag(this, element);
 		}
	}

	@Override
	protected void parserDidStartTag(XmlPullParser parser, String element) {
		
 		lastTag = element;
 		
		if (lastTag.equals(kXMLTagNews))
			this._xmlParserDidFindAttribute(this, parser.getAttributeName(0), parser.getAttributeValue(0));
	}

	@Override
	protected void parserFoundCharacters(XmlPullParser parser, String data) {		
		
 		if (lastTag.equals(kXMLTagSingleNews))
 			tempNews = new News();
 		
 		else if (lastTag.equals(kXMLTagNewsID))
			tempNews.setId(Integer.valueOf(data));	
		
		else if (lastTag.equals(kXMLTagTitle))
			tempNews.setTitle(data);

		else if (lastTag.equals(kXMLTagIntroduction))
			tempNews.setIntroduction(data.trim());
		
		else if (lastTag.equals(kXMLTagURLImage))
			imageURL = data.trim();
		
		lastTag = "";
	}
	
	@Override
	public String parserType() {
		return PARSE_TYPE;
	}
}