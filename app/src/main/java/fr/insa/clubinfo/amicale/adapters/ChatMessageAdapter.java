package fr.insa.clubinfo.amicale.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.interfaces.OnImageClickedListener;
import fr.insa.clubinfo.amicale.models.Chat;
import fr.insa.clubinfo.amicale.models.ChatMessage;
import fr.insa.clubinfo.amicale.views.SwitchImageViewAsyncLayout;

/**
 * Created by Proïd on 06/06/2016.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    private static final int layoutViewOther = R.layout.adapter_message_chat_other;
    private static final int layoutViewSelf = R.layout.adapter_message_chat_self;

    private Chat chat;
    private final OnImageClickedListener imageClickedListener;

    public ChatMessageAdapter(Chat chat, OnImageClickedListener imageClickedListener) {
        this.chat = chat;
        this.imageClickedListener = imageClickedListener;
    }

    @Override
    public int getItemViewType(int position) {
        if(chat.getMessage(position).isSelf())
            return layoutViewSelf;
        else
            return layoutViewOther;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ChatMessage msg = chat.getMessage(position);
        Bitmap image = msg.getImage();
        if(msg.hasImage()) {
            if(image != null)
                holder.switchImgAsync.showImageView(image);
            else
                // TODO load image ?
                holder.switchImgAsync.showProgressView();
            holder.switchImgAsync.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if(imageClickedListener != null)
                        imageClickedListener.onImageClicked(position);
                }
            });
            holder.imageCard.setVisibility(View.VISIBLE);
        }
        else {
            holder.imageCard.setVisibility(View.GONE);
        }

        String content = chat.getMessage(position).getContent();
        if(!content.isEmpty()) {
            holder.textContent.setText(content);
            holder.textCard.setVisibility(View.VISIBLE);
        } else {
            holder.textCard.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if(chat == null)
            return 0;
        else
            return chat.getMessagesCount();
    }

    public void update(Chat chat) {
        this.chat = chat;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textContent;
        final SwitchImageViewAsyncLayout switchImgAsync;
        final CardView imageCard;
        final CardView textCard;

        public ViewHolder(View view) {
            super(view);
            textContent = (TextView) view.findViewById(R.id.adapter_message_chat_tv_content);
            switchImgAsync = (SwitchImageViewAsyncLayout) view.findViewById(R.id.adapter_message_chat_sl_picture_async);
            imageCard = (CardView) view.findViewById(R.id.adapter_message_chat_cv_image);
            textCard = (CardView) view.findViewById(R.id.adapter_message_chat_cv_text);
        }
    }
}
