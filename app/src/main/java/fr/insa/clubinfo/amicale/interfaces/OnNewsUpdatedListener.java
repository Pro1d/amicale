package fr.insa.clubinfo.amicale.interfaces;

import java.util.List;

import fr.insa.clubinfo.amicale.models.Article;
import fr.insa.clubinfo.amicale.models.News;

/**
 * Created by Proïd on 06/06/2016.
 */

public interface OnNewsUpdatedListener {
    void onNewsLoaded(List<Article> list);
    void onNewArticleReceived(Article article);
    void onImageLoaded(Article article);
    void onNewsSyncCanceled();
    /*void onNewsSyncFailed();*/

}
