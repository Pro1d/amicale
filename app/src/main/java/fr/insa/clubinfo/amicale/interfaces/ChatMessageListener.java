package fr.insa.clubinfo.amicale.interfaces;

import android.graphics.Bitmap;

import java.util.List;

import fr.insa.clubinfo.amicale.models.Chat;
import fr.insa.clubinfo.amicale.models.ChatMessage;

/**
 * Created by Proïd on 06/06/2016.
 */

public interface ChatMessageListener {
    void onMoreChatMessagesLoaded(List<ChatMessage> m);
    void onNewChatMessageReceived(ChatMessage msg);
    void onImageLoaded(ChatMessage msg);
    /*void onChatSyncCanceled();
    void onChatSyncFailed();*/
}
