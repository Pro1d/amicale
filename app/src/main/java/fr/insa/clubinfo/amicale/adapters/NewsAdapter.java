package fr.insa.clubinfo.amicale.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.helpers.Date;
import fr.insa.clubinfo.amicale.interfaces.OnImageClickedListener;
import fr.insa.clubinfo.amicale.models.Article;
import fr.insa.clubinfo.amicale.models.News;

/**
 * Created by Proïd on 06/06/2016.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private static final int layoutViewArticle = R.layout.adapter_article;
    private static final int layoutViewLoading = R.layout.adapter_loading;

    private News news;
    private boolean showLoadingView = false;
    private final OnImageClickedListener imageClickedListener;
    private Context context;

    public NewsAdapter(News news, OnImageClickedListener imageClickedListener, Context context) {
        this.news = news;
        this.imageClickedListener = imageClickedListener;
        this.context = context;
    }

    public void setShowLoadingView(boolean showLoadingView) {
        this.showLoadingView = showLoadingView;
    }

    public boolean getShowLoadingView() {
        return showLoadingView;
    }

    @Override
    public int getItemViewType(int position) {
        if(showLoadingView && position == news.getArticlesCount())
            return layoutViewLoading;

        return layoutViewArticle;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(getItemViewType(position) == layoutViewArticle) {
            Article article  = news.getArticle(position);
            if (article.hasImage()) {
                // Get the image
                holder.image.setVisibility(View.VISIBLE);
                Glide.with(context).load(article.getImageURL()).into(holder.image);
            } else {
                // No image
                holder.image.setVisibility(View.GONE);
                holder.image.setImageDrawable(null);
            }

            String content = article.getContent();
            if (!content.isEmpty()) {
                holder.content.setText(content);
                holder.content.setVisibility(View.VISIBLE);
            }
            else {
                holder.content.setText("");
                holder.content.setVisibility(View.GONE);
            }

            holder.title.setText(article.getTitle());
            holder.date.setText(new Date(article.getDate()).toText());
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        // free image
        /*if(holder.image != null) {
            holder.image.setImageDrawable(null);
        }*/
    }

    @Override
    public int getItemCount() {
        int loadingView = showLoadingView ? 1 : 0;
        if(news == null)
            return loadingView;
        else
            return news.getArticlesCount()+loadingView;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView content;
        final TextView title;
        final TextView date;
        final ImageView image;

        public ViewHolder(View view) {
            super(view);
            content = (TextView) view.findViewById(R.id.adapter_news_tv_content);
            title = (TextView) view.findViewById(R.id.adapter_article_tv_title);
            date = (TextView) view.findViewById(R.id.adapter_article_tv_date);
            image = (ImageView) view.findViewById(R.id.adapter_article_iv_image);

            if(image != null) {
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imageClickedListener != null)
                            imageClickedListener.onImageClicked(ViewHolder.this.getAdapterPosition());
                    }
                });
            }
        }
    }
}
