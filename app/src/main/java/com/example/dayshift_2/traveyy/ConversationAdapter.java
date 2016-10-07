package com.example.dayshift_2.traveyy;


import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import io.fabric.sdk.android.Fabric;

class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> {

    private List<ChatMessage> chatMessages;


    ConversationAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_chat_message, parent, false);
        Fabric.with(parent.getContext(), new Crashlytics());

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(holder.layout.getLayoutParams());
        if (this.chatMessages.get(position).getSender().equals("user")) {
            holder.parent_layout.setGravity(Gravity.END);
            holder.layout.setBackgroundResource(R.drawable.in);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.setMarginStart(128);
                params.setMarginEnd(8);
            }
        } else {
            holder.parent_layout.setGravity(Gravity.START);
            holder.layout.setBackgroundResource(R.drawable.out);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.setMarginEnd(128);
                params.setMarginStart(8);
            }
        }

        holder.layout.setLayoutParams(params);
        holder.textMessage.setText(this.chatMessages.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void add(ChatMessage newMessage) {
        chatMessages.add(newMessage);
        notifyItemInserted(getItemCount() + 1);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textMessage;
        LinearLayout layout;
        LinearLayout parent_layout;


        MyViewHolder(View view) {
            super(view);
            textMessage = (TextView) view.findViewById(R.id.txtMessage);
            layout = (LinearLayout) view.findViewById(R.id.gravity_layout);
            parent_layout = (LinearLayout) view.findViewById(R.id.parent_layout);
        }
    }
}
