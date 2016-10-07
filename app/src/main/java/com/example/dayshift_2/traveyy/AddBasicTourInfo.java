package com.example.dayshift_2.traveyy;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AddBasicTourInfo extends Fragment {
    public static AddBasicTourInfo instance = null;

    public static AddBasicTourInfo newInstance() {
        return instance == null ? (instance = new AddBasicTourInfo()) : instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_basic_tour_info, container, false);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle("Basic Info");
        return root;
    }
}
