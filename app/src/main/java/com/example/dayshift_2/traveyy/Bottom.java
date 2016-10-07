package com.example.dayshift_2.traveyy;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarFragment;

import io.fabric.sdk.android.Fabric;

public class Bottom extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three_buttons);
        Fabric.with(this, new Crashlytics());
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        // ************* Setting Up The Bottom Bar ************ //


        BottomBar bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.noTabletGoodness();

        // *********** Adding Item Fragments to the bottom Bar ************ //

        bottomBar.setFragmentItems(getSupportFragmentManager(), R.id.fragmentContainer,
                new BottomBarFragment(ProfileFragment.newInstance("Content For Profile"), R.drawable.ic_profile, "Profile"),

                new BottomBarFragment(MessagesFragment.newInstance(), R.drawable.ic_chat, "Messages"));
        bottomBar.setActiveTabColor(ContextCompat.getColor(this,R.color.web));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.bottom_to_top, R.anim.top_to_bottom);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            mLocationManager.removeUpdates(this);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    55);
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100,
                    0, this);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100,
                    0, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed Too Google Services : " + connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("Longitude = ", +location.getLongitude() + "\nLatitude = " + location.getLatitude());
        Toast.makeText(this, "Location Updated", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 55:

                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("Longitude = ", "" + requestCode);

                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100,
                            1000f, this);
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100,
                            1000f, this);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Not Granted.", Toast.LENGTH_LONG).show();
                    }
                }
        }
    }

}
