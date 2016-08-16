package fr.insa.clubinfo.amicale.sync;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.interfaces.OnNewsUpdatedListener;
import fr.insa.clubinfo.amicale.models.Article;
import fr.insa.clubinfo.amicale.models.News;

/**
 * Created by Proïd on 05/06/2016.
 */

public class NewsLoader implements ValueEventListener {
    private static int default_loaded_item_count = 10;
    private int load_count = default_loaded_item_count;
    private final OnNewsUpdatedListener listener;
    Query query = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("timestampInverse");
    News currentNews = null;

    public NewsLoader(OnNewsUpdatedListener listener) {
        this.listener = listener;
        query.limitToFirst(load_count);
        query.addValueEventListener(this);
    }

    public void loadMore(int count) {
        query.limitToFirst(load_count+count);
    }

    public void cancel() {
        query.removeEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
        News news = new News();
        for(DataSnapshot item : iterable) {
            if(currentNews != null && currentNews.getArticle(item.getKey()) != null)
                news.addArticle(currentNews.getArticle(item.getKey()));
            else {
                Article article = new Article(item.getKey());
                if (item.hasChild("description"))
                    article.setContent((String) item.child("description").getValue());
                if (item.hasChild("title"))
                    article.setTitle((String) item.child("title").getValue());
                if (item.hasChild("date")) {
                    try {
                        String[] s = ((String) item.child("date").getValue()).split("-");
                        int day = Integer.parseInt(s[0]);
                        int month = Integer.parseInt(s[1]);
                        int year = Integer.parseInt(s[2]);
                        article.setDate(new GregorianCalendar(year, month - 1, day));
                    } catch (Exception e) {

                    }
                }
                if (item.hasChild("imagePresents") && ((Boolean) item.child("imagePresents").getValue())) {
                    article.setHasImage(true);
                    loadImage((String) item.child("imageURL").getValue(), article);
                }
                news.addArticle(article);
            }
        }
        load_count = news.getArticlesCount();
        currentNews = news;
        listener.onNewsLoaded(news);
    }

    private void loadImage(String dataURL, final Article article) {
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
                article.setHasImage(false);
                listener.onImageLoaded(article);
            }
        });
    }


    @Override
    public void onCancelled(DatabaseError databaseError) {
        listener.onNewsSyncCanceled();
    }
}
