package com.example.dayshift_2.traveyy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends BaseActivity
        implements View.OnClickListener {

    // ************* Variables Initialization *************** //

    String placeSelected = null, activitySelected = null, countrySelect = null;
    Button city, search, country;
    TextView activity;
    ArrayAdapter cityAdapter, adapter;
    FirebaseAuth auth;
    private CheckBox guides, hotels;

    // ************* Initialization *************** //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        setDrawer(savedInstanceState, "Traveyy");
        auth = FirebaseAuth.getInstance();
        initialize();
    }

    public void initialize() {
        search = (Button) findViewById(R.id.btt_search);
        city = (Button) findViewById(R.id.btt_city);
        activity = (TextView) findViewById(R.id.tv_activity);
        country = (Button) findViewById(R.id.tv_country);
        guides = (CheckBox) findViewById(R.id.main_guides_checkbox);
        hotels = (CheckBox) findViewById(R.id.main_hotels_checkbox);

        search.setOnClickListener(this);
        city.setOnClickListener(this);
        city.setText(R.string.selectCity);
        activity.setOnClickListener(this);
        country.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // **** OnClick search button
            // **** Check if user selected country and city
            case R.id.btt_search:
                if (countrySelect != null) {

                    if (Utils.isNetworkAvailable(this)) {

                        String selected = "";
                        if ((guides.isChecked() && hotels.isChecked()) ||
                                (!guides.isChecked() && !hotels.isChecked()))
                            selected = "";
                        else if (guides.isChecked() && !hotels.isChecked())
                            selected = "Guide";
                        else if (!guides.isChecked() && hotels.isChecked())
                            selected = "Hotel";
                        if (placeSelected == null)
                            placeSelected = "";
                        Intent intent = new Intent(this, Guides.class);
                        intent.putExtra("country", countrySelect.trim());
                        intent.putExtra("city", placeSelected.trim());
                        intent.putExtra("who", selected.trim());
                        startActivity(intent);
                        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                    } else
                        Toast.makeText(this, "Could Not Connect To The Network", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(this, "Please Select Your Destination!", Toast.LENGTH_LONG).show();
                break;

            // ***** Set and adapter that is populated from the string resource "Countries"
            // ***** Set a dialog to let the user choose the country
            case R.id.tv_country:
                adapter = ArrayAdapter.createFromResource(this,
                        R.array.countries, android.R.layout.simple_list_item_1);
                new AlertDialog.Builder(this)
                        .setTitle("Countries:")
                        .setCancelable(true)
                        .setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                countrySelect = adapter.getItem(which).toString();
                                country.setText(countrySelect);
                                city.setText(getString(R.string.selectCity));
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;

            // ***** Set and adapter that is populated from the string resource "Activities"
            // ***** Set a dialog to let the user choose the activity

            case R.id.tv_activity:
                adapter = ArrayAdapter.createFromResource(this,
                        R.array.activities, android.R.layout.simple_list_item_1);
                new AlertDialog.Builder(this)
                        .setTitle("Activities:")
                        .setCancelable(true)
                        .setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activitySelected = adapter.getItem(which).toString();
                                activity.setText(activitySelected);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;


            // ***** Set and adapter that is populated from the string resource "Cities"
            // ***** Set a dialog to let the user choose the city
            case R.id.btt_city:
                if (cityWrtCountry(country.getText().toString())) {
                    new AlertDialog.Builder(this)
                            .setTitle("Cities:")
                            .setCancelable(true)
                            .setSingleChoiceItems(cityAdapter, -1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    placeSelected = cityAdapter.getItem(which).toString();
                                    city.setText(placeSelected);
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                } else
                    Toast.makeText(this, "Place Select A Country First!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // **** Populate the city adapter w.r.t the country selected **** //

    private boolean cityWrtCountry(String text) {

        switch (text) {
            case "United Arab Emirates":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.uaeCities, android.R.layout.simple_list_item_1);
                return true;
            case "Thailand":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.thailandCities, android.R.layout.simple_list_item_1);
                return true;
            case "Malaysia":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.malaysiaCities, android.R.layout.simple_list_item_1);
                return true;
            case "Turkey":
                cityAdapter = ArrayAdapter.createFromResource(this,
                        R.array.turkeyCities, android.R.layout.simple_list_item_1);
                return true;
        }
        return false;
    }

    // ** Set the Menu Button text according the user Login Status ** //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        if (auth.getCurrentUser() != null)
            menu.getItem(0).setTitle("Logout");
        else
            menu.getItem(0).setTitle("Login");

        return super.onCreateOptionsMenu(menu);
    }

    // ***** Check if user is already signed in then logout the user
    // ***** else let the user login by calling TAB BAR activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.login) {
            if (auth.getCurrentUser() == null) {
                startActivity(new Intent(this, TabBar.class));
                this.overridePendingTransition(R.anim.slide_in_top, R.anim.slide_in_bottomm);
            } else {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                item.setTitle("Login");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        if (auth.getCurrentUser() != null)
            updateProfile();

        placeSelected = "";
        invalidateOptionsMenu();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (result != null) {
            if (result.isDrawerOpen())
                result.closeDrawer();
            else
                this.finish();
        }
    }
}