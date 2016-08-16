package fr.insa.clubinfo.amicale.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.helpers.Date;
import fr.insa.clubinfo.amicale.interfaces.OnImageClickedListener;
import fr.insa.clubinfo.amicale.models.News;
import fr.insa.clubinfo.amicale.views.SwitchImageViewAsyncLayout;

/**
 * Created by Proïd on 06/06/2016.
 */

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private static final int layoutViewArticle = R.layout.adapter_article;

    private News news;
    private final OnImageClickedListener imageClickedListener;

    public NewsAdapter(News news, OnImageClickedListener imageClickedListener) {
        this.news = news;
        this.imageClickedListener = imageClickedListener;
    }

    @Override
    public int getItemViewType(int position) {
        return layoutViewArticle;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Bitmap image = news.getArticle(position).getImage();
        if (image != null) {
            // Image already loaded
            holder.image.showImageView(image);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if(imageClickedListener != null)
                        imageClickedListener.onImageClicked(position);
                }
            });
            holder.image.setVisibility(View.VISIBLE);
        }
        else if(news.getArticle(position).hasImage()) {
            // Image is being loaded
            holder.image.showProgressView();
            holder.image.setVisibility(View.VISIBLE);
        }
        else {
            // No image
            holder.image.setVisibility(View.GONE);
        }

        String content = news.getArticle(position).getContent();
        if(!content.isEmpty()) {
            holder.content.setText(content);
            holder.content.setVisibility(View.VISIBLE);
        } else
            holder.content.setVisibility(View.GONE);

        holder.title.setText(news.getArticle(position).getTitle());
        holder.date.setText(new Date(news.getArticle(position).getDate()).toText());
    }

    @Override
    public int getItemCount() {
        if(news == null)
            return 0;
        else
            return news.getArticlesCount();
    }

    public void update(News news) {
        this.news = news;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView content;
        final TextView title;
        final TextView date;
        final SwitchImageViewAsyncLayout image;

        public ViewHolder(View view) {
            super(view);
            content = (TextView) view.findViewById(R.id.adapter_news_tv_content);
            title = (TextView) view.findViewById(R.id.adapter_article_tv_title);
            date = (TextView) view.findViewById(R.id.adapter_article_tv_date);
            image = (SwitchImageViewAsyncLayout) view.findViewById(R.id.adapter_article_sl_image);
        }
    }
}
