package com.poovarasan.miu.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.poovarasan.miu.R;
import com.poovarasan.miu.widget.CircleImageView;

import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by poovarasanv on 20/10/16.
 */

public class ContactAdapter extends AbstractItem<ContactAdapter, ContactAdapter.ViewHolder> {

    Drawable image;
    String name, status;

    public ContactAdapter(Drawable image, String name, String status) {
        this.image = image;
        this.name = name;
        this.status = status;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int getType() {
        return R.id.contact_adapter;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.contact_item;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);


        holder.contact_image.setImageDrawable(image);
        //holder.isOnline.setVisibility(View.GONE);

        if (status.length() > 20)
            status = status.substring(0, 20) + "...";


        holder.contact_number.setText(status);
        holder.contact_name.setText(name);
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {

        TextView contact_name;
        EmojiconTextView contact_number;
        CircleImageView contact_image;
        ImageView isOnline;

        public ViewHolder(View itemView) {
            super(itemView);

            this.contact_name = (TextView) itemView.findViewById(R.id.contact_name);
            this.contact_number = (EmojiconTextView) itemView.findViewById(R.id.contact_number);
            this.contact_image = (CircleImageView) itemView.findViewById(R.id.contact_image);
            this.isOnline = (ImageView) itemView.findViewById(R.id.isOnline);
        }
    }
}
