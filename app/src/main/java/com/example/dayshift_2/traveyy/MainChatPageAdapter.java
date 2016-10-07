package com.example.dayshift_2.traveyy;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainChatPageAdapter extends RecyclerView.Adapter<MainChatPageAdapter.MyViewHolder> {

    MyViewHolder holder;
    private List<SingleMessage> chatMessages;

    public MainChatPageAdapter(List<SingleMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_guide_notification, parent, false);
        Fabric.with(parent.getContext(), new Crashlytics());
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        this.holder = holder;
        try {
            if (this.chatMessages.get(position).getName().equals("user")) {
                myTask task = new myTask();
                task.execute(this.get(position).getSenderEmail());
            } else
                holder.picture.setImageResource(R.drawable.ic_image);

        } catch (Exception ex) {
            holder.picture.setImageResource(R.drawable.ic_image);
            Log.i("ExFromMainChatAdapter", ex.getMessage());
        }

        holder.name.setText(this.chatMessages.get(position).getName());
        Log.i("TAGGG", "" + this.chatMessages.get(position).getName());
        holder.message.setText(this.chatMessages.get(position).getMessage());
        holder.time.setText(this.chatMessages.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void add(SingleMessage newMessage) {
        chatMessages.add(newMessage);
        notifyItemInserted(getItemCount() + 1);
    }

    public SingleMessage get(int position) {
        return chatMessages.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView message;
        ImageView picture;
        TextView time;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tv_sender_name);
            message = (TextView) view.findViewById(R.id.tv_sender_message);
            picture = (ImageView) view.findViewById(R.id.iv_sender_pic);
            time = (TextView) view.findViewById(R.id.tv_sender_time);
        }
    }

    private class myTask extends AsyncTask<String, Void, String> {
        String picUrl;



        @Override
        protected String doInBackground(String... params) {

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("User");
            Log.i("PICURL",params[0] + " Count == "+ref.getRef());
            Query query = ref.orderByChild("email").equalTo(params[0]);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        NewUser user = snapshot.getValue(NewUser.class);
                        picUrl = user.getPicUrl();
                        Log.i("PICURL", "" + dataSnapshot.getValue());
                        onPostExecute(picUrl);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                Glide.with(holder.picture.getContext()).load(Uri.parse(s)).fitCenter().
                        placeholder(R.drawable.ic_image).into(holder.picture);
            }catch(Exception ex){
                Log.i("ExceptionCaught",ex.toString() + " String = " + s);
            }
        }
    }
}