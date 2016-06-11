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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = new NewsLoader(this);
        loader.loadAsync();
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onNewsLoaded(News news) {
        this.news = news;
        adapter.update(news);
    }

    @Override
    public void onNewsSyncFailed() {
        adapter.update(news);
        Toast.makeText(getActivity(), R.string.loading_error_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNewArticleReceived(News news, Article article) {
        this.news = news;
        adapter.update(news);
        // TODO update last item only
    }

    @Override
    public void onNewsSyncCanceled() {

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
