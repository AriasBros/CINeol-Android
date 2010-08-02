package es.leafsoft.cineol.activities;

import net.cineol.model.Movie;

public interface MovieActivityDelegate {

	public void movieActivityDidLoadMovie(Movie movie);
}
