package fr.insa.clubinfo.amicale.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.insa.clubinfo.amicale.R;
import fr.insa.clubinfo.amicale.helpers.Date;
import fr.insa.clubinfo.amicale.interfaces.OnImageClickedListener;
import fr.insa.clubinfo.amicale.models.Chat;
import fr.insa.clubinfo.amicale.models.ChatMessage;
import fr.insa.clubinfo.amicale.views.SwitchImageViewAsyncLayout;

/**
 * Created by Proïd on 06/06/2016.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    private static final double displayClockDelayThreshold = 20 * 60; // 20 minutes

    private static final int layoutViewOther = R.layout.adapter_message_chat_other;
    private static final int layoutViewSelf = R.layout.adapter_message_chat_self;
    private static final int layoutLoading = R.layout.adapter_loading;

    private final Chat chat;
    private final OnImageClickedListener imageClickedListener;
    private boolean showLoadingView;

    public ChatMessageAdapter(Chat chat, OnImageClickedListener imageClickedListener) {
        this.chat = chat;
        this.imageClickedListener = imageClickedListener;
    }

    @Override
    public int getItemViewType(int position) {
        if(showLoadingView && position == chat.getMessagesCount()) {
            return layoutLoading;
        }
        if(chat.getMessage(position).isOwn())
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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(getItemViewType(position) != layoutLoading) {
            ChatMessage msg = chat.getMessage(position);
            Bitmap image = msg.getImage();
            // Image
            if (msg.hasImage()) {
                if (image != null)
                    holder.switchImgAsync.showImageView(image);
                else
                    holder.switchImgAsync.showProgressView();
                holder.switchImgAsync.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imageClickedListener != null)
                            imageClickedListener.onImageClicked(holder.getAdapterPosition());
                    }
                });
                holder.imageCard.setVisibility(View.VISIBLE);
            } else {
                holder.imageCard.setVisibility(View.GONE);
            }

            // Text
            String content = chat.getMessage(position).getContent();
            if (!content.isEmpty()) {
                holder.textContent.setText(content);
                holder.textCard.setVisibility(View.VISIBLE);
            } else {
                holder.textCard.setVisibility(View.GONE);
            }

            // Date
            boolean showDate = shouldDisplayDate(position);
            boolean showClock = shouldDisplayClock(position, showDate);
            String time = "";
            if (showDate)
                time += Date.prettyFormat(chat.getMessage(position).getDate());
            if (showClock && showDate)
                time += "\n";
            if (showClock)
                time += Date.prettyFormatClock(chat.getMessage(position).getDate());
            if (showClock || showDate) {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(time);
            } else
                holder.date.setVisibility(View.GONE);

            // Sender name
            boolean showName = shouldDisplayUserName(position, showDate, showClock);
            if (showName) {
                holder.name.setText(chat.getMessage(position).getSenderName());
                holder.name.setVisibility(View.VISIBLE);
            } else
                holder.name.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        int loadingView = showLoadingView ? 1 : 0;
        if(chat == null)
            return 0 + loadingView;
        else
            return chat.getMessagesCount() + loadingView;
    }

    private double previousTimestamp(int fromPosition) {
        if(fromPosition == 0)
            return 0.0;
        else
            return chat.getMessage(fromPosition-1).getTimestamp();
    }

    private boolean shouldDisplayDate(int position) {
        long daySince1970_previous = (long) previousTimestamp(position) / (3600*24);
        long daySince1970 = (long) chat.getMessage(position).getTimestamp() / (3600*24);

        return daySince1970_previous != daySince1970;
    }

    private boolean shouldDisplayClock(int position, boolean shouldDisplayDate) {
        double delay = chat.getMessage(position).getTimestamp() - previousTimestamp(position);

        return shouldDisplayDate || delay >= displayClockDelayThreshold;
    }

    private boolean shouldDisplayUserName(int fromPosition, boolean shouldDisplayDate, boolean shouldDisplayClock) {
        if(fromPosition == 0)
            return true;
        else
            return !(chat.getMessage(fromPosition-1).getSenderName()+"_"+chat.getMessage(fromPosition-1).getSenderId())
                    .equals(chat.getMessage(fromPosition).getSenderName()+"_"+chat.getMessage(fromPosition).getSenderId())
                    || shouldDisplayDate
                    || shouldDisplayClock;
    }

    public void setShowLoadingView(boolean showLoadingView) {
        this.showLoadingView = showLoadingView;
    }

    public boolean getShowLoadingView() {
        return showLoadingView;
    }

    public int getPosition(int index) {
        return showLoadingView ? index + 1 : index;
    }

    public int getIndex(int position) {
        return showLoadingView ? position - 1 : position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textContent;
        final SwitchImageViewAsyncLayout switchImgAsync;
        final CardView imageCard;
        final CardView textCard;
        final TextView date;
        final TextView name;

        public ViewHolder(View view) {
            super(view);
            textContent = (TextView) view.findViewById(R.id.adapter_message_chat_tv_content);
            switchImgAsync = (SwitchImageViewAsyncLayout) view.findViewById(R.id.adapter_message_chat_sl_picture_async);
            imageCard = (CardView) view.findViewById(R.id.adapter_message_chat_cv_image);
            textCard = (CardView) view.findViewById(R.id.adapter_message_chat_cv_text);
            date = (TextView) view.findViewById(R.id.adapter_message_chat_tv_date);
            name = (TextView) view.findViewById(R.id.adapter_message_chat_tv_name);
        }
    }
}
