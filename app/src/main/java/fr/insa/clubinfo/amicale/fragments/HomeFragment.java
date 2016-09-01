package fr.insa.clubinfo.amicale.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.adapters.NewsAdapter;
import fr.insa.clubinfo.amicale.interfaces.OnImageClickedListener;
import fr.insa.clubinfo.amicale.interfaces.OnNewsUpdatedListener;
import fr.insa.clubinfo.amicale.models.Article;
import fr.insa.clubinfo.amicale.models.News;
import fr.insa.clubinfo.amicale.sync.NewsLoader;
import fr.insa.clubinfo.amicale.views.ImageViewer;

public class HomeFragment extends Fragment implements OnNewsUpdatedListener, OnImageClickedListener {
    private NewsAdapter adapter;
    private NewsLoader loader;
    private News news;
    private boolean loading = true;
    private static final int visibleThreshold = 5, loadMoreCount = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = new NewsLoader(this);
        loader.loadMore(loadMoreCount, 0);
        news = new News();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loader.cancel();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_home);
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        // Recycler view
        adapter = new NewsAdapter(news, this, getActivity());
        adapter.setShowLoadingView(true);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.news_rv_list);
        recyclerView.setAdapter(adapter);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if(!loading) {
                    if(totalItemCount - firstVisibleItem - visibleItemCount < visibleThreshold) {
                        loading = true;
                        loader.loadMore(loadMoreCount, news.getLastTimestampInverse());//visibleItemCount);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onNewsSyncCanceled() {
        loading = false;
    }

    @Override
    public void onNewsLoaded(List<Article> list) {
        // Hide the progress bar if we reach the end
        if(list.size() < loadMoreCount && adapter.getShowLoadingView()) {
            adapter.setShowLoadingView(false);
            adapter.notifyItemRemoved(news.getArticlesCount());
        }

        loading = false;
        int lastCount = news.getArticlesCount();
        // add articles the the news list
        news.addOldArticles(list);
        // insert them in the recycler view
        adapter.notifyItemRangeInserted(lastCount, list.size());
    }

    @Override
    public void onNewArticleReceived(Article article) {
        news.addNewArticle(article);
        adapter.notifyItemInserted(0);
    }

    @Override
    public void onImageClicked(int position) {
        ImageViewer.getImageViewer().show(news, news.getImagePosition(position));
    }

    @Override
    public boolean onBackPressed() {
        ImageViewer imageViewer = ImageViewer.getImageViewer();
        if (imageViewer.isVisible()) {
            imageViewer.hide();
            return true;
        }
        else
            return false;
    }

}
