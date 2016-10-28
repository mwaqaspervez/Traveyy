package com.example.dayshift_2.traveyy;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import io.fabric.sdk.android.Fabric;

public class PageFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PAGE_NUMBER = "page_number";
    RatingBar ratingBar;
    ReviewAdapter adapter;
    private ImageView mainView, image1, image2, image3, image4, image5;
    private LinearLayout writeAReviewLayout;
    private ScrollView scrollView;
    private CardView cardView;
    private ScrollView scroller;
    private EditText edWriterMessage;
    private List<ReviewSingleMessage> reviewSingleMessages;
    private SharedPreferences prefs;

    public PageFragment() {
    }

    public static PageFragment newInstance(int page) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, page);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int adc = getArguments().getInt(ARG_PAGE_NUMBER, -1);
        Fabric.with(getContext(), new Crashlytics());
        prefs = getActivity().getSharedPreferences("selected", Context.MODE_PRIVATE);
        switch (adc) {
            case 1:
                // %********************* Tab Fragment of Detail ************ //
                View rootView = inflater.inflate(R.layout.layout_detail_info, container, false);
                RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.about_layout_detail);
                layout.setOnClickListener(this);

                TextView age = (TextView) rootView.findViewById(R.id.summary_age);
                TextView education = (TextView) rootView.findViewById(R.id.summary_qual);
                TextView tour = (TextView) rootView.findViewById(R.id.summary_tour);
                TextView gender = (TextView) rootView.findViewById(R.id.summary_gender);
                TextView about = (TextView) rootView.findViewById(R.id.summary_about);

                cardView = (CardView) rootView.findViewById(R.id.card_view);
                scroller = (ScrollView) rootView.findViewById(R.id.layout_info);

                age.setText(String.valueOf(prefs.getInt("age", 0)));
                education.setText(prefs.getString("qual", "School"));
                tour.setText(String.valueOf(prefs.getInt("tours", 2)));
                gender.setText(prefs.getString("gender", "MALE"));
                about.setText(prefs.getString("about", "NO INFORMATION SHARED"));

                return rootView;

            case 2:
                // %********************* Tab Fragment of Pictures ************ //
                View rootView2 = inflater.inflate(R.layout.layout_detail_pictures, container, false);

                image1 = (ImageView) rootView2.findViewById(R.id.image11);
                image2 = (ImageView) rootView2.findViewById(R.id.image12);
                image3 = (ImageView) rootView2.findViewById(R.id.image13);
                image4 = (ImageView) rootView2.findViewById(R.id.image14);
                image5 = (ImageView) rootView2.findViewById(R.id.image15);
                mainView = (ImageView) rootView2.findViewById(R.id.iv_main_view);
                mainView.setImageResource(R.drawable.ic_placeholder);

                Set<String> set = prefs.getStringSet("urls", null);
                assert set != null : "No Pictures Added";
                String[] array = set.toArray(new String[set.size()]);
                for (int i = 0; i < array.length; i++) {
                    switch (i) {
                        case 0:
                            Glide.with(getContext()).load(array[0])
                                    .asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .placeholder(R.drawable.ic_placeholder)
                                    .into(image1);
                        case 1:
                            Glide.with(getContext()).load(array[1]).asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .placeholder(R.drawable.ic_placeholder)
                                    .into(image2);
                        case 2:
                            Glide.with(getContext()).load(array[2]).asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .placeholder(R.drawable.ic_placeholder)
                                    .into(image3);
                        case 3:
                            Glide.with(getContext()).load(array[3]).asBitmap()
                                    .placeholder(R.drawable.ic_placeholder)
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .into(image4);
                        case 4:
                            Glide.with(getContext()).load(array[4]).asBitmap()
                                    .placeholder(R.drawable.ic_placeholder)
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .into(image5);
                    }
                }
                Glide.with(getContext()).load(array[new Random().nextInt(array.length)])
                        .asBitmap().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(mainView);
                image1.setOnClickListener(this);
                image2.setOnClickListener(this);
                image3.setOnClickListener(this);
                image4.setOnClickListener(this);
                image5.setOnClickListener(this);

                return rootView2;

            case 3:
                // %********************* Tab Fragment of Review ************ //
                View rootView1 = inflater.inflate(R.layout.layout_detail_review, container, false);

                // %********************* Setting up Views and List Adapter for the review secton ************ //
                Button writeAReview = (Button) rootView1.findViewById(R.id.writeAReviewButton);
                writeAReviewLayout = (LinearLayout) rootView1.findViewById(R.id.writeareview_layout);
                scrollView = (ScrollView) rootView1.findViewById(R.id.scrollView_detail_review);
                Button writeAReviewButton = (Button) rootView1.findViewById(R.id.addNewReview);
                ratingBar = (RatingBar) rootView1.findViewById(R.id.addNewRating);
                edWriterMessage = (EditText) rootView1.findViewById(R.id.edWriterMessage);
                ListView reviewsList = (ListView) rootView1.findViewById(R.id.reviewListView);

                Gson gson = new Gson();
                String json = prefs.getString("reviews", null);
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                ReviewSingleMessage[] reviews = gson.fromJson(json, ReviewSingleMessage[].class);

                reviewSingleMessages = new ArrayList<>();
                Collections.addAll(reviewSingleMessages, reviews);

                adapter = new ReviewAdapter(getContext(), reviewSingleMessages);
                reviewsList.setAdapter(adapter);

                // ********************* Change the Color Of the Stars To BLUE ************ //
                LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(Color.parseColor("#0091EA"), PorterDuff.Mode.SRC_ATOP);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    stars.setTint(Color.parseColor("#d3d3d3"));

                writeAReviewButton.setOnClickListener(this);
                writeAReview.setOnClickListener(this);

                return rootView1;
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        switch (v.getId()) {

            // ************** On Image click change the background of the main ImageView **************//
            case R.id.image11:
                mainView.setImageDrawable(image1.getDrawable());
                break;
            case R.id.image12:
                mainView.setImageDrawable(image2.getDrawable());
                break;
            case R.id.image13:
                mainView.setImageDrawable(image3.getDrawable());
                break;
            case R.id.image14:
                mainView.setImageDrawable(image4.getDrawable());
                break;
            case R.id.image15:
                mainView.setImageDrawable(image5.getDrawable());
                break;

            case R.id.about_layout_detail:

                // ************** Check Visibility **************//
                if (cardView.getVisibility() == View.GONE) {
                    cardView.setVisibility(View.VISIBLE);
                    sendSummaryScroll();
                } else
                    cardView.setVisibility(View.GONE);
                break;

            // **************  On Click Of Write A Review button **************//
            case R.id.writeAReviewButton:

                // ************** Check Visibility And Start Animation ************//
                // ************** Check If User is logged in ************** //
                if (auth.getCurrentUser() != null) {
                    if (writeAReviewLayout.getVisibility() == View.GONE) {
                        writeAReviewLayout.setVisibility(View.VISIBLE);
                        sendScroll();
                    } else
                        writeAReviewLayout.setVisibility(View.GONE);
                } else
                    makeDialogForLogin();
                break;

            case R.id.addNewReview:
                if (!edWriterMessage.getText().toString().equals("")) {

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                    String formattedDate = df.format(c.getTime());

                    //noinspection ConstantConditions
                    reviewSingleMessages.add(new ReviewSingleMessage(auth.getCurrentUser().getDisplayName(), auth.getCurrentUser().getPhotoUrl().toString(),
                            edWriterMessage.getText().toString(), (int) ratingBar.getRating(), formattedDate));
                    writeAReviewLayout.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    myTask task = new myTask();
                    task.execute(auth.getCurrentUser().getEmail(),
                            auth.getCurrentUser().getDisplayName(),
                            edWriterMessage.getText().toString(),
                            String.valueOf((int) ratingBar.getRating()),
                            auth.getCurrentUser().getPhotoUrl().toString(),
                            formattedDate,
                            prefs.getString("picUrl", "NoUrl"));
                    Log.i("Current User = ", auth.getCurrentUser().getPhotoUrl().toString());
                    sendScroll();
                }
                break;
        }
    }

    // *********** Scrolls to the bottom of the screen when users presses WRITE A REVIEW button ****** //
    private void sendScroll() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);

                    }
                });
            }
        }).start();
    }

    // ******** Scroll to the bottom of the screen when users presses about button ********* //
    private void sendSummaryScroll() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        scroller.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }).start();
    }

    // ******** Make Dialog For Login ***************** //
    private void makeDialogForLogin() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setCancelable(true)
                .setTitle("Authentication Failed")
                .setMessage("You need to login first")
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(getContext(), TabBar.class);
                        intent.putExtra("class", "DetailInformation");
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_top, R.anim.slide_in_bottomm);
                    }
                });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public class myTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String p = null;
            try {
                p = String.format(Locale.getDefault(), "email=%s&" +
                                "name=%s&" +
                                "message=%s&" +
                                "rating=%d&" +
                                "imageUri=%s&" +
                                "date=%s&" +
                                "reviewFor=%s", params[0], params[1], params[2],
                        Integer.getInteger(params[3]), URLEncoder.encode(params[4], "UTF-8"), params[5], params[6]);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return Utils.postRequest("insert_review.php", p);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.toLowerCase().contains("Added".toLowerCase()))
                Toast.makeText(getContext(), Html.fromHtml(s), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), Html.fromHtml(s), Toast.LENGTH_LONG).show();
        }
    }
}