package es.leafsoft.cineol.activities;

import net.cineol.model.News;

public interface NewsActivityDelegate {

	public void newsActivityDidLoadNews(News news);
}
