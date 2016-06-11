package fr.insa.clubinfo.amicale.sync;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import java.util.GregorianCalendar;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.interfaces.OnNewsUpdatedListener;
import fr.insa.clubinfo.amicale.models.Article;
import fr.insa.clubinfo.amicale.models.News;

/**
 * Created by Proïd on 05/06/2016.
 */

public class NewsLoader {
    private final OnNewsUpdatedListener listener;
    private AsyncTask<Void, Void, News> currentTask;

    public NewsLoader(OnNewsUpdatedListener listener) {
        this.listener = listener;
    }

    public void loadAsync() {
        currentTask = new AsyncTask<Void, Void, News>() {
            @Override
            protected News doInBackground(Void... params) {
                News n = new News();
                for(int i = 0; i < 5; i++) {
                    Article a = new Article();
                    a.setTitle("Article n°"+i);
                    a.setContent("L'amicale c'est bien, toussa toussa. " +
                            "L'application android est trop plus mieux que " +
                            "celle des aïe machins alors voilà quoi.");
                    a.setDate(new GregorianCalendar());
                    if(Math.random() > 0.5) {
                        Bitmap bmp = BitmapFactory.decodeResource(((Fragment) listener).getResources(), R.drawable.logo_amicale_icon);
                        a.setImage(bmp);
                    }
                    n.addArticle(a);
                }
                return n;
            }

            @Override
            protected void onPostExecute(News news) {
                listener.onNewsLoaded(news);
            }

            @Override
            protected void onCancelled(News news) {
                listener.onNewsSyncCanceled();
            }
        }.execute();
    }

    public void cancel() {
        currentTask.cancel(true);
    }
}
