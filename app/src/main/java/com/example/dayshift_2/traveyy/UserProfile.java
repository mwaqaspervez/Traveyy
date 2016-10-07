package com.example.dayshift_2.traveyy;


import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {

    private TextView first_name, last_name, phone, country, email;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_sample_collapsing_toolbar);
        first_name = (TextView) findViewById(R.id.user_profile_first_name);
        last_name = (TextView) findViewById(R.id.user_profile_last_name);
        phone = (TextView) findViewById(R.id.user_profile_phone);
        country = (TextView) findViewById(R.id.user_profile_country);
        email = (TextView) findViewById(R.id.user_profile_email);

        auth = FirebaseAuth.getInstance();

        MyTask task = new MyTask();
        task.execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (collapsingToolbarLayout != null) {
            if (auth.getCurrentUser() != null)
                collapsingToolbarLayout.setTitle(auth.getCurrentUser().getDisplayName());
            else
                collapsingToolbarLayout.setTitle("Traveyy");
        }

        fillFab();
        loadBackdrop();
    }

    private void setUserCredentials(String firstName, String lastName, String emailAddress,
                                    String phoneNumber, String countryName) {

        first_name.setText(firstName);
        last_name.setText(lastName);
        email.setText(emailAddress);
        phone.setText(phoneNumber);
        country.setText(countryName);
        ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        if (imageView != null)
            
            Glide.with(this).load(auth.getCurrentUser().getPhotoUrl()).override(320, 480).fitCenter().into(imageView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        if (imageView != null)
            imageView.setImageResource(R.drawable.splash);
    }

    private void fillFab() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        if (fab != null) {
            fab.setImageDrawable(new IconicsDrawable(this, FontAwesome.Icon.faw_pencil).actionBar().color(Color.WHITE));
            fab.setOnClickListener(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_action_button:
                startActivity(new Intent(this, EditUserProfile.class));
                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                break;
        }
    }

    public class MyTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String q = null;
            if (auth.getCurrentUser() != null)
                q = String.format(Locale.getDefault(),
                        "email=%s", auth.getCurrentUser().getEmail());

            return Utils.getRequest("get_userdata.php", q);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            try {
                JSONObject object = new JSONObject(s);
                JSONArray array = object.getJSONArray("result");
                JSONObject myObject = array.getJSONObject(0);

                String zz = "-";
                String[] x = myObject.getString("name").split(" ");
                if (x.length > 1)
                    zz = x[1];

                setUserCredentials(x[0], zz, myObject.getString("email"),
                        myObject.getString("phoneNumber"), myObject.getString("country"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
