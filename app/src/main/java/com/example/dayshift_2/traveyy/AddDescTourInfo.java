package com.example.dayshift_2.traveyy;

import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class AddDescTourInfo extends Fragment {
    private static AddDescTourInfo instance = null;
    private LinearLayout layout;

    public static AddDescTourInfo newInstance() {
        return instance == null ? (instance = new AddDescTourInfo()) : instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_desc_tour_info, container, false);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle("Tour Description");

        TextView images = (TextView) root.findViewById(R.id.tour_add_images);
        layout = (LinearLayout) root.findViewById(R.id.add_pic_layout);
        images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);

            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == 100 && resultCode == Activity.RESULT_OK
                    && null != data) {

                ArrayList<Uri> mArrayUri = new ArrayList<>();
                if (data.getData() != null) {

                    Uri mImageUri = data.getData();
                    addBitmap(mImageUri);
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                        }
                        for (int i = 0; i < mArrayUri.size(); i++)
                            addBitmap(mArrayUri.get(i));
                    }
                }
            } else
                Toast.makeText(getActivity(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void addBitmap(Uri uri) {

        layout.setVisibility(View.VISIBLE);
        ImageView view = new ImageView(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setAdjustViewBounds(true);
        int margin = Utils.pixelToDp(2, getActivity());
        layoutParams.setMargins(margin, margin, margin, 0);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(getActivity()).load(uri)
                .override(Utils.pixelToDp(120, getActivity()), Utils.pixelToDp(120, getActivity()))
                .into(view);
        layout.addView(view, layoutParams);
    }
}
