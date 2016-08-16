package fr.insa.clubinfo.amicale.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
    private static final int visibleThreshold = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = new NewsLoader(this);
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
        adapter = new NewsAdapter(news, this);
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
                        loader.loadMore(visibleItemCount+visibleThreshold);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onNewsLoaded(News news) {
        this.news = news;
        loading = false;
        adapter.update(news);
    }
    /*
    @Override
    public void onNewsSyncFailed() {
        loading = false;
        adapter.update(news);
        Toast.makeText(getActivity(), R.string.loading_error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNewArticleReceived(News news, Article article) {
        loading = false;
        this.news = news;
        adapter.update(news);
        // TODO update last item only
    }*/

    @Override
    public void onNewsSyncCanceled() {
        loading = false;
    }

    @Override
    public void onImageLoaded(Article article) {
        if(news != null) {
            int index = news.getIndex(article.getFirebaseKey());
            if(index >= 0) {
                adapter.notifyItemChanged(index);
            }
        }
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
