package com.example.dayshift_2.traveyy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class BaseActivity extends AppCompatActivity {
    // **** save our header or result
    protected Drawer result = null;
    private FirebaseAuth auth;
    private String name;
    private String email;
    private Uri url;
    private ProfileDrawerItem profile;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        RelativeLayout fullLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_sample_custom_container_dark_toolbar, null);
        FrameLayout activityContainer = (FrameLayout) fullLayout.findViewById(R.id.frame_container);
        Fabric.with(this, new Crashlytics());
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        auth = FirebaseAuth.getInstance();
        super.setContentView(fullLayout);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // ******* add the values which need to be saved from the drawer to the bundle ********
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen())
            result.closeDrawer();
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
    }

    protected void setDrawer(Bundle savedInstanceState, String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        auth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        setImageLoader();
        setUserData();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"white\">" + title + "</font>"));
        }

        if (toolbar != null) {

            profile = new ProfileDrawerItem().withName(name).withEmail(email).withIcon(url);

            AccountHeader headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.dubai)
                    .addProfiles(profile)
                    .build();
            result = new DrawerBuilder(BaseActivity.this)
                    // ******** this layout have to contain child layouts ******** //
                    .withRootView(R.id.drawer_container)
                    .withDisplayBelowStatusBar(false)
                    .withToolbar(toolbar)
                    .withAccountHeader(headerResult)
                    .withActionBarDrawerToggleAnimated(true)
                    .addDrawerItems(
                            new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home),
                            new PrimaryDrawerItem().withName(R.string.drawer_item_message).withIcon(FontAwesome.Icon.faw_comment)
                                    .withBadgeStyle(new BadgeStyle().withColor(getResources().getColor(R.color.web)).withTextColor(Color.WHITE)).withBadge("4"),
                            new PrimaryDrawerItem().withName(R.string.drawer_item_profile).withIcon(FontAwesome.Icon.faw_user),
                            new SectionDrawerItem().withName(R.string.drawer_item_section_header),
                            new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog),
                            new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_question),
                            new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_bullhorn)
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            switch (position) {
                                case 1:
                                    if (!getLocalClassName().equals("MainActivity")) {
                                        Intent intent = new Intent(BaseActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                                    } else if (result.isDrawerOpen())
                                        result.closeDrawer();
                                    break;
                                case 2:
                                    if (checkIfLoggedIn()) {
                                        Intent intent = new Intent(BaseActivity.this, MainChatPage.class);
                                        intent.putExtra("sender", "user");
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                                    }
                                    break;
                                case 3:
                                    if (checkIfLoggedIn()) {
                                        startActivity(new Intent(BaseActivity.this, UserProfile.class));
                                        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                                    }
                                    break;
                                case 5:
                                    startActivity(new Intent(BaseActivity.this, SettingPref.class));
                                    overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                                    break;
                                case 6:
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(getString(R.string.website)));
                                    startActivity(i);
                                    overridePendingTransition(R.anim.bottom_to_top, R.anim.top_to_bottom);
                                    break;
                                case 7:
                                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                            "mailto", getString(R.string.traveyy_email), null));
                                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                                    startActivity(Intent.createChooser(emailIntent, "Send picUrl..."));
                                    break;
                            }
                            return true;
                        }
                    })
                    .withSavedInstance(savedInstanceState)
                    .build();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // *** Respond to the action bar's Up/Home button *** //
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private boolean checkIfLoggedIn() {
        if (auth.getCurrentUser() == null)
            makeDialogForLogin();
        else
            return true;

        return false;
    }
    private void makeDialogForLogin() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(true)
                .setTitle("Authentication Failed")
                .setMessage("You need to login first")
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(BaseActivity.this, TabBar.class);
                        intent.putExtra("class", "DetailInformation");
                        startActivity(intent);
                        BaseActivity.this.overridePendingTransition(R.anim.slide_in_top, R.anim.slide_in_bottomm);
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
    private void setImageLoader() {
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });
    }
    private void setUserData() {
        if (auth.getCurrentUser() != null) {
            final FirebaseUser user = auth.getCurrentUser();
            name = user.getDisplayName();
            email = user.getEmail();
            url = user.getPhotoUrl();

        } else {
            // ********* Default Credentials ********* //
            name = "Traveyy User";
            email = "Traveyy.com";
            url = Uri.parse("https://firebasestorage.googleapis.com/v0/b/traveyy-d1e4f.appspot.com/o/images%2Fapp_logo.png?alt=media&token=45178200-331b-4cbf-96c8-075405b474d6");
        }
    }
    protected void updateProfile(){
        setUserData();
        profile = new ProfileDrawerItem().withEmail(email).withIcon(url).withName(name);
    }
}