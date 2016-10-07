package com.example.dayshift_2.traveyy;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;

import java.util.List;

import io.fabric.sdk.android.Fabric;

class ReviewAdapter extends BaseAdapter {

    private final Context context;
    private List<ReviewSingleMessage> messages;

    public ReviewAdapter(Context context, List<ReviewSingleMessage> messages) {
        this.context = context;
        this.messages = messages;
        Fabric.with(context, new Crashlytics());
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolderItem viewHolderItem;
        viewHolderItem = new ViewHolderItem();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_review, parent,
                    false);

            viewHolderItem.writerName = (TextView) convertView.findViewById(R.id.newReviewerName);
            viewHolderItem.writerMessage = (TextView) convertView.findViewById(R.id.newReviewerMessage);
            viewHolderItem.writerDate = (TextView) convertView.findViewById(R.id.newReviewerDate);
            viewHolderItem.writerRating = (RatingBar) convertView.findViewById(R.id.newReviewerRating);
            viewHolderItem.writeDp = (ImageView) convertView.findViewById(R.id.writeDp);
            convertView.setTag(viewHolderItem);
        } else
            viewHolderItem = (ViewHolderItem) convertView.getTag();


        Glide.with(viewHolderItem.writeDp.getContext()).load(this.messages.get(position).getImageUri())
                .asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.ic_image)
                .into(viewHolderItem.writeDp);

        viewHolderItem.writerMessage.setText(this.messages.get(position).getMessage());
        viewHolderItem.writerName.setText(this.messages.get(position).getName());
        viewHolderItem.writerDate.setText(this.messages.get(position).getDate());
        viewHolderItem.writerRating.setNumStars(this.messages.get(position).getRating());

        LayerDrawable stars = (LayerDrawable) viewHolderItem.writerRating.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#0091EA"), PorterDuff.Mode.SRC_ATOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            stars.setTint(Color.parseColor("#0091EA"));

        return convertView;
    }

    class ViewHolderItem {
        TextView writerName, writerMessage, writerDate;
        ImageView writeDp;
        RatingBar writerRating;
    }
}