package fr.insa.clubinfo.amicale.interfaces;

import fr.insa.clubinfo.amicale.models.Chat;
import fr.insa.clubinfo.amicale.models.ChatMessage;

/**
 * Created by Proïd on 06/06/2016.
 */

public interface ChatMessageListener {
    void onChatLoaded(Chat chat);
    void onChatSyncFailed();
    void onNewChatMessageReceived(Chat chat, ChatMessage msg);
    void onChatSyncCanceled();
}
