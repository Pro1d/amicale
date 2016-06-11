package fr.insa.clubinfo.amicale.interfaces;

import fr.insa.clubinfo.amicale.models.Article;
import fr.insa.clubinfo.amicale.models.News;

/**
 * Created by Proïd on 06/06/2016.
 */

public interface OnNewsUpdatedListener {
    void onNewsLoaded(News news);
    void onNewsSyncFailed();
    void onNewArticleReceived(News news, Article article);
    void onNewsSyncCanceled();
}
