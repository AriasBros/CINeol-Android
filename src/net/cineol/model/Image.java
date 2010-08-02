package net.cineol.model;

import android.graphics.drawable.BitmapDrawable;

public class Image {

	private String id;
	private String urlThumbImage;
	private String urlImage;
	private String pathThumbImage;
	private String pathImage;
	
	private BitmapDrawable thumbnail;
	private BitmapDrawable image;
	
	public Image() {
		super();
	}
	
	public Image(String thumb, String image) {
		this.urlThumbImage = thumb;
		this.urlImage = image;
	}
	
	public Image(BitmapDrawable thumb, BitmapDrawable image) {
		this.thumbnail = thumb;
		this.image = image;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrlThumbImage() {
		return urlThumbImage;
	}

	public void setUrlThumbImage(String urlThumbImage) {
		this.urlThumbImage = urlThumbImage;
	}

	public String getUrlImage() {
		return urlImage;
	}

	public void setUrlImage(String urlImage) {
		this.urlImage = urlImage;
	}
	
	public String getPathThumbImage() {
		return pathThumbImage;
	}

	public void setPathThumbImage(String pathThumbImage) {
		this.pathThumbImage = pathThumbImage;
	}

	public String getPathImage() {
		return pathImage;
	}

	public void setPathImage(String pathImage) {
		this.pathImage = pathImage;
	}

	public BitmapDrawable getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(BitmapDrawable thumbnail) {
		this.thumbnail = thumbnail;
	}

	public BitmapDrawable getImage() {
		return image;
	}

	public void setImage(BitmapDrawable image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "Image [urlPoster=" + urlImage + ", urlThumbPoster="
				+ urlThumbImage + "]";
	}
}