package net.cineol.xml;

import java.io.InputStream;

import net.cineol.model.Image;
import net.cineol.model.Movie;
import net.cineol.model.Person;
import net.cineol.model.Video;
import net.cineol.utils.CINeolUtils;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Log;

public class XMLParserMovie extends XMLParser {

	public static final String PARSE_TYPE = "XMLParserMovie";
	private static final String TAG = PARSE_TYPE;
	
	private Movie movie = new Movie();
	private Video tempVideo = new Video();
	private Image tempImage = new Image();
	private Person tempPerson = new Person();
	
	private boolean downloadThumbPoster;
	private boolean processingTrailers = false;
	private boolean processingPerson = false;
	
	// XML Tags.
	protected static final String kXMLTagMovie				= "Pelicula";
	protected static final String kXMLTagIndexTitle			= "titulo";
	protected static final String kXMLTagTitle				= "titulobonito";	
	protected static final String kXMLTagOriginalTitle		= "title";
	protected static final String kXMLTagYear				= "anio";
	protected static final String kXMLTagGenre 				= "genero";
	protected static final String kXMLTagCountry			= "pais";
	protected static final String kXMLTagDuration			= "duracion";
	protected static final String kXMLTagFormat				= "formato";
	protected static final String kXMLTagURL				= "url";
	protected static final String kXMLTagXMLURL 			= "xmlurl";
	protected static final String kXMLTagTrailers			= "trailers";
	protected static final String kXMLTagTrailer			= "trailer";
	protected static final String kXMLTagDescTrailer		= "Desc";
	protected static final String kXMLTagIDTrailer			= "idDailymotion";
	protected static final String kXMLTagDateTrailer		= "fecha";
	protected static final String kXMLTagGallery			= "Galeria";
	protected static final String kXMLTagPhoto				= "foto";
	protected static final String kXMLTagThumbPhoto			= "thumb90";
	protected static final String kXMLTagBigPhoto			= "grande";
	protected static final String kXMLTagSpainPremier		= "fecha_estreno_españa";
	protected static final String kXMLTagOriginalPremier 	= "fecha_estreno_origen";
	protected static final String kXMLTagDirectors			= "Directores";
	protected static final String kXMLTagDirector			= "Director";
	protected static final String kXMLTagProducers			= "Productores";
	protected static final String kXMLTagProducer			= "Productor";	
	protected static final String kXMLTagScriptwriters		= "Guionistas";
	protected static final String kXMLTagScriptwriter		= "Guionista";	
	protected static final String kXMLTagPhotographers		= "Fotografos";	
	protected static final String kXMLTagPhotographer		= "Fotografo";
	protected static final String kXMLTagMusicians			= "Musicos";
	protected static final String kXMLTagMusician			= "Musico";
	protected static final String kXMLTagActors				= "Actores";
	protected static final String kXMLTagActor				= "Actor";
	protected static final String kXMLTagName				= "Nombre";
	protected static final String kXMLTagInfo				= "Info";
	protected static final String kXMLTagSpainTakings		= "recaudacionspain";
	protected static final String kXMLTagUsaTakings			= "recaudacionusa";
	protected static final String kXMLTagSynopsis 			= "sinopsis";
	protected static final String kXMLTagRating				= "nota";
	protected static final String kXMLTagVotes 				= "numvotos";
	protected static final String kXMLTagComments			= "numComentarios";
	protected static final String kXMLTagURLLittlePoster 	= "Cartel-150x90";
	protected static final String kXMLTagURLBigPoster 		= "CartelGrande";

	public XMLParserMovie(Context context, InputStream data, XMLParserDelegate delegate, boolean thumb) {
		super(context, data);
		this.downloadThumbPoster = thumb;
		this.setDelegate(delegate);
	}
	
	public Movie getMovie() {
		return movie;
	}
	
	public void setDownloadThumbPoster(boolean downloadThumbPoster) {
		this.downloadThumbPoster = downloadThumbPoster;
	}

	public boolean getDownloadThumbPoster() {
		return downloadThumbPoster;
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
		if (element.equals(kXMLTagTrailers))
			this.processingTrailers = false;
		
		else if (element.equals(kXMLTagTrailer))
			movie.addVideo(tempVideo);
		
		else if (element.equals(kXMLTagPhoto))
			movie.addImage(tempImage);
		
		else if (element.equals(kXMLTagDirectors)  	  ||
				 element.equals(kXMLTagProducers)	  ||
				 element.equals(kXMLTagScriptwriters) ||
				 element.equals(kXMLTagPhotographers) ||
				 element.equals(kXMLTagMusicians)	  ||
				 element.equals(kXMLTagActors))
			this.processingPerson = false;
		
		else if (element.equals(kXMLTagDirector)  	 ||
				 element.equals(kXMLTagProducer)	 ||
				 element.equals(kXMLTagScriptwriter) ||
				 element.equals(kXMLTagPhotographer) ||
				 element.equals(kXMLTagMusician))
			movie.addCredit(tempPerson);
		
		else if (element.equals(kXMLTagActor))
			movie.addActor(tempPerson);
	}

	@Override
	protected void parserDidStartTag(XmlPullParser parser, String element) {
 		lastTag = element;
 		
		if (lastTag.equals(kXMLTagTrailers))
			this.processingTrailers = true;
		
		else if (element.equals(kXMLTagDirectors)  	  ||
				 element.equals(kXMLTagProducers)	  ||
				 element.equals(kXMLTagScriptwriters) ||
				 element.equals(kXMLTagPhotographers) ||
				 element.equals(kXMLTagMusicians)	  ||
				 element.equals(kXMLTagActors))
			this.processingPerson = true;
	}

