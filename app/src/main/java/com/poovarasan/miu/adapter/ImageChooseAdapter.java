package com.poovarasan.miu.adapter;

import android.content.Context;
import android.support.annotation.ArrayRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.poovarasan.miu.R;

/**
 * Created by poovarasanv on 1/11/16.
 */

public class ImageChooseAdapter extends RecyclerView.Adapter<ImageChooseAdapter.VH> {

    public interface Callback {
        void onItemClicked(int index);

        void onButtonClicked(int index);
    }

    private final CharSequence[] mItems;
    private final int[] mDrawable;
    private Callback mCallback;

    public ImageChooseAdapter(Context context, @ArrayRes int arrayResId, int[] drawabeId) {
        this(context.getResources().getTextArray(arrayResId), drawabeId);
    }

    public ImageChooseAdapter(CharSequence[] mItems, int[] mDrawable) {
        this.mItems = mItems;
        this.mDrawable = mDrawable;
    }

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.setting_menu_item, parent, false);
        return new VH(view, this);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.title.setText(mItems[position]);
        holder.image.setImageResource(mDrawable[position]);
        holder.materialRippleLayout.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mItems.length;
    }

    public static class VH extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView title;
        final MaterialRippleLayout materialRippleLayout;
        final ImageView image;
        final ImageChooseAdapter adapter;

        public VH(View itemView, ImageChooseAdapter adapter) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.menu_text);
            image = (ImageView) itemView.findViewById(R.id.menu_icon);
            materialRippleLayout = (MaterialRippleLayout) itemView.findViewById(R.id.full_menu_item);

            this.adapter = adapter;
            itemView.setOnClickListener(this);
            materialRippleLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (adapter.mCallback == null)
                return;
            if (view instanceof Button) {
                adapter.mCallback.onButtonClicked(getAdapterPosition());
            } else {
                adapter.mCallback.onItemClicked(getAdapterPosition());
            }
        }
    }
}