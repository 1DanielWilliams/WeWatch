package com.example.capstone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.models.Conversation;

import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder>{
    private Context context;
    private List<Conversation> conversations;

    public ConversationsAdapter(Context context, List<Conversation> conversations) {
        this.context = context;
        this.conversations = conversations;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);
        holder.bind(conversation);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //declare different views of post up here

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // assign views to ids
        }
        public void bind(Conversation conversation) {
            //bind data from event object to different views above

        }
    }
}
