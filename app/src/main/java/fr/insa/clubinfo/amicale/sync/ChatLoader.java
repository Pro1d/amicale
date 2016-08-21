package fr.insa.clubinfo.amicale.sync;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import fr.insa.clubinfo.amicale.interfaces.ChatMessageListener;
import fr.insa.clubinfo.amicale.models.Article;
import fr.insa.clubinfo.amicale.models.Chat;
import fr.insa.clubinfo.amicale.models.ChatMessage;

/**
 * Created by Proïd on 05/06/2016.
 */

public class ChatLoader implements ChildEventListener, ValueEventListener {
    private final ChatMessageListener listener;
    private final Query childQuery;
    private Query loadMoreQuery;

    public ChatLoader(ChatMessageListener listener) {
        this.listener = listener;
        double currentTimestamp = (double) System.currentTimeMillis() / 1000;
        childQuery = FirebaseDatabase.getInstance().getReference().child("messages")
                .startAt(currentTimestamp, "dateTimestamp")
                .orderByChild("dateTimestamp");
        childQuery.addChildEventListener(this);
    }

    public void loadMore(int count, double lastTimestamp) {
        loadMoreQuery = FirebaseDatabase.getInstance().getReference().child("messages")
                .limitToLast(count)
                .orderByChild("dateTimestamp")
                .endAt(lastTimestamp, "dateTimestamp");
        loadMoreQuery.addListenerForSingleValueEvent(this);
    }

    public void loadLast(int maxCount, double historyTimeInSeconds) {
        double startTimestamp = (double) System.currentTimeMillis() / 1000 - historyTimeInSeconds;
        loadMoreQuery = FirebaseDatabase.getInstance().getReference().child("messages")
                .limitToLast(maxCount)
                .startAt(startTimestamp, "dateTimestamp")
                .orderByChild("dateTimestamp");
        loadMoreQuery.addValueEventListener(this);
    }

    public void cancel() {
        childQuery.removeEventListener((ChildEventListener)this);
        if(loadMoreQuery != null) {
            loadMoreQuery.removeEventListener((ValueEventListener)this);
            loadMoreQuery = null;
        }
    }

    private ChatMessage createChatMessage(DataSnapshot data) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ChatMessage m = new ChatMessage(data.getKey());
        if(data.hasChild("senderDisplayName"))
            m.setSenderName((String)data.child("senderDisplayName").getValue());
        if(data.hasChild("text"))
            m.setContent((String)data.child("text").getValue());
        if(data.hasChild("senderId") && ((String)data.child("senderId").getValue()).equals(uid))
            m.setOwn(true);
        else
            m.setOwn(false);
        if(data.hasChild("isMedia") && (boolean) data.child("isMedia").getValue() && data.hasChild("imageURL")) {
            m.setImageURL((String) data.child("imageURL").getValue());
            loadImage(m.getImageURL(), m);
        }
        if(data.hasChild("dateTimestamp")) {
            m.setTimestamp((double)data.child("dateTimestamp").getValue());
        }
        if(data.hasChild("date")) {
            try {
                String[] s = ((String) data.child("date").getValue()).split("[ :-]");
                int year = Integer.parseInt(s[0]);
                int month = Integer.parseInt(s[1]);
                int day = Integer.parseInt(s[2]);
                int hour = Integer.parseInt(s[3]);
                int min = Integer.parseInt(s[4]);
                int sec = Integer.parseInt(s[5]);
                GregorianCalendar gc = new GregorianCalendar(year, month - 1, day, hour, min, sec);
                m.setDate(gc);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        return m;
    }


    private void loadImage(String dataURL, final ChatMessage message) {
        /// TODO cancel
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storage.getReferenceFromUrl(dataURL).getBytes(1024*1024*8).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                message.setImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                listener.onImageLoaded(message);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                message.setImage(null);
                message.setImageURL(null);
                listener.onImageLoaded(message);
            }
        });
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        ArrayList<ChatMessage> list = new ArrayList<>((int)dataSnapshot.getChildrenCount());
        for(DataSnapshot item : dataSnapshot.getChildren()) {
            list.add(createChatMessage(item));
        }
        listener.onMoreChatMessagesLoaded(list);
        if(loadMoreQuery != null) {
            loadMoreQuery.removeEventListener((ValueEventListener)this);
            loadMoreQuery = null;
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        ChatMessage m = createChatMessage(dataSnapshot);
        listener.onNewChatMessageReceived(m);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
