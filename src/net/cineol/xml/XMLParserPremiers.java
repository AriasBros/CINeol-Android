package net.cineol.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;

import net.cineol.model.Movie;
import net.cineol.utils.CINeolUtils;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;


public class XMLParserPremiers extends XMLParser {

	public static final String PARSE_TYPE = "XMLParserPremiers";
	private static final String TAG = PARSE_TYPE;
	
	// XML Tags.
	public static final String kXMLTagPremiers			= "Estrenos";
	public static final String kXMLTagMovie				= "Pelicula";
	public static final String kXMLTagIndexTitle		= "titulo";
	public static final String kXMLTagTitle				= "titulobonito";
	public static final String kXMLTagGenre 			= "genero";
	public static final String kXMLTagFormat			= "formato";
	public static final String kXMLTagDuration			= "duracion";
	public static final String kXMLTagXMLURL 			= "xmlurl";
	public static final String kXMLTagSpainPremier		= "fecha_estreno_españa";
	public static final String kXMLTagOriginalPremier 	= "fecha_estreno_origen";
	public static final String kXMLTagSynopsis 			= "sinopsis";
	public static final String kXMLTagRating			= "nota";
	public static final String kXMLTagVotes 			= "numvotos";
	public static final String kXMLTagURLLittlePoster 	= "Cartel-150x90";

	// XML Attributes.
	public static final String kXMLAttributeTotal		= "total";
	
	// Movie Vars.
	protected String indexTitle 		= null;
	protected String title 				= null;
	protected String genre 				= null;
	protected String format 			= null;	
	protected String duration			= null;
	protected String xmlURL 			= null;
	protected String spainPremier 		= null;
	protected String originalPremier	= null;
	protected String synopsis			= null;
	protected String rating				= null;
	protected String votes 				= null;
	protected String urlLittlePoster	= null;
			
	protected ConcurrentLinkedQueue<Movie> movies = new ConcurrentLinkedQueue<Movie>();
	
	
	public XMLParserPremiers(Context context, InputStream data) {
		super(context, data);
	}
	
	/*
	public Vector<Movie> getPremiers() {
		return movies;
	}
	*/
	
	public Movie getMovie() {
		return movies.poll();
	}

	protected void parserDidStartTag(XmlPullParser parser, String element) {
 		lastTag = element;
 		
 		// Si el TAG es kXMLTagPremiers obtenemos el numero de pelis que devuelve el script.
		if (lastTag.equals(kXMLTagPremiers))
			this._xmlParserDidFindAttribute(this, parser.getAttributeName(0), parser.getAttributeValue(0));
	}
	
	protected void parserDidEndTag(XmlPullParser parser, String element) {
 		if (element.equals(kXMLTagMovie)) {
 			Movie movie = new Movie(CINeolUtils.movieIDFromXMLURL(xmlURL)); 
 			
 			movie.setIndexTitle(indexTitle);
 			movie.setTitle(title);
 			movie.setGenre(genre);
 			movie.setFormat(format);
 			movie.setSynopsis(synopsis);

 			try {
 				movie.setDuration(Integer.valueOf(duration));
 			} catch (NumberFormatException e) {
 				movie.setDuration(0);
 			}
			
 			movie.setRating(Float.valueOf(rating));
 			movie.setVotes(Integer.valueOf(votes));

 			movie.setSpainPremier(CINeolUtils.dateFromString(spainPremier, "dd-MMM-yyyy"));
 			movie.setOriginalPremier(CINeolUtils.dateFromString(originalPremier, "dd-MMM-yyyy"));

 			// Descargamos el thumbnail.
			movie.setPosterThumbnail(CINeolUtils.thumbPosterFromURL(this.context, urlLittlePoster));
			
			movies.add(movie);
 			
 			this._xmlParserDidEndTag(this, element);
 		}
	}
	
	protected void parserFoundCharacters(XmlPullParser parser, String data) {	
		if (lastTag.equals(kXMLTagIndexTitle))
			indexTitle = data;
		
		else if (lastTag.equals(kXMLTagTitle))
			title = data;
		
		else if (lastTag.equals(kXMLTagGenre))
			genre = data;
		
		else if (lastTag.equals(kXMLTagFormat))
			format = data;
		
		else if (lastTag.equals(kXMLTagDuration))
			duration = data;
		
		else if (lastTag.equals(kXMLTagXMLURL))
			xmlURL = data;
		
		else if (lastTag.equals(kXMLTagSpainPremier))
			spainPremier = data;
		
		else if (lastTag.equals(kXMLTagOriginalPremier))
			originalPremier = data;
		
		else if (lastTag.equals(kXMLTagSynopsis))
			synopsis = data;
		
		else if (lastTag.equals(kXMLTagRating))
			rating = data;
		
		else if (lastTag.equals(kXMLTagVotes))
			votes = data;
		
		else if (lastTag.equals(kXMLTagURLLittlePoster))
			urlLittlePoster = data;
		
		lastTag = "";
	}
	
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
