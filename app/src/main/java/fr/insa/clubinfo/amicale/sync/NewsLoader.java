package fr.insa.clubinfo.amicale.sync;

import android.graphics.BitmapFactory;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import fr.insa.clubinfo.amicale.interfaces.OnNewsUpdatedListener;
import fr.insa.clubinfo.amicale.models.Article;
import fr.insa.clubinfo.amicale.models.News;

/**
 * Created by Proïd on 05/06/2016.
 */

public class NewsLoader implements ValueEventListener, ChildEventListener {
    private final OnNewsUpdatedListener listener;
    private Query query = null;
    private Query observer = null;

    public NewsLoader(OnNewsUpdatedListener listener) {
        this.listener = listener;
        observer = FirebaseDatabase.getInstance().getReference().child("posts");
        //observer.addChildEventListener(this);
    }

    public void loadMore(int count, long lastTimestampInverse) {
        query = FirebaseDatabase.getInstance().getReference().child("posts")
                .limitToFirst(count)
                .startAt(lastTimestampInverse, "timestampInverse")
                .orderByChild("timestampInverse");
        query.addValueEventListener(this);
    }

    public void cancel() {
        observer.removeEventListener((ChildEventListener)this);
        if(query != null) {
            query.removeEventListener((ValueEventListener) this);
            query = null;
        }
    }

    private Article createArticle(DataSnapshot item) {
        Article article = new Article(item.getKey());

        if(item.hasChild("description"))
            article.setContent((String) item.child("description").getValue());
        if(item.hasChild("title"))
            article.setTitle((String) item.child("title").getValue());

        if(item.hasChild("date")) {
            try {
                String[] s = ((String) item.child("date").getValue()).split("-");
                int day = Integer.parseInt(s[0]);
                int month = Integer.parseInt(s[1]);
                int year = Integer.parseInt(s[2]);
                article.setDate(new GregorianCalendar(year, month - 1, day));
            } catch (Exception e) {

            }
        }

        if(item.hasChild("timestampInverse")) {
            article.setTimestampInverse((long)item.child("timestampInverse").getValue());
        }

        if(item.hasChild("imagePresents") && ((Boolean) item.child("imagePresents").getValue())) {
            String url = (String) item.child("imageURL").getValue();
            if(article.getImageURL() == null || !article.getImageURL().equals(url)) {
                article.setImageURL(url);
                article.setImage(null);
                loadImage(url, article);
            }
        }
        else {
            article.setImageURL(null);
            article.setImage(null);
        }

        return article;
    }

    private void loadImage(String dataURL, final Article article) {
        /// TODO cancel
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storage.getReferenceFromUrl(dataURL).getBytes(1024*1024*8).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                article.setImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                listener.onImageLoaded(article);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                article.setImage(null);
                article.setImageURL(null);
                listener.onImageLoaded(article);
            }
        });
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        ArrayList<Article> list = new ArrayList<>((int)dataSnapshot.getChildrenCount());
        for(DataSnapshot item : dataSnapshot.getChildren()) {
            list.add(createArticle(item));
        }
        listener.onNewsLoaded(list);

        if(query != null) {
            query.removeEventListener((ValueEventListener) this);
            query = null;
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Article a = createArticle(dataSnapshot);
        listener.onNewArticleReceived(a);
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
        listener.onNewsSyncCanceled();
    }
}
