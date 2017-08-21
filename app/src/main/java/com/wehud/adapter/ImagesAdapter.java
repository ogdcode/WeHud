package com.wehud.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.wehud.R;
import com.wehud.model.Image;

import java.util.List;

public final class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesVH> {

    private List<Image> mImages;

    private View mSelectedView;
    private static int mSelectedPosition = -1;

    private int mViewResourceId;

    public ImagesAdapter(List<Image> images) {
        mImages = images;
    }

    @Override
    public ImagesVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.avatar, parent, false);
        return new ImagesVH(view);
    }

    @Override
    public void onBindViewHolder(ImagesVH holder, int position) {
        Image image = mImages.get(position);

        String url = image.getUrl();
        int resId = image.getResId();

        if (!TextUtils.isEmpty(url))
            Picasso.with(holder.context).load(url).resize(256, 256).into(holder.image);
        else holder.image.setImageResource(resId);
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    @Override
    public long getItemId(int position) {
        if (position == -1) return mSelectedPosition;
        return super.getItemId(position);
    }

    class ImagesVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private ImageView image;

        ImagesVH(View view) {
            super(view);
            view.setClickable(true);
            view.setOnClickListener(this);
            this.context = view.getContext();
            this.image = (ImageView) view.findViewById(R.id.image);
            this.image.setBackgroundResource(R.drawable.list_item_selector);
        }

        @Override
        public void onClick(View view) {
            if (!view.isSelected()) {
                if (mSelectedView != null)
                    mSelectedView.setSelected(false);

                mSelectedPosition = getAdapterPosition();
                mSelectedView = view;
            } else {
                mSelectedPosition = -1;
                mSelectedView = null;
            }

            view.setSelected(!view.isSelected());
        }
    }
}
