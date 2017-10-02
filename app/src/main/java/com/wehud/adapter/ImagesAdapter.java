package com.wehud.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wehud.R;
import com.wehud.model.Image;
import com.wehud.util.Utils;

import java.util.List;

/**
 * This {@link android.support.v7.widget.RecyclerView.Adapter} subclass
 * handles {@link Image} objects.
 *
 * @author Olivier Gonçalves, 2017
 */
public final class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesVH> {

    private List<Image> mImages;

    private View mSelectedView;
    private static int mSelectedPosition = -1;

    public ImagesAdapter(List<Image> images) {
        mImages = images;
    }

    @Override
    public ImagesVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.avatar, parent, false);
        return new ImagesVH(view);
    }

    @Override
    public void onBindViewHolder(ImagesVH holder, int position) {
        final Image image = mImages.get(position);

        final String url = image.getUrl();
        final int resId = image.getResId();

        if (!TextUtils.isEmpty(url)) Utils.loadImage(holder.context, url, holder.image, 256);
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

    /**
     * This {@link android.support.v7.widget.RecyclerView.ViewHolder} subclass
     * holds references to views used in {@link ImagesAdapter}.
     */
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
