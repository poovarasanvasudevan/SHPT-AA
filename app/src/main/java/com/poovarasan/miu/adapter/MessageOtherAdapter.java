package com.poovarasan.miu.adapter;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.poovarasan.miu.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by poovarasanv on 7/11/16.
 */

public class MessageOtherAdapter extends AbstractItem<MessageOtherAdapter, MessageOtherAdapter.VH> {

    String message;
    long date;

    public MessageOtherAdapter(String message, long date) {
        this.message = message;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public int getType() {
        return R.id.message_other_adapter;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.message_other;
    }

    @Override
    public void bindView(final VH holder, List payloads) {
        super.bindView(holder, payloads);

        holder.message.setText(message);
        String dateString = new SimpleDateFormat("MM/dd/yyyy-mm:ss").format(new Date(date));
        holder.time.setText(dateString);

    }

    protected static class VH extends RecyclerView.ViewHolder {

        EmojiconTextView message;
        AppCompatTextView time;

        public VH(View itemView) {
            super(itemView);

            message = (EmojiconTextView) itemView.findViewById(R.id.message_scout_text_text_view_text);
            time = (AppCompatTextView) itemView.findViewById(R.id.message_scout_text_text_view_timestamp);
        }
    }
}
