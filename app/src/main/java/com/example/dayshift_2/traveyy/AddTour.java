package com.example.dayshift_2.traveyy;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;

import java.util.HashMap;

import io.fabric.sdk.android.Fabric;

public class AddTour extends AppCompatActivity {

    Button button;
    Fragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_tour);
        Fabric.with(this, new Crashlytics());
        newFragment = AddDescTourInfo.newInstance();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        button = (Button) findViewById(R.id.add_new_fragment);
        button.setText("Add Description");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.getText().toString().equals("Add Description"))
                    addFragmentToStack();
                else {
                    try {
                        HashMap<String, String> map = new HashMap<>();

                        Utils.postRequest("insert_tour.php", Utils.getPostDataString(map));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }


            }
        });

        if (savedInstanceState == null) {
            newFragment = AddBasicTourInfo.newInstance();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.tour_fragment, newFragment, "Basic").commit();
        }
    }

    void addFragmentToStack() {

        AddBasicTourInfo frag = (AddBasicTourInfo)
                getFragmentManager().findFragmentByTag("Basic");

        if (frag != null && frag.isVisible()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            newFragment = AddDescTourInfo.newInstance();
            ft.setCustomAnimations(R.animator.fragment_slide_left_enter,
                    R.animator.fragment_slide_left_exit,
                    R.animator.fragment_slide_right_enter,
                    R.animator.fragment_slide_right_exit);
            ft.replace(R.id.tour_fragment, newFragment, "Desc");
            ft.addToBackStack(null);
            ft.commit();
            button.setText("Post");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
