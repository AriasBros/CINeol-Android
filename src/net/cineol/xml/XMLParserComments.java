package net.cineol.xml;

import java.io.InputStream;
import java.util.ArrayList;

import net.cineol.model.Post;
import net.cineol.utils.CINeolUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class XMLParserComments extends XMLParser {

	public static final String PARSE_TYPE = "XMLParserComments";
	private static final String TAG = PARSE_TYPE;
	
	private Post tempPost = new Post();
	private ArrayList<Post> comments = new ArrayList<Post>();
	
	// XML Tags.
	public static final String kXMLTagPosts		= "Posts";
	public static final String kXMLTagPost		= "Post";
	public static final String kXMLTagUser		= "usuario";
	public static final String kXMLTagDate		= "fecha";
	public static final String kXMLTagMessage	= "mensaje";
	
	// XML Attributes.
	public static final String kXMLAttributeTotalPosts	= "TotalPosts";
	
	public XMLParserComments(Context context, InputStream data) {
		super(context, data);
	}

	public ArrayList<Post> getComments() {
		return comments;
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
		if (element.equals(kXMLTagPost)) {
			comments.add(new Post(tempPost));
			this._xmlParserDidEndTag(this, element);
		}
	}

	@Override
	protected void parserDidStartTag(XmlPullParser parser, String element) {
 		lastTag = element;
 		
 		// Si el TAG es kXMLTagPosts obtenemos el numero de posts que devuelve el script.
		if (lastTag.equals(kXMLTagPosts))
			this._xmlParserDidFindAttribute(this, parser.getAttributeName(0), parser.getAttributeValue(0));
	}

	@Override
	protected void parserFoundCharacters(XmlPullParser parser, String data) {		
		if (lastTag.equals(kXMLTagUser))
			tempPost.setUser(data);
		
		else if (lastTag.equals(kXMLTagDate))
			tempPost.setDate(CINeolUtils.dateFromString(data, "HH:mm - dd/MM/yyyy"));
		
		else if (lastTag.equals(kXMLTagMessage))
			tempPost.setMessage(data);
		
		lastTag = "";
	}
	
	@Override
	public String parserType() {
		return PARSE_TYPE;
	}
}