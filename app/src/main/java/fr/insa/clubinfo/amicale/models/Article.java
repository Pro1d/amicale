package fr.insa.clubinfo.amicale.models;

import android.graphics.Bitmap;

import java.util.GregorianCalendar;

public class Article {

	private String title;
	private Bitmap image;
	private String imageURL;
	private String content;
	private GregorianCalendar date;
	private final String firebaseKey;
	private long timestampInverse;

	public Article(String firebaseKey) {
		this.firebaseKey = firebaseKey;
	}

	public void setTimestampInverse(long timestampInverse) {
		this.timestampInverse = timestampInverse;
	}

	public long getTimestampInverse() {
		return timestampInverse;
	}

	public String getFirebaseKey() {
		return firebaseKey;
	}

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

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean hasImage() {
		return imageURL != null;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

}
