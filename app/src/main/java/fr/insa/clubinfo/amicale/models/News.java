package fr.insa.clubinfo.amicale.models;

import java.util.ArrayList;
import java.util.List;

import fr.insa.clubinfo.amicale.interfaces.ImageList;

/**
 * Created by Proïd on 06/06/2016.
 */

public class News implements ImageList {
    private final ArrayList<Article> articles = new ArrayList<>();

    public int getArticlesCount() {
        return articles.size();
    }

    public Article getArticle(int index) {
        return articles.get(index);
    }

    public void addNewArticle(Article article) {
        articles.add(0, article);
    }
    public void addOldArticles(List<Article> list) {
        articles.addAll(list);
    }


    public int getImagePosition(int index) {
        // No image at given index
        if(!getArticle(index).hasImage())
            return -1;

        int position = 0;
        for(int i = 0; i < index; i++)
            if(getArticle(i).hasImage())
                position++;

        return position;
    }

    @Override
    public String getImageURL(int position) {
        int count = 0;
        for(Article m : articles)
            if(m.hasImage())
                if(count++ == position)
                    return m.getImageURL();
        return null;
    }

    @Override
    public int getImageCount() {
        int count = 0;
        for(Article m : articles)
            if(m.hasImage())
                count++;
        return count;
    }

    public int getIndex(String firebaseKey) {
        for(int i = 0; i < getArticlesCount(); i++)
            if(getArticle(i).getFirebaseKey().equals(firebaseKey))
                return i;
        return 0;
    }

    public long getLastTimestampInverse() {
        return articles.get(articles.size()-1).getTimestampInverse();
    }
}
