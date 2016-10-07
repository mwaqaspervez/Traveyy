package com.example.dayshift_2.traveyy;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class FullImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fullimage);
        Fabric.with(this, new Crashlytics());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        }
        ImageView im = (ImageView) findViewById(R.id.iv_fullimage);
        if (im != null)
            Glide.with(this).load(getIntent().getStringExtra("image"))
                    .asBitmap()
                    .into(im);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();

        return super.onOptionsItemSelected(item);
    }
}
