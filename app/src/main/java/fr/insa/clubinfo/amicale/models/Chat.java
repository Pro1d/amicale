package fr.insa.clubinfo.amicale.models;

import java.util.ArrayList;
import java.util.List;

import fr.insa.clubinfo.amicale.interfaces.ImageList;


public class Chat implements ImageList {
	private final ArrayList<ChatMessage> messages = new ArrayList<>();

	public void addMessagesToBack(List<ChatMessage> list) {
		messages.addAll(0, list);
	}
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
	public String getImageURL(int position) {
		int count = 0;
		for(ChatMessage m : messages)
			if(m.hasImage())
				if(count++ == position)
					return m.getImageURL();
		return null;
	}

	@Override
	public int getImageCount() {
		int count = 0;
		for(ChatMessage m : messages)
			if(m.hasImage())
				count++;
		return count;
	}

    public int getImagePosition(int index) {
        // No image at given index
        if(!getMessage(index).hasImage())
            return -1;

        int position = 0;
        for(int i = 0; i < index; i++)
            if(getMessage(i).hasImage())
                position++;

        return position;
    }

	public int getIndex(String key) {
		for(int i = messages.size(); --i >= 0;)
			if(getMessage(i).getFirebaseKey().equals(key))
				return i;
		return -1;
	}

	public double getOldestTimestamp() {
		if(messages.size() == 0)
			return 0;
		else
			return messages.get(0).getTimestamp();
	}
}
