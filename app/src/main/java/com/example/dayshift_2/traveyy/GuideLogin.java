package com.example.dayshift_2.traveyy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class GuideLogin extends Activity {

    Button btLogin;
    EditText email;
    EditText password;
    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_guide_login);
        Fabric.with(this, new Crashlytics());
        context = this;
        btLogin = (Button) findViewById(R.id.btt_guide_login);
        email = (EditText) findViewById(R.id.et_login_guide_username);
        password = (EditText) findViewById(R.id.et_login_guide_password);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //   if(isValidEmail(picUrl.getText().toString())) {
              //     saveEmail(picUrl.getText().toString());
                saveEmail("norman@elistguy.com");
                startActivity(new Intent(GuideLogin.this, Bottom.class));
                context.getParent().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                // }
               // myTask task = new myTask();
              //  task.execute();
            }
        });
    }

    public void saveEmail(String email) {
        SharedPreferences prefs = getSharedPreferences("guideEmail", MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("guideemail", email);
        edit.apply();
    }

    public boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void onBackPressed() {
        this.getParent().onBackPressed();
    }

    public class myTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            return Utils.postRequest("insert_review.php",
                    "name=waqas&message=ok fine&email=waqas@gmail.com&rating=5&imageUri=asdasd&date=04-august-2014&reviewFor=ali@gmail.com");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.toLowerCase().contains("ERROR CODE = 1062".toLowerCase()))
                btLogin.setText("User Already Exist!!");
            else
                btLogin.setText(Html.fromHtml(s));
        }
    }

}
