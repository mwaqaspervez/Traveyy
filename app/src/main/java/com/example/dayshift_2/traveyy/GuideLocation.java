package com.example.dayshift_2.traveyy;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GuideLocation extends BaseActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_location);
        setDrawer(savedInstanceState,"Location");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng sydney = new LatLng(Utils.getPrefs(this,"selected").getFloat("lat",24.8615f)
                , Utils.getPrefs(this,"selected").getFloat("long",67.0099f));
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Guide Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16.0f));

    }
}
