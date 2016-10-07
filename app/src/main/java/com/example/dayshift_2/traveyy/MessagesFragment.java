package com.example.dayshift_2.traveyy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.fabric.sdk.android.Fabric;

// ******** Message Fragment Called From Bottom Activity ******* //

public class MessagesFragment extends Fragment {
    private static final String STARTING_TEXT = "Four Buttons Bottom Navigation";
    private MainChatPageAdapter adapter;
    private String myEmail;
    private List<ChatMessage> chatList;

    public MessagesFragment() {
    }

    // Setting up the newInstance For Message Fragment to be called from Bottom Class ***** //

    public static MessagesFragment newInstance() {
        Bundle args = new Bundle();
        args.putString(STARTING_TEXT, "Content For Messages");
        MessagesFragment homeFragment = new MessagesFragment();
        homeFragment.setArguments(args);
        return homeFragment;
    }

    // ****** Inflating the main layout for fragment ******** //
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_singlemessage, container, false);
        Fabric.with(getContext(), new Crashlytics());
        initialize(rootView);
        return rootView;
    }

    private void initialize(View rootView) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null)
            actionBar.setTitle("Messages");

        SharedPreferences prefs = getContext().getSharedPreferences("guideEmail", Context.MODE_PRIVATE);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.message_recyclerview);
        List<SingleMessage> list = new ArrayList<>();
        adapter = new MainChatPageAdapter(list);
        chatList = new ArrayList<>();
        myEmail = prefs.getString("guideemail", "traveyy@traveyy.com");
        Log.i("FromMessageFragment", myEmail);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        assert recyclerView != null : "Layout Manager Is Found NULL";
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                SharedPreferences prefs = getActivity().getSharedPreferences("guideEmail", Context.MODE_PRIVATE);


                Intent intent = new Intent(getContext(), ConversationMain.class);
                intent.putExtra("userEmail", adapter.get(position).getSenderEmail());
                intent.putExtra("guideEmail", prefs.getString("guideemail", "traveyy@traveyy.com"));
                intent.putExtra("sender", "guide");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
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
                            allMessages.get(i).getUserEmail()));
                    array[j] = null;
                }
            }
        }
        for (SingleMessage message : filteredMessages) {
            Log.i("FROMFILTERED", "" + message);
        }
        return filteredMessages;
    }

    public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        public MyAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getContext());
            dialog.setIndeterminate(true);
            dialog.setMessage("Please Wait...");
            dialog.setTitle("Loading Messages");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mDatabase = database.getReference("Messages");

            com.google.firebase.database.Query query = mDatabase.orderByChild("guideEmail").equalTo(myEmail);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        ChatMessage post = postSnapshot.getValue(ChatMessage.class);
                        Log.i("INDATACHANGE", " " + post.getSender() + " " + post.getMessage() + " " + post.getQueryEmail());
                        chatList.add(post);
                    }
                    for (SingleMessage message : filterMessage(chatList)) {
                        adapter.add(message);
                        Log.i("FilteredMessages", message.getMessage() + " " + message.getSenderEmail());
                        //list.add(message);
                        dialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

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



