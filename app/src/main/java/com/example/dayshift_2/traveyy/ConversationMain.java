package com.example.dayshift_2.traveyy;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class ConversationMain extends AppCompatActivity {

    private ConversationAdapter adapter;
    private EditText editText;
    private String userEmail;
    private String guideEmail;
    private String queryEmail;
    private String sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_conversation_layout);
        Fabric.with(this, new Crashlytics());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Messages");
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null)
            userEmail = auth.getCurrentUser().getEmail();
        else
            userEmail = getIntent().getStringExtra("userEmail");

        guideEmail = getIntent().getStringExtra("guideEmail");

        queryEmail = userEmail + " " + guideEmail;

        sender = getIntent().getStringExtra("sender");

        List<ChatMessage> myMessages = new ArrayList<>();
        editText = (EditText) findViewById(R.id.messageInput);
        ImageButton bttSendMessage = (ImageButton) findViewById(R.id.sendButton);

        assert bttSendMessage != null : "Cannot find button reference";
        bttSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Messages");
                if (!editText.getText().toString().equals("")) {

                    String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                    ChatMessage chat = new ChatMessage(editText.getText().toString(), userEmail, guideEmail, userEmail + " " + guideEmail, mydate, sender);
                    myRef.push().setValue(chat);
                    editText.setText("");
                }
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_conversation);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setStackFromEnd(true);
        assert recyclerView != null : "Layout Manager Is Found NULL";
        recyclerView.setLayoutManager(mLayoutManager);

        adapter = new ConversationAdapter(myMessages);
        recyclerView.setAdapter(adapter);
        GetMessagesAsync async = new GetMessagesAsync();
        async.execute(guideEmail);
    }

    // **** On BackPressed implemented on Top arrow **** //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
        return super.onOptionsItemSelected(item);
    }

    public class GetMessagesAsync extends AsyncTask<String, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ConversationMain.this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setTitle("Loading Messages");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mDatabase = database.getReference("Messages");
            Query query = mDatabase.orderByChild("queryEmail").equalTo(queryEmail);

            // *********** when a new message is add on child added is called ******** //
            // *********** Used when a new message is added *********** //
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                    adapter.add(message);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }

        // ******* Dismiss dialog when asyncTask completes ******** //
        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }
}
