package com.example.dayshift_2.traveyy;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainChatPage extends AppCompatActivity {

    private MainChatPageAdapter adapter;
    private String myEmail;
    private List<ChatMessage> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_singlemessage);
        Fabric.with(this, new Crashlytics());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Messages");
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.message_recyclerview);
        List<SingleMessage> list = new ArrayList<>();
        adapter = new MainChatPageAdapter(list);
        chatList = new ArrayList<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null)
            myEmail = auth.getCurrentUser().getEmail();
        else
            myEmail = "Traveyy.com";

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        assert recyclerView != null : "Layout Manager Is Found NULL";
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainChatPage.this, ConversationMain.class);
                intent.putExtra("guideEmail", adapter.get(position).getSenderEmail());
                intent.putExtra("sender", getIntent().getStringExtra("sender"));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }

    private List<SingleMessage> filterMessage(List<ChatMessage> allMessages) {

        HashSet<String> hashSet = new HashSet<>();

        for (ChatMessage message : allMessages) {
            hashSet.add(message.getQueryEmail());
        }
        String[] array = hashSet.toArray(new String[hashSet.size()]);
        List<SingleMessage> filteredMessages = new ArrayList<>();
        for (int i = allMessages.size() - 1; i > 0; i--) {

            for (int j = 0; j < array.length; j++) {
                if (allMessages.get(i).getQueryEmail().equals(array[j])) {
                    filteredMessages.add(new SingleMessage(allMessages.get(i).getSender()
                            , allMessages.get(i).getMessage(), allMessages.get(i).getDate(),
                            allMessages.get(i).getGuideEmail()));
                    array[j] = null;
                }
            }
        }
        return filteredMessages;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        public MyAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainChatPage.this);
            dialog.setIndeterminate(true);
            dialog.setMessage("Please Wait...");
            dialog.setTitle("Loading Messages");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mDatabase = database.getReference("Messages");

            Query query = mDatabase.orderByChild("userEmail").equalTo(myEmail);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        ChatMessage post = postSnapshot.getValue(ChatMessage.class);
                        chatList.add(post);
                    }
                    for (SingleMessage message : filterMessage(chatList))
                        adapter.add(message);
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
