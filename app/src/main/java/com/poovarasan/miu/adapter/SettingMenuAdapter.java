package com.poovarasan.miu.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.poovarasan.miu.R;

import java.util.List;

/**
 * Created by poovarasanv on 24/10/16.
 */

public class SettingMenuAdapter extends AbstractItem<SettingMenuAdapter, SettingMenuAdapter.ViewHolder> {

    Drawable icon;
    String title;
    Activity activity;
    Class aClass;

    public SettingMenuAdapter(Drawable icon, String title, Activity activity, Class aClass) {
        this.icon = icon;
        this.title = title;
        this.activity = activity;
        this.aClass = aClass;
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

        holder.full_menu_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, aClass);
                activity.startActivity(i);
            }
        });
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        TextView menu_name;
        ImageView menu_image;
        MaterialRippleLayout full_menu_item;

        public ViewHolder(View itemView) {
            super(itemView);

            this.menu_name = (TextView) itemView.findViewById(R.id.menu_text);
            this.menu_image = (ImageView) itemView.findViewById(R.id.menu_icon);
            this.full_menu_item = (MaterialRippleLayout) itemView.findViewById(R.id.full_menu_item);

        }
    }
}
// Intent i = new Intent(Settings.this, DataUsage.class);
//startActivity(i);