package fr.insa.clubinfo.amicale.models;

import android.graphics.drawable.Drawable;

import java.util.GregorianCalendar;

public class ChatMessage {

	private Drawable image;
	private boolean hasImage = false;
	private String content;
	private boolean self;
	private GregorianCalendar date;

	public Drawable getImage() {
		return image;
	}

	public void setImage(Drawable image) {
		this.image = image;
		hasImage = true;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isSelf() {
		return self;
	}

	public void setSelf(boolean self) {
		this.self = self;
	}

	public GregorianCalendar getDate() {
		return date;
	}

	public void setDate(GregorianCalendar date) {
		this.date = date;
	}

	public boolean hasImage() {
		return hasImage;
	}

	public void hasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}

}