	@Override
	protected void parserFoundCharacters(XmlPullParser parser, String data) {
		if (lastTag.equals(kXMLTagIndexTitle))
			movie.setIndexTitle(data);
		
		else if (lastTag.equals(kXMLTagTitle))
			movie.setTitle(data);
		
		else if (lastTag.equals(kXMLTagOriginalTitle))
			movie.setOriginalTitle(data);		
		
		else if (lastTag.equals(kXMLTagYear))
			movie.setYear(Integer.valueOf(data));
		
		else if (lastTag.equals(kXMLTagGenre))
			movie.setGenre(data);
	
		else if (lastTag.equals(kXMLTagCountry))
			movie.setCountry(data);
		
		else if (lastTag.equals(kXMLTagFormat))
			movie.setFormat(data);
		
		else if (lastTag.equals(kXMLTagDuration))
 			try {
 				movie.setDuration(Integer.valueOf(data));
 			} catch (NumberFormatException e) {
 				movie.setDuration(0);
 			}
 			
 		else if (movie.getUrlCINeol() == null && lastTag.equals(kXMLTagURL))
 			movie.setUrlCINeol(data);
 			
		else if (!this.processingPerson && lastTag.equals(kXMLTagXMLURL))
			movie.setId(CINeolUtils.movieIDFromXMLURL(data));
		
		else if (lastTag.equals(kXMLTagTrailer))
			tempVideo = new Video();

		else if (lastTag.equals(kXMLTagDescTrailer))
			tempVideo.setDescription(data);

		else if (lastTag.equals(kXMLTagIDTrailer))
			tempVideo.setId(data);
		
		else if (lastTag.equals(kXMLTagDateTrailer))
			tempVideo.setDate(CINeolUtils.dateFromString(data, "dd-MMM-yyyy"));
		
		else if (lastTag.equals(kXMLTagPhoto))
			tempImage = new Image();
		
		else if (lastTag.equals(kXMLTagThumbPhoto))
			tempImage.setUrlThumbImage(data);
		
		else if (lastTag.equals(kXMLTagBigPhoto)) {
			tempImage.setUrlImage(data);
			tempImage.setId(CINeolUtils.photoIDFromURL(data));
		}
		
		else if (lastTag.equals(kXMLTagSpainPremier))
 			movie.setSpainPremier(CINeolUtils.dateFromString(data, "dd-MMM-yyyy"));
		
		else if (lastTag.equals(kXMLTagOriginalPremier))
 			movie.setOriginalPremier(CINeolUtils.dateFromString(data, "dd-MMM-yyyy"));
	
		else if (lastTag.equals(kXMLTagDirector)  	 ||
				 lastTag.equals(kXMLTagProducer)	 ||
				 lastTag.equals(kXMLTagScriptwriter) ||
				 lastTag.equals(kXMLTagPhotographer) ||
				 lastTag.equals(kXMLTagMusician)	 ||
				 lastTag.equals(kXMLTagActor))
		{
			this.tempPerson = new Person();
			this.tempPerson.setJob(this.jobForLastTag());
		}
		
		else if (lastTag.equals(kXMLTagName))
			this.tempPerson.setName(data);

		else if (this.processingPerson && lastTag.equals(kXMLTagURL)) {
			this.tempPerson.setUrlCINeol(data);
			this.tempPerson.setId(CINeolUtils.personIDFromURL(data));
		}
		
		else if (lastTag.equals(kXMLTagInfo))
			this.tempPerson.setJob(data);	
		
		else if (lastTag.equals(kXMLTagSpainTakings))
			try {
				movie.setSpainTakings(Integer.valueOf(data));
			}
			catch (NumberFormatException e) {
				movie.setSpainTakings(-1);
			}
		
		else if (lastTag.equals(kXMLTagUsaTakings))
			try {
				movie.setUsaTakings(Integer.valueOf(data));
			}
			catch (NumberFormatException e) {
				movie.setUsaTakings(-1);
			}
		
		else if (lastTag.equals(kXMLTagSynopsis))
 			movie.setSynopsis(data);
		
		else if (lastTag.equals(kXMLTagRating))
 			movie.setRating(Float.valueOf(data));
		
		else if (lastTag.equals(kXMLTagVotes))
 			movie.setVotes(Integer.valueOf(data));
		
		else if (lastTag.equals(kXMLTagComments))
 			movie.setNumberOfComments((Integer.valueOf(data)));
		
		else if (downloadThumbPoster && lastTag.equals(kXMLTagURLLittlePoster))
			movie.setPosterThumbnail(CINeolUtils.thumbPosterFromURL(this.context, data));
		
		else if (lastTag.equals(kXMLTagURLBigPoster))
			movie.setPoster(CINeolUtils.posterFromURL(this.context, data));	
		
		lastTag = "";		
	}
	
	private String jobForLastTag() {
		if (lastTag.equals(kXMLTagDirector))
			return "Director";

		else if (lastTag.equals(kXMLTagProducer))
			return "Productor";
		
		else if (lastTag.equals(kXMLTagScriptwriter))
			return "Guionista";
			
		else if (lastTag.equals(kXMLTagPhotographer))
			return "Fotógrafo";
			
		else if (lastTag.equals(kXMLTagMusician))
			return "Músico";
			
		return null;
	}
	
	@Override
	public String parserType() {
		return PARSE_TYPE;
	}
}