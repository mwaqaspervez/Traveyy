package com.example.dayshift_2.traveyy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;

public class Detail2 extends BaseActivity implements View.OnClickListener {

    ImageView im;
    TextView email, name, license;
    Button bttChat, bttMap;
    RatingBar ratingBar;

    // ** On Create. Initialize and setDrawer ** //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_details2);
        setDrawer(savedInstanceState, "Profile");
        initialize();
    }

    private void initialize() {
        im = (ImageView) findViewById(R.id.rv_Image);
        email = (TextView) findViewById(R.id.tv_sendEmail);
        name = (TextView) findViewById(R.id.tv_name);
        license = (TextView) findViewById(R.id.detail_licensenumber);
        bttChat = (Button) findViewById(R.id.btt_explore);
        ratingBar = (RatingBar) findViewById(R.id.detail_rating);
        bttMap = (Button) findViewById(R.id.btt_map);


        name.setText(getIntent().getStringExtra("name"));
        ratingBar.setNumStars(getIntent().getIntExtra("rating", 5));
        license.setText(getIntent().getStringExtra("licenseNumber"));
        Glide.with(this).load(getIntent().getStringExtra("image"))
                .asBitmap()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .placeholder(R.drawable.ic_placeholder)
                .into(im);

        if (getIntent().getStringExtra("who").equals("Hotel")) {
            email.setVisibility(View.GONE);
            bttChat.setText(getResources().getString(R.string.book_now));
            RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            buttonLayoutParams.setMargins(0, 12, 0, 0);
            buttonLayoutParams.addRule(RelativeLayout.BELOW, ratingBar.getId());
            buttonLayoutParams.addRule(RelativeLayout.RIGHT_OF, im.getId());
            bttChat.setLayoutParams(buttonLayoutParams);
        }
        im.setOnClickListener(this);
        email.setOnClickListener(this);
        bttChat.setOnClickListener(this);
        bttMap.setOnClickListener(this);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager());

        if (pager != null) {
            pager.setOffscreenPageLimit(3);
            pager.setAdapter(adapter);
        }
        if (tabs != null)
            tabs.setupWithViewPager(pager);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rv_Image:
                Intent intent = new Intent(Detail2.this, FullImageActivity.class);
                intent.putExtra("image", getIntent().getStringExtra("image"));
                intent.putExtra("name", getIntent().getStringExtra("name"));
                startActivity(intent);
                break;

            case R.id.tv_sendEmail:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "abc@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                startActivity(Intent.createChooser(emailIntent, "Send picUrl..."));
                break;

            case R.id.btt_explore:
                FirebaseAuth auth = FirebaseAuth.getInstance();
                /*if (getIntent().getStringExtra("who").equals("Hotel")) {
                     overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
*/
                if (auth.getCurrentUser() != null) {
                    Intent myIntent = new Intent(this, ConversationMain.class);
                    myIntent.putExtra("guideEmail", getIntent().getStringExtra("picUrl"));
                    myIntent.putExtra("sender", "user");
                    startActivity(myIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                } else
                    makeDialogForLogin();
                break;

            case R.id.btt_map:
                startActivity(new Intent(this, GuideLocation.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                break;
        }
    }

    // ************ Dialog if not logged in ******************** //
    private void makeDialogForLogin() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(true)
                .setTitle("Authentication Failed")
                .setMessage("You need to login first")
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(Detail2.this, TabBar.class);
                        intent.putExtra("class", "DetailInformation");
                        startActivity(intent);
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
}