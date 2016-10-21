package com.poovarasan.miu.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.poovarasan.miu.R;

import java.util.List;

/**
 * Created by poovarasanv on 20/10/16.
 */

public class ContactAdapter extends AbstractItem<ContactAdapter, ContactAdapter.ViewHolder> {

    Bitmap image;
    String name, status;

    public ContactAdapter(Bitmap image, String name, String status) {
        this.image = image;
        this.name = name;
        this.status = status;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
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


        TextDrawable drawable2 = TextDrawable.builder()
                .buildRound("A", Color.parseColor("#3d5997"));

        holder.contact_image.setImageDrawable(drawable2);
        holder.contact_number.setText(status);
        holder.contact_name.setText(name);
    }


    protected static class ViewHolder extends RecyclerView.ViewHolder {

        TextView contact_name, contact_number;
        ImageView contact_image;

        public ViewHolder(View itemView) {
            super(itemView);

            this.contact_name = (TextView) itemView.findViewById(R.id.contact_name);
            this.contact_number = (TextView) itemView.findViewById(R.id.contact_number);
            this.contact_image = (ImageView) itemView.findViewById(R.id.contact_image);
        }
    }
}
