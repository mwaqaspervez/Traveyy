package com.example.dayshift_2.traveyy;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;

import java.util.List;

import io.fabric.sdk.android.Fabric;

public class RecycleViewGuideAdapter extends RecyclerView.Adapter<RecycleViewGuideAdapter.MyViewHolder> {

    private List<GuidesInformation> guide;

    public RecycleViewGuideAdapter(List<GuidesInformation> guideList) {
        this.guide = guideList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_list_item, parent, false);
        Fabric.with(parent.getContext(), new Crashlytics());
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Glide.with(holder.myGuideInfo.getContext()).load(this.guide.get(position).guidePic)
                .asBitmap().placeholder(R.drawable.ic_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.myGuidePic);
        holder.myGuideName.setText(this.guide.get(position).guideName);
        holder.myGuideLicenseNumber.setText(this.guide.get(position).guideLicenseNumber);
        holder.myRatingBar.setNumStars(this.guide.get(position).guideRating);
        servicesToShow(this.guide.get(position).gym, this.guide.get(position).car,
                this.guide.get(position).dine, this.guide.get(position).paw, this.guide.get(position).wifi,
                holder);

        // if (!this.guide.get(position).who.equals("Guide"))
        //      holder.myGuideInfo.setText(String.format(Locale.getDefault(),
        //            "%d %s", this.guide.get(position).age, this.guide.get(position).gender));
        //  else
        holder.myGuideInfo.setText(this.guide.get(position).guideInfo);

        LayerDrawable stars = (LayerDrawable) holder.myRatingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#0091EA"), PorterDuff.Mode.SRC_ATOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            stars.setTint(Color.parseColor("#0091EA"));
    }

    @Override
    public int getItemCount() {
        return guide.size();
    }

    public void add(GuidesInformation movie) {
        guide.add(movie);
        notifyItemInserted(getItemCount() + 1);
    }

    private void servicesToShow(boolean gym, boolean ride, boolean dine, boolean paw, boolean wifi, MyViewHolder holder) {
        if (gym)
            holder.myGym.setVisibility(View.VISIBLE);
        if (ride)
            holder.myRide.setVisibility(View.VISIBLE);
        if (dine)
            holder.myDine.setVisibility(View.VISIBLE);
        if (paw)
            holder.myPaw.setVisibility(View.VISIBLE);
        if (wifi)
            holder.myWifi.setVisibility(View.VISIBLE);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView myGuideName;
        TextView myGuideInfo;
        TextView myGuideLicenseNumber;
        RatingBar myRatingBar;
        ImageView myGuidePic;
        ImageView myGym, myRide, myDine, myPaw, myWifi;

        public MyViewHolder(View view) {
            super(view);
            myGuideName = (TextView) view.findViewById(R.id.guide_name);
            myGuideInfo = (TextView) view.findViewById(R.id.guide_info);
            myGuideLicenseNumber = (TextView) view.findViewById(R.id.guide_license_number);
            myRatingBar = (RatingBar) view.findViewById(R.id.guide_ratingbar);
            myGuidePic = (ImageView) view.findViewById(R.id.circle_image);
            myGym = (ImageView) view.findViewById(R.id.guide_gym);
            myRide = (ImageView) view.findViewById(R.id.guide_ride);
            myDine = (ImageView) view.findViewById(R.id.guide_dine);
            myPaw = (ImageView) view.findViewById(R.id.guide_paw);
            myWifi = (ImageView) view.findViewById(R.id.guide_wifi);
        }
    }
}