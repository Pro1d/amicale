package fr.insa.clubinfo.amicale.models;

import android.graphics.Bitmap;

import java.util.ArrayList;

import fr.insa.clubinfo.amicale.interfaces.ImageList;


public class Chat implements ImageList {
	private final ArrayList<ChatMessage> messages = new ArrayList<>();

	public void addMessage(ChatMessage msg) {
		messages.add(msg);
	}
	public int getMessagesCount() {
		return messages.size();
	}
	public ChatMessage getMessage(int index) {
		return messages.get(index);
	}

	@Override
	public Bitmap getImage(int position) {
		int count = 0;
		for(ChatMessage m : messages)
			if(m.getImage() != null)
				if(count++ == position)
					return m.getImage();
		return null;
	}

	@Override
	public int getCount() {
		int count = 0;
		for(ChatMessage m : messages)
			if(m.getImage() != null)
				count++;
		return count;
	}

    public int getImagePosition(int index) {
        // No image at given index
        if(getImage(index) == null)
            return -1;

        int position = 0;
        for(int i = 0; i < index; i++)
            if(getMessage(index).getImage() != null)
                position++;

        return position;
    }
}
