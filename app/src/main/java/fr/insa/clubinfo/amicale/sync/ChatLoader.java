package fr.insa.clubinfo.amicale.sync;

import android.os.AsyncTask;

import java.util.GregorianCalendar;

import fr.insa.clubinfo.amicale.interfaces.ChatMessageListener;
import fr.insa.clubinfo.amicale.models.Chat;
import fr.insa.clubinfo.amicale.models.ChatMessage;

/**
 * Created by Proïd on 05/06/2016.
 */

public class ChatLoader {
    private final ChatMessageListener listener;
    private AsyncTask<Void, Void, Chat> currentTask;

    public ChatLoader(ChatMessageListener listener) {
        this.listener = listener;
    }

    public void loadAsync() {
        currentTask = new AsyncTask<Void, Void, Chat>() {
            @Override
            protected Chat doInBackground(Void... params) {
                Chat c = new Chat();
                for(int i = 0; i < 20; i++) {
                    ChatMessage msg = new ChatMessage();
                    msg.setSelf(Math.random() > 0.5);
                    msg.setContent("An awesome message n°" + i);
                    msg.setDate(new GregorianCalendar());
                    c.addMessage(msg);
                }
                return c;
            }

            @Override
            protected void onPostExecute(Chat chat) {
                listener.onChatLoaded(chat);
            }

            @Override
            protected void onCancelled(Chat chat) {
                listener.onChatSyncFailed();
            }
        }.execute();
    }

    public void cancel() {
        currentTask.cancel(true);
    }
}
