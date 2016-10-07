package com.example.dayshift_2.traveyy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;

public class Guides extends BaseActivity {

    List<GuidesInformation> guideList;
    RecyclerView recyclerView;
    private RecycleViewGuideAdapter mAdapter;
    private List<ReviewSingleMessage> reviewList;
    private List<GuidesInformation> list;
    private ProgressBar dialog;
    private TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_guides);
        setDrawer(savedInstanceState, "Guides");
        dialog = (ProgressBar) findViewById(R.id.guides_progressbar);
        tv = (TextView) findViewById(R.id.guide_noRecord);

        guideList = new ArrayList<>();
        reviewList = new ArrayList<>();
        list = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        MyTask task = new MyTask();
        task.execute();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecycleViewGuideAdapter(guideList);
        recyclerView.setAdapter(new SlideInLeftAnimationAdapter(mAdapter));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(Guides.this, Detail2.class);
                intent.putExtra("image", guideList.get(position).guidePic);
                intent.putExtra("name", guideList.get(position).guideName);
                intent.putExtra("info", guideList.get(position).guideInfo);
                intent.putExtra("rating", guideList.get(position).guideRating);
                intent.putExtra("licenseNumber", guideList.get(position).guideLicenseNumber);
                intent.putExtra("picUrl", guideList.get(position).email);
                intent.putExtra("who", guideList.get(position).who);

                SharedPreferences prefs = getSharedPreferences("selected", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("age", guideList.get(position).age);
                editor.putString("gender", guideList.get(position).gender);
                editor.putString("qual", guideList.get(position).qualification);
                editor.putInt("tours", guideList.get(position).tours);
                editor.putString("about", guideList.get(position).about);
                editor.putString("picUrl", guideList.get(position).email);
                editor.putString("who", guideList.get(position).who);
                editor.putBoolean("gym", guideList.get(position).gym);
                editor.putBoolean("dine", guideList.get(position).dine);
                editor.putBoolean("paw", guideList.get(position).paw);
                editor.putBoolean("wifi", guideList.get(position).wifi);
                editor.putBoolean("car", guideList.get(position).car);
                editor.putFloat("lat", 24.8615f);
                editor.putFloat("long", 67.0099f);

                Set<String> set = new HashSet<>();
                set.addAll(Arrays.asList(guideList.get(position).picsUrls));
                editor.putStringSet("urls", set);
                Gson gson = new Gson();
                String json = gson.toJson(guideList.get(position).reviews);
                editor.putString("reviews", json);
                editor.apply();

                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void fillData() {
        if (list.size() == 0)
            tv.setVisibility(View.VISIBLE);
        for (int i = 0; i < list.size(); i++)
            mAdapter.add(list.get(i));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();

        return super.onOptionsItemSelected(item);
    }

    public class MyTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            String query = String.format(Locale.getDefault(), "country=%s" + "&" + "city=%s" + "&" + "who=%s",
                    getIntent().getStringExtra("country").replace(" ", "+"),
                    getIntent().getStringExtra("city").replace(" ", "+"), getIntent().getStringExtra("who"));
            return Utils.getRequest("get_records.php", query);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {

                JSONObject object = new JSONObject(s);
                JSONArray array = object.getJSONArray("result");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject c = array.getJSONObject(i);

                    String guideName = c.getString("guideName");
                    String guideInfo = c.getString("guideInfo");
                    String guideStatus = c.getString("guideStatus");
                    String guidePic = c.getString("guidePic");
                    int guideRating = c.getInt("rating");
                    String guideLicenseNumber = c.getString("guideLicenseNumber");
                    int age = c.getInt("age");
                    String gender = c.getString("gender");
                    String qualification = c.getString("qualification");
                    int tours = c.getInt("tours");
                    String about = c.getString("about");
                    String email = c.getString("email");

                    boolean gym = false;
                    if (c.getInt("gym") == 1)
                        gym = true;
                    boolean car = false;
                    if (c.getInt("car") == 1)
                        car = true;
                    boolean dine = false;
                    if (c.getInt("dine") == 1)
                        dine = true;
                    boolean wifi = false;
                    if (c.getInt("wifi") == 1)
                        wifi = true;
                    boolean paw = false;
                    if (c.getInt("paw") == 1)
                        paw = true;

                    String who = c.getString("who");
                    String picUrls = c.getString("picsUrls");
                    String[] urls = picUrls.split("----");

                    for (int z = 0; z < urls.length; z++)
                        urls[z] = urls[z].trim();

                    JSONArray reviews = c.getJSONArray("Review");

                    for (int j = 0; j < reviews.length(); j++) {
                        JSONObject a = reviews.getJSONObject(j);
                        Log.i("Reviews", "Reviews = " + a.toString());
                        String name = a.getString("name");
                        String message = a.getString("message");
                        int ratingForReview = a.getInt("ratingForReview");

                        String imageUri = a.getString("imageUri");
                        String date = a.getString("date");
                        reviewList.add(new ReviewSingleMessage(name, imageUri, message,
                                ratingForReview, date));
                    }
                    list.add(new GuidesInformation(guideName, guideInfo, guidePic, guideRating,
                            guideLicenseNumber, age, gender, qualification, tours, about, email,
                            guideStatus, gym, car, dine, paw, wifi, who, urls, reviewList));
                    reviewList = new ArrayList<>();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.setVisibility(View.GONE);
            fillData();
        }
    }
}
