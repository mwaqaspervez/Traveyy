package com.example.dayshift_2.traveyy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class EditUserProfile extends AppCompatActivity {

    private static final int PICK_PHOTO_FOR_AVATAR = 0;
    protected EditText country, phoneNUmber, name;
    protected String picUrl = "";
    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_user_profile);
        Fabric.with(this, new Crashlytics());
        avatar = (ImageView) findViewById(R.id.roundedImageview_edit_user_profile);
        name = (EditText) findViewById(R.id.edit_user_name);
        phoneNUmber = (EditText) findViewById(R.id.edit_user_phone);
        country = (EditText) findViewById(R.id.edit_user_country);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });


        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_user_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        } else if (item.getItemId() == R.id.save_button) {
            if (checkFields()) {
                MyTask task = new MyTask();
                //noinspection ConstantConditions
                task.execute(name.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                        phoneNUmber.getText().toString(), country.getText().toString(), picUrl);
            } else
                Toast.makeText(this, "Please Enter The Remaining Fields Below", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkFields() {
        return !(name.getText().toString().equals("")
                || country.getText().toString().equals("") || phoneNUmber.getText().toString().equals(""));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
                if (data == null)
                    return;

                Uri uri = data.getData();
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                avatar.setImageBitmap(bmp);
            }
        } catch (OutOfMemoryError ex) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 6;

            AssetFileDescriptor fileDescriptor;
            try {
                fileDescriptor = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
                assert fileDescriptor != null : "FileDescriptor is missing";
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                avatar.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError exx) {
                makeDialog("File size too large");
            }

            //Now you can do whatever you want with your input stream, save it as file, upload to a server, decode a bitmap...
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    public class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String query = null;
            try {
                query = String.format(Locale.getDefault(), "name=%s" +
                                "&" +
                                "email=%s" +
                                "&" +
                                "phoneNumber=%s" +
                                "&" +
                                "country=%s" + "&" +
                                "picUrl=%s", params[0], params[1], params[2], params[3],
                        URLEncoder.encode(params[4], "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return Utils.postRequest("update_user.php", query);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.toLowerCase().trim().equals("success")) {
                s = "Profile Updated";
                Intent intent = new Intent(EditUserProfile.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else
                s = "Something Went Wrong\n" + s;
            Toast.makeText(EditUserProfile.this, s, Toast.LENGTH_SHORT).show();
        }
    }
}
