package fr.insa.clubinfo.amicale.models;

import java.util.ArrayList;


public class Chat {
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
}
