package com.poovarasan.miu.adapter;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.poovarasan.miu.R;

import java.util.List;

/**
 * Created by poovarasanv on 24/10/16.
 */

public class SettingMenuAdapter extends AbstractItem<SettingMenuAdapter, SettingMenuAdapter.ViewHolder> {

    Drawable icon;
    String title;

    public SettingMenuAdapter(Drawable icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getType() {
        return R.id.menu_adapter;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.setting_menu_item;
    }


    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.menu_image.setImageDrawable(icon);
        holder.menu_name.setText(title);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        TextView menu_name;
        ImageView menu_image;

        public ViewHolder(View itemView) {
            super(itemView);

            this.menu_name = (TextView) itemView.findViewById(R.id.menu_text);
            this.menu_image = (ImageView) itemView.findViewById(R.id.menu_icon);
        }
    }
}
