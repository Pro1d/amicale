package fr.insa.clubinfo.amicale.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
        if(showLoadingView && position == 0) {
            return layoutLoading;
        }
        if(chat.getMessage(getIndex(position)).isOwn())
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
        final int index = getIndex(position);
        if(getItemViewType(position) != layoutLoading) {
            ChatMessage msg = chat.getMessage(index);
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
                            imageClickedListener.onImageClicked(getIndex(holder.getAdapterPosition()));
                    }
                });
                holder.imageCard.setVisibility(View.VISIBLE);
            } else {
                holder.switchImgAsync.hideAll();
                holder.imageCard.setVisibility(View.GONE);
            }

            // Text
            String content = chat.getMessage(index).getContent();
            if (!content.isEmpty()) {
                holder.textContent.setText(content);
                holder.textCard.setVisibility(View.VISIBLE);
            } else {
                holder.textCard.setVisibility(View.GONE);
            }

            // Date
            boolean showDate = shouldDisplayDate(index);
            boolean showClock = shouldDisplayClock(index, showDate);
            String time = "";
            if (showDate)
                time += Date.prettyFormat(chat.getMessage(index).getDate());
            if (showClock && showDate)
                time += "\n";
            if (showClock)
                time += Date.prettyFormatClock(chat.getMessage(index).getDate());
            if (showClock || showDate) {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(time);
            } else
                holder.date.setVisibility(View.GONE);

            // Sender name
            boolean showName = shouldDisplayUserName(index, showDate, showClock);
            if (showName) {
                holder.name.setText(chat.getMessage(index).getSenderName());
                holder.name.setVisibility(View.VISIBLE);
            } else
                holder.name.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        // free image
        if(holder.switchImgAsync != null) {
            holder.switchImgAsync.hideAll();
        }
    }

    @Override
    public int getItemCount() {
        int loadingView = showLoadingView ? 1 : 0;
        if(chat == null)
            return loadingView;
        else
            return chat.getMessagesCount() + loadingView;
    }

    private double previousTimestamp(int fromIndex) {
        if(fromIndex == 0)
            return 0.0;
        else
            return chat.getMessage(fromIndex-1).getTimestamp();
    }

    private boolean shouldDisplayDate(int index) {
        long daySince1970_previous = (long) previousTimestamp(index) / (3600*24);
        long daySince1970 = (long) chat.getMessage(index).getTimestamp() / (3600*24);

        return daySince1970_previous != daySince1970;
    }

    private boolean shouldDisplayClock(int index, boolean shouldDisplayDate) {
        double delay = chat.getMessage(index).getTimestamp() - previousTimestamp(index);

        return shouldDisplayDate || delay >= displayClockDelayThreshold;
    }

    private boolean shouldDisplayUserName(int fromIndex, boolean shouldDisplayDate, boolean shouldDisplayClock) {
        if(fromIndex == 0)
            return true;
        else
            return !(chat.getMessage(fromIndex-1).getSenderName()+"_"+chat.getMessage(fromIndex-1).getSenderId())
                    .equals(chat.getMessage(fromIndex).getSenderName()+"_"+chat.getMessage(fromIndex).getSenderId())
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

    private int getIndex(int position) {
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
