package com.example.dayshift_2.traveyy;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TabHost;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class TabBar extends AppCompatActivity implements TabHost.OnTabChangeListener {

    /**
     * Called when the activity is first created.
     */
    TabHost tabHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.main_login);
        // *********  Get TabHost Reference ******** //////
        tabHost = (TabHost) findViewById(R.id.tabhost);
        LocalActivityManager mLocalActivityManager = new LocalActivityManager(this, false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);
        tabHost.setup(mLocalActivityManager);

       if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"white\">Login</font>"));
        }

        // Set TabChangeListener called when tab changed


        TabHost.TabSpec spec;
        Intent intent;

        /************* TAB1 ************/
        // Create  Intents to launch an Activity for the tab (to be reused)
        intent = new Intent(this, GuideMainPage.class);
        spec = tabHost.newTabSpec("Customer").setIndicator("Customer").setContent(intent);


        //Add intent to tab
        tabHost.addTab(spec);

        /************* TAB2 ************/
        intent = new Intent().setClass(this, GuideLogin.class);
        spec = tabHost.newTabSpec("Guide").setIndicator("Guide")
                .setContent(intent);
        tabHost.addTab(spec);

        // Set Tab1 as Default tab and change image
        tabHost.getTabWidget().setCurrentTab(0);
    }

    @Override
    public void onTabChanged(String tabId) {

        /************ Called when tab changed *************/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            this.overridePendingTransition(R.anim.bottom_to_top, R.anim.top_to_bottom);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
       super.onBackPressed();
        this.overridePendingTransition(R.anim.bottom_to_top, R.anim.top_to_bottom);
    }


}