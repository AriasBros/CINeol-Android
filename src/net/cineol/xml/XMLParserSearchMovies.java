package net.cineol.xml;

import java.io.InputStream;
import java.util.ArrayList;
import net.cineol.model.Movie;
import net.cineol.utils.CINeolUtils;
import org.xmlpull.v1.XmlPullParser;

import es.leafsoft.utils.InputStreamUtils;
import android.content.Context;
import android.util.Log;

public class XMLParserSearchMovies extends XMLParser {

	public static final String PARSE_TYPE = "XMLParserSearchMovies";
	private static final String TAG = PARSE_TYPE;
	
	private Movie tempMovie = new Movie();
	
	// XML Tags.
	public static final String kXMLTagResults			= "resultados";
	public static final String kXMLTagMovie				= "pelicula";
	public static final String kXMLTagIndexTitle		= "titulo";
	public static final String kXMLTagOriginalTitle		= "title";
	public static final String kXMLTagYear				= "anio";
	public static final String kXMLTagCINeolURL			= "url";
	public static final String kXMLTagXMLURL 			= "xmlurl";
			
	protected ArrayList<Movie> movies = new ArrayList<Movie>();
	
	
	public XMLParserSearchMovies(Context context, InputStream data, XMLParserDelegate delegate) {
		super(context, data);
		this.setDelegate(delegate);		
	}
	
	public ArrayList<Movie> getResults() {
		return movies;
	}


	protected void parserDidStartTag(XmlPullParser parser, String element) {
 		lastTag = element;
	}
	
	protected void parserDidEndTag(XmlPullParser parser, String element) {
 		if (element.equals(kXMLTagMovie)) {
			movies.add(tempMovie);
 			this._xmlParserDidEndTag(this, element);
 		}
	}
	
	protected void parserFoundCharacters(XmlPullParser parser, String data) {	
		if (lastTag.equals(kXMLTagMovie))
			tempMovie = new Movie();
		
		else if (lastTag.equals(kXMLTagIndexTitle))
			tempMovie.setIndexTitle(data);

		else if (lastTag.equals(kXMLTagOriginalTitle))
			tempMovie.setOriginalTitle(data);
		
		else if (lastTag.equals(kXMLTagYear))
			tempMovie.setYear(Integer.valueOf(data));
		
		else if (lastTag.equals(kXMLTagCINeolURL))
			tempMovie.setUrlCINeol(data);
		
		else if (lastTag.equals(kXMLTagXMLURL))
			tempMovie.setId(CINeolUtils.tempMovieIDFromXMLURL(data));

		lastTag = "";
	}
	
	@Override
	protected void parserDidStartDocument(XmlPullParser parser) {
		this._xmlParserDidStartDocument(this);
	}
	
	protected void parserDidEndDocument(XmlPullParser parser) {
		this._xmlParserDidEndDocument(this);
	}

	@Override
	public String parserType() {
		return PARSE_TYPE;
	}
}
