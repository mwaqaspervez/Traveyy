package com.example.dayshift_2.traveyy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.fabric.sdk.android.Fabric;

public class GuideMainPage extends Activity implements View.OnClickListener {

    public static final int RC_SIGN_IN = 100;
    Button btSignUp, btLoginIn, btFb;
    SignInButton btGoogle;
    EditText username, password;
    TextView emailAddress;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_gudiemainpage);
        mAuth = FirebaseAuth.getInstance();
        Fabric.with(this, new Crashlytics());
        initialize();
    }

    private void initialize() {
        btLoginIn = (Button) findViewById(R.id.btt_user_login);
        btSignUp = (Button) findViewById(R.id.btt_signup);
        btFb = (Button) findViewById(R.id.fb_login_button);
        btGoogle = (SignInButton) findViewById(R.id.google_login_button);
        username = (EditText) findViewById(R.id.et_login_cust_username);
        password = (EditText) findViewById(R.id.et_login_cust_password);
        username.setImeOptions(EditorInfo.IME_ACTION_DONE);
        password.setImeOptions(EditorInfo.IME_ACTION_DONE);
        emailAddress = (TextView) findViewById(R.id.tv_emailaddress);

        btLoginIn.setOnClickListener(this);
        btSignUp.setOnClickListener(this);
        btFb.setOnClickListener(this);
        btGoogle.setOnClickListener(this);
        emailAddress.setOnClickListener(this);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    GuideMainPage.this.finish();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btt_user_login:
                String email = username.getText().toString();
                String pass = password.getText().toString();
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setCancelable(false);
                dialog.setTitle("Signing In");
                dialog.setMessage("Loading");
                dialog.setIndeterminate(true);
                dialog.show();
                if (Utils.isNetworkAvailable(this)) {
                    if (!(email.equals("") && pass.equals(""))) {
                        mAuth.signInWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        dialog.dismiss();

                                    }
                                }).addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(GuideMainPage.this, "Authentication failed. " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(GuideMainPage.this, "Please Enter Username And Password",
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(GuideMainPage.this, "Could Not Connect To The Network",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                break;
            case R.id.fb_login_button:
            case R.id.google_login_button:
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setProviders(
                                        AuthUI.GOOGLE_PROVIDER,
                                        AuthUI.FACEBOOK_PROVIDER)
                                .setTheme(R.style.AppTheme_WithActionBar)
                                .build(),
                        RC_SIGN_IN);
                this.getParent().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                break;
            case R.id.btt_signup:
                startActivity(new Intent(this, UserSignUp.class));
                this.getParent().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                break;
            case R.id.tv_emailaddress:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getString(R.string.website)));
                startActivity(i);
                break;
        }
    }

    private void makeDialog(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        this.getParent().onBackPressed();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // user is signed in!
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                makeDialog("Couldn't Complete SignIn Request", "Error Authenticating");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() != null)
            this.finish();
    }

}

