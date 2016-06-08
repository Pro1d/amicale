package fr.insa.clubinfo.amicale.models;

import android.graphics.drawable.Drawable;

import java.util.GregorianCalendar;

public class Article {

	private String title;
	private Drawable image;
	private boolean hasImage;
	private String content;
	private GregorianCalendar date;

	public GregorianCalendar getDate() {
		return date;
	}

	public void setDate(GregorianCalendar date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Drawable getImage() {
		return image;
	}

	public void setImage(Drawable image) {
		this.image = image;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean hasImage() {
		return hasImage;
	}

	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}
}
