package fr.insa.clubinfo.amicale.models;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

import fr.insa.clubinfo.amicale.interfaces.ImageList;

/**
 * Created by Proïd on 06/06/2016.
 */

public class News implements ImageList {
    private final ArrayList<Article> articles = new ArrayList<>();
    private final HashMap<String, Article> hashMap = new HashMap<>();

    public int getArticlesCount() {
        return articles.size();
    }

    public Article getArticle(int index) {
        return articles.get(index);
    }

    public Article getArticle(String key) {
        return hashMap.get(key);
    }

    public void addArticle(Article article) {
        articles.add(article);
        hashMap.put(article.getFirebaseKey(), article);
    }


    public int getImagePosition(int index) {
        // No image at given index
        if(getImage(index) == null)
            return -1;

        int position = 0;
        for(int i = 0; i < index; i++)
            if(getArticle(index).getImage() != null)
                position++;

        return position;
    }

    @Override
    public Bitmap getImage(int position) {
        int count = 0;
        for(Article m : articles)
            if(m.getImage() != null)
                if(count++ == position)
                    return m.getImage();
        return null;
    }

    @Override
    public int getCount() {
        int count = 0;
        for(Article m : articles)
            if(m.getImage() != null)
                count++;
        return count;
    }

    public int getIndex(String firebaseKey) {
        for(int i = 0; i < getArticlesCount(); i++)
            if(getArticle(i).getFirebaseKey().equals(firebaseKey))
                return i;
        return 0;
    }
}
