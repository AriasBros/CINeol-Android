package es.leafsoft.cineol.activities;

public interface PremiersActivityDelegate {
	abstract void premiersActivityWillShowMovie(PremiersActivity activity);
	abstract void premiersActivityDidShowMovie(PremiersActivity activity);
}
