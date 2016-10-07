package com.example.dayshift_2.traveyy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.io.FileNotFoundException;
import java.io.IOException;

import io.fabric.sdk.android.Fabric;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String STARTING_TEXT = "Four Buttons Bottom Navigation";
    private static final int RESULT_LOAD_IMAGE = 2;
    Bitmap bitmap = null;
    private TextView guideName;
    private TextView guideStatus;
    private int selected = 0;
    private ImageView dp;
    private AlertDialog dialog;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(String text) {
        Bundle args = new Bundle();
        args.putString(STARTING_TEXT, text);
        ProfileFragment homeFragment = new ProfileFragment();
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_profile_fragment, container, false);
        Fabric.with(getContext(), new Crashlytics());
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        RelativeLayout nameLayout, statusLayout;

        Button addTour = (Button) rootView.findViewById(R.id.add_tour);
        guideName = (TextView) rootView.findViewById(R.id.guideSignedUp_name);
        guideStatus = (TextView) rootView.findViewById(R.id.statusChange_profile_fragment);
        dp = (ImageView) rootView.findViewById(R.id.displayimage_profile_fragment);

        nameLayout = (RelativeLayout) rootView.findViewById(R.id.name_profile_fragment);
        statusLayout = (RelativeLayout) rootView.findViewById(R.id.status_layout_profile_fragment);

        nameLayout.setOnClickListener(this);
        statusLayout.setOnClickListener(this);
        dp.setOnClickListener(this);
        addTour.setOnClickListener(this);

        if (actionBar != null)
            actionBar.setTitle("Profile");

        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.name_profile_fragment:
                makeDialogForNameChange();
                break;

            case R.id.status_layout_profile_fragment:
                makeDialogForStatusChange();
                break;

            case R.id.displayimage_profile_fragment:
                LayoutInflater inflater = getLayoutInflater(null);
                View dialoglayout = inflater.inflate(R.layout.custom_layout_profile_fragment, null);
                TextView viewPic = (TextView) dialoglayout.findViewById(R.id.view_profile_picture);
                TextView changePic = (TextView) dialoglayout.findViewById(R.id.change_profile_picture);

                viewPic.setOnClickListener(this);
                changePic.setOnClickListener(this);

                // ************** Show the available options that is **************//
                // ************** 1 - View Profile Image **************//
                // ************** 2 - Change Profile Image **************//
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true)
                        .setTitle("Profile Picture")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setView(dialoglayout)
                        .create();
                dialog = builder.show();
                break;

            // ************** Start FullImageActivity to show the full image **************//
            case R.id.view_profile_picture:
                Intent intent = new Intent(getContext(), FullImageActivity.class);
                intent.putExtra("image", Utils.getResizedBitmap(drawableToBitmap(dp.getDrawable()), 240, 360));
                startActivity(intent);

                break;

            // ************** Start an intent to get a user selected image **************//
            case R.id.change_profile_picture:
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;

            case R.id.add_tour:
                startActivity(new Intent(getContext(), ViewTour.class));
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                break;
        }
    }

    // ************** Make a dialog to change the name  **************//
    private void makeDialogForNameChange() {
        final EditText name = new EditText(getContext());
        name.setInputType(InputType.TYPE_CLASS_TEXT);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Change Display Name")
                .setView(name)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (name.getText() != null && !name.getText().toString().equals(""))
                            guideName.setText(name.getText().toString());

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
                .show();
    }

    // ************** Make Dialog to show the status available **************//
    private void makeDialogForStatusChange() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Current Status")
                .setCancelable(true)
                .setSingleChoiceItems(getResources().getStringArray(R.array.statusMenu), -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected = which;
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                guideStatus.setText(getResources().getStringArray(R.array.statusMenu)[selected]);

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        }).show();
    }

    // ************** To get image from the result back from the intent **************//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
                Uri uri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                dp.setImageBitmap(Utils.getResizedBitmap(bitmap, 240, 360));
            }
        } catch (IOException e) {
            e.printStackTrace();

            // **** IF image size is large than downsample it upto 6 times **** //
        } catch (OutOfMemoryError ex) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 6;

            try {
                AssetFileDescriptor fileDescriptor = getActivity().getContentResolver().openAssetFileDescriptor(data.getData(), "r");

                assert fileDescriptor != null : "The file format is not supported.";
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                dp.setImageBitmap(Utils.getResizedBitmap(bitmap, 240, 360));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError exx) {
                makeDialog("File size too large");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dialog != null)
            dialog.dismiss();
    }


    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    // ***** Make Dialog with message and title ******** //
    private void makeDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Error")
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
