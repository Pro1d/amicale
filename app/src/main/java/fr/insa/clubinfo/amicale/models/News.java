package fr.insa.clubinfo.amicale.models;

import java.util.ArrayList;

/**
 * Created by Proïd on 06/06/2016.
 */

public class News {
    private final ArrayList<Article> articles = new ArrayList<>();

    public int getArticlesCount() {
        return articles.size();
    }

    public Article getArticle(int index) {
        return articles.get(index);
    }

    public void addArticle(Article article) {
        articles.add(article);
    }
}
