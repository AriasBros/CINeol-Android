package net.cineol.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.os.Handler;

public abstract class XMLParser implements Runnable {
	
	private static final String TAG = "XMLParser";
	
	protected Context context;
	private XMLParserDelegate delegate;
	protected XmlPullParser parser;	
	protected String lastTag;

	// Threads
	protected Handler mainThread;
	protected ExecutorService threadParse;
	protected Future<?> pendingParse;
	
	public abstract String parserType();
	protected abstract void parserDidStartTag(XmlPullParser parser, String element);
	protected abstract void parserDidEndTag(XmlPullParser parser, String element);
	protected abstract void parserFoundCharacters(XmlPullParser parser, String data);
	protected abstract void parserDidStartDocument(XmlPullParser parser);
	protected abstract void parserDidEndDocument(XmlPullParser parser);
	
	public XMLParser(Context context, InputStream data) {	
		this.context = context;
		mainThread = new Handler();

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	
	        factory.setNamespaceAware(false);
	        
	        this.parser = factory.newPullParser();
	        this.parser.setInput(data, null);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}
	
	public void parse() {
		threadParse = Executors.newSingleThreadExecutor();
		
		// Cancel previous thread.
		if (pendingParse != null)
			pendingParse.cancel(true);
		
		pendingParse = threadParse.submit(this);
	}
	
	
	public void run() {
		this._parse();
	}
	
	protected void _parse() {		
        try {
			int eventType = parser.getEventType();
        
			while (eventType != XmlPullParser.END_DOCUMENT) {
	        	switch (eventType) {
		       	 	case XmlPullParser.START_DOCUMENT:
		       	 		this.parserDidStartDocument(parser);
		       	 		break;
		       	 		
		       	 	case XmlPullParser.START_TAG:
		       	 		this.parserDidStartTag(parser, parser.getName());
		       	 		break;
		       	 		
		       	 	case XmlPullParser.CDSECT:
		       	 		break;
		       	
		       	 	case XmlPullParser.TEXT:
		       	 		this.parserFoundCharacters(parser, parser.getText());
		       	 		break;
		       	 		
		       	 	case XmlPullParser.END_TAG:
		       	 		this.parserDidEndTag(parser, parser.getName());
		       	 		break;
		       	 	
		       	 	case XmlPullParser.END_DOCUMENT:
		       	 		break;
	       	 	}

	       		eventType = parser.next();
			}
			
   			this.parserDidEndDocument(parser);

       	} catch (XmlPullParserException e) {
			//e.printStackTrace();
       	} catch (IOException e) {
			//e.printStackTrace();
       	}
	}
	
	public void setDelegate(XMLParserDelegate delegate) {
		this.delegate = delegate;
	}
	
	public XMLParserDelegate getDelegate() {
		return delegate;
	}
	
	protected void _xmlParserDidEndTag(final XMLParser parser, final String elementName) {
		mainThread.post(new Runnable() {
			public void run() {
				delegate.xmlParserDidEndTag(parser, elementName);
			}
		});	
	}
	
	protected void _xmlParserDidStartDocument(final XMLParser parser) {
		mainThread.post(new Runnable() {
			public void run() {
				delegate.xmlParserDidStartDocument(parser);
			}
		});		
	}

	protected void _xmlParserDidEndDocument(final XMLParser parser) {
		mainThread.post(new Runnable() {
			public void run() {
				delegate.xmlParserDidEndDocument(parser);
			}
		});
		
		threadParse.shutdownNow();
	}
	
	protected void _xmlParserDidFindAttribute(final XMLParser parser,
											  final String attributeName,
											  final String attributeValue)
	{
		mainThread.post(new Runnable() {
			public void run() {
				delegate.xmlParserDidFindAttribute(parser, attributeName, attributeValue);
			}
		});
	}
}
