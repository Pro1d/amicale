package fr.insa.clubinfo.amicale.models;

import java.util.GregorianCalendar;
import java.util.HashMap;

public class ChatMessage {

	private String imageURL = null;
	private String content = "";
	private String senderName = "";
	private boolean own = false;
    GregorianCalendar date = new GregorianCalendar();
	private String firebaseKey = "";
	private double timestamp = 0.0;
    private String senderId = "";

	public ChatMessage(String firebaseKey) {
		this.firebaseKey = firebaseKey;
	}

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

    public double getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(double timestamp) {
		this.timestamp = timestamp;
        date.setTimeInMillis((long) (timestamp * 1000));
	}

	public String getFirebaseKey() {
		return firebaseKey;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isOwn() {
		return own;
	}

	public void setOwn(boolean own) {
		this.own = own;
	}

	public GregorianCalendar getDate() {
		return date;
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

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("dateTimestamp", timestamp);
        map.put("isMedia", false);
        map.put("senderDisplayName", senderName);
        map.put("senderId", senderId);
        if(!hasImage()) {
            map.put("imageURL", "");
            map.put("text", content);
        } else {
            map.put("imageURL", imageURL);
            map.put("text", "");
        }
		// Generate unique hash value for the stupid iOS app
		map.put("hashValue", Integer.toHexString(senderId.hashCode())
				+Integer.toHexString(content.hashCode())
				+Long.toHexString((long)(timestamp*1000)));

        return map;
    }
}
