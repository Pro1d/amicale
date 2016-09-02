package fr.insa.clubinfo.amicale.models;

import java.util.GregorianCalendar;

public class Article {

	private String title;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean hasImage() {
		return imageURL != null && !imageURL.isEmpty();
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getImageURL() {
		return imageURL;
	}
}
