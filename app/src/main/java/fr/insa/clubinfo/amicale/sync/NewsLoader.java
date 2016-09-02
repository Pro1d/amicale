package fr.insa.clubinfo.amicale.sync;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;

import fr.insa.clubinfo.amicale.interfaces.OnNewsUpdatedListener;
import fr.insa.clubinfo.amicale.models.Article;

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
                e.printStackTrace();
            }
        }

        if(item.hasChild("timestampInverse")) {
            article.setTimestampInverse((long)item.child("timestampInverse").getValue());
        }

        article.setImageURL(null);
        if(item.hasChild("imageURL")) {
            String url = (String) item.child("imageURL").getValue();
            if(!url.isEmpty()) {
                article.setImageURL(url);
            }
        }

        return article;
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
