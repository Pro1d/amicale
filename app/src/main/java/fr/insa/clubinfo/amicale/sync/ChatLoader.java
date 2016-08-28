package fr.insa.clubinfo.amicale.sync;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashSet;

import fr.insa.clubinfo.amicale.interfaces.ChatMessageListener;
import fr.insa.clubinfo.amicale.models.ChatMessage;

/**
 * Created by Proïd on 05/06/2016.
 */

public class ChatLoader implements ChildEventListener, ValueEventListener {
    private final ChatMessageListener listener;
    private final Query childQuery;
    private Query loadMoreQuery;
    private final HashSet<String> activeImageDownload = new HashSet<>();
    private final String uid;

    public ChatLoader(ChatMessageListener listener, String uid) {
        this.listener = listener;
        this.uid = uid;
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

    public void cancel() {
        childQuery.removeEventListener((ChildEventListener)this);
        if(loadMoreQuery != null) {
            loadMoreQuery.removeEventListener((ValueEventListener)this);
            loadMoreQuery = null;
        }
        FirebaseStorage storage = FirebaseStorage.getInstance();
        for(String dataURL : activeImageDownload) {
            for(FileDownloadTask task : storage.getReferenceFromUrl(dataURL).getActiveDownloadTasks()) {
                task.cancel();
            }
        }
    }

    private ChatMessage createChatMessage(DataSnapshot data) {
        ChatMessage m = new ChatMessage(data.getKey());
        if(data.hasChild("senderDisplayName"))
            m.setSenderName((String)data.child("senderDisplayName").getValue());
        if(data.hasChild("text"))
            m.setContent((String)data.child("text").getValue());
        if(data.hasChild("senderId") && data.child("senderId").getValue().equals(uid))
            m.setOwn(true);
        else
            m.setOwn(false);
        if(data.hasChild("imageURL")) {
            String url = (String) data.child("imageURL").getValue();
            if(!url.isEmpty()) {
                m.setImageURL(url);
                loadImage(url, m);
            }
        }
        if(data.hasChild("dateTimestamp")) {
            m.setTimestamp((double)data.child("dateTimestamp").getValue());
        }
        return m;
    }


    private void loadImage(final String dataURL, final ChatMessage message) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        activeImageDownload.add(dataURL);
        storage.getReferenceFromUrl(dataURL).getBytes(1024*1024*8).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                activeImageDownload.remove(dataURL);
                message.setImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                listener.onImageLoaded(message);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                activeImageDownload.remove(dataURL);
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
