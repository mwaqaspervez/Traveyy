package com.example.dayshift_2.traveyy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class UserSignUp extends AppCompatActivity {

    private static final int PICK_PHOTO_FOR_AVATAR = 2;
    public FirebaseAuth auth;
    Button btSignUp;
    TextView tvPicture;
    Uri downloadLink;
    private EditText edEmail, edPassword, edRePassword, edPhone, edCountry, edName;
    private ImageView ivPicture;
    private Bitmap bitmap = null;
    private ProgressDialog dialog;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signup);
        Fabric.with(this, new Crashlytics());
        initialize();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
                    assert auth.getCurrentUser() != null : "User Not Found";
                    auth.getCurrentUser().updateProfile(request.setDisplayName(edName.getText().toString()).setPhotoUri(downloadLink).build());
                    if (downloadLink != null) {
                        dialog.dismiss();
                        startActivity(new Intent(UserSignUp.this, MainActivity.class));
                    }
                } else {
                    // User is signed out
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                        Toast.makeText(UserSignUp.this, "Could Not Complete Request", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null)
            auth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            this.overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize() {

        edEmail = (EditText) findViewById(R.id.iv_signup_email);
        edPassword = (EditText) findViewById(R.id.iv_signup_password);
        edRePassword = (EditText) findViewById(R.id.iv_signup_re_password);
        edPhone = (EditText) findViewById(R.id.iv_signup_phone);
        edCountry = (EditText) findViewById(R.id.iv_signup_country);
        edName = (EditText) findViewById(R.id.iv_signup_name);
        tvPicture = (TextView) findViewById(R.id.tv_signup_picture);
        ivPicture = (ImageView) findViewById(R.id.iv_signup_picture);
        btSignUp = (Button) findViewById(R.id.btt_signup_signup);

        // ****** Start Activity for result for image select ******** //

        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        tvPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });


        // ******** On Click of button sign up. Check if same password then valid picUrl and start Async Task ********//
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSamePassword()) {
                    if (isValidEmail(edEmail.getText().toString())) {

                        dialog = new ProgressDialog(UserSignUp.this);
                        dialog.setIndeterminate(true);
                        dialog.setMessage("Please Wait...");
                        dialog.setTitle("Signing Up");
                        dialog.setCancelable(true);
                        dialog.show();
                        // ** Executing Async Task For SignUp ** //
                        new myAsync().execute(
                                edEmail.getText().toString(),
                                edPassword.getText().toString(),
                                edPhone.getText().toString(),
                                edCountry.getText().toString(),
                                edName.getText().toString()
                        );
                    } else
                        Toast.makeText(UserSignUp.this, "Email is not valid", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(UserSignUp.this, "Password does not match OR Password should be at least '6' characters", Toast.LENGTH_SHORT).show();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sign Up");
        }
    }

    // ***** Starting image picker intent ****** //
    public void pickImage() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    // ******* On Activity Result For getting the image back *****//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
                if (data == null) return;

                Uri uri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ivPicture.setImageBitmap(Utils.getResizedBitmap(bitmap, 64, 64));
            }
        } catch (IOException e) {
            e.printStackTrace();

            // **** IF image size is large than down sample it up to 6 times **** //
        } catch (OutOfMemoryError ex) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 6;

            try {
                AssetFileDescriptor fileDescriptor = getContentResolver().openAssetFileDescriptor(data.getData(), "r");

                assert fileDescriptor != null : "File Descriptor is found null";
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                ivPicture.setImageBitmap(Utils.getResizedBitmap(bitmap, 64, 64));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError exx) {
                makeDialog("File size too large");
            }
        }
    }

    // *** Checking if user has entered same password **** //
    public boolean checkSamePassword() {
        return edPassword.getText().toString().toLowerCase().equals(edRePassword.getText().toString().toLowerCase())
                && edPassword.getText().toString().length() >= 6;
    }

    // ******** Check if Email is valid From Java Library ***********//
    public boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // ***** Make Dialog with message and title ******** //
    private void makeDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserSignUp.this);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    public class myAsync extends AsyncTask<String, Boolean, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // ********** Params are Username,Password , Phone Number and Country ************ //
        @Override
        protected String doInBackground(final String... params) {

            // **** Getting the storage link to the firebase *********//

            FirebaseStorage storage = FirebaseStorage.getInstance();

            // **** Save the image in image folder and rename it as the user picUrl address ***** //
            StorageReference storageRef = storage.getReferenceFromUrl("gs://traveyy-d1e4f.appspot.com")
                    .child("images/" + params[0]);
            ByteArrayOutputStream b = new ByteArrayOutputStream();

            // **** Checking if the user has selected any photo else use the default image ****//
            if (bitmap != null) {

                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, b);
                byte[] data = b.toByteArray();
                UploadTask uploadTask = storageRef.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        downloadLink = taskSnapshot.getDownloadUrl();
                        // ****  Save the user in firebase database
                        // with user data and the link to the profile image

                        publishProgress(true);

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("User");
                        myRef.push().setValue(new NewUser(params[0], params[1], params[2], params[3], downloadLink.toString(), params[4]));

                        String query = null;
                        try {
                            query = String.format(Locale.getDefault(), "name=%s" +
                                    "&" +
                                    "password=%s" +
                                    "&" +
                                    "email=%s" +
                                    "&" +
                                    "phoneNumber=%s" +
                                    "&" +
                                    "picUrl=%s +" +
                                    "&" +
                                    "country=%s", params[4], params[1], params[0], params[2], URLEncoder.encode(downloadLink.toString(), "UTF-8"), params[3]);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Utils.postRequest("insert_username.php", query);
                    }
                });
            }
            // ****** Add on success listener ********* //
            auth.createUserWithEmailAndPassword(params[0], params[1])
                    .addOnFailureListener(UserSignUp.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserSignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful())
                        Toast.makeText(UserSignUp.this, String.format("Authentication Failed -> %s", task.getException()),
                                Toast.LENGTH_SHORT).show();

                }
            });
            return null;
        }
    }


}