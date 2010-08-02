package net.cineol.webservice;

import java.io.InputStream;

public interface CINeolNotificationCenter {
	public void cineolDidFinishLoadingMovie(final InputStream data);
	public void cineolDidFinishLoadingMovieShowtimes(final InputStream data);
	public void cineolDidFinishLoadingComments(final InputStream data);
	public void cineolDidFinishLoadingNews(final InputStream data);
	public void cineolDidFinishLoadingSingleNews(final InputStream data);
	public void cineolDidFinishSearchMovies(final InputStream data);
	public void cineolDidDownloadThumbnail(final InputStream data);
	public void cineolDidDownloadImage(final InputStream data);
	public void cineolDidDownloadThumbnails();
}
