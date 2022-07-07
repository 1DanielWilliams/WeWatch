package com.example.capstone.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.models.Message;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private Context context;
    private List<Message> messages;


    public MessagesAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessageContent;
        private TextView tvScreenNameMessage;
        private TextView tvDateTIme;
        private TextView tvUsernameMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // assign ids to views
            tvMessageContent = itemView.findViewById(R.id.tvMessageContent);
            tvScreenNameMessage = itemView.findViewById(R.id.tvScreenNameMessage);
            tvDateTIme = itemView.findViewById(R.id.tvDateTIme);
            tvUsernameMessage = itemView.findViewById(R.id.tvUsernameMessage);
            //change where the layout is depending on who sent it

            }

        public void bind(Message message) {
            // bind data to views

            tvMessageContent.setText(message.getMessage_content());
            String[] date = new Date(message.getDate_time()).toString().split(" ");
            String[] fullTime = date[3].split(":");
            String time = fullTime[0] + ":" + fullTime[1];
            tvDateTIme.setText(date[0] + " " + date[1] + " " + date[2] + " " + time);
            ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
            query.whereEqualTo("objectId", message.getSenderID());
            query.findInBackground((users, e) -> {
                tvScreenNameMessage.setText(users.get(0).getString("screenName"));
                tvUsernameMessage.setText("@" + users.get(0).getUsername());
            });

            // Changes how the text message appears depending on who sent it
            if (Objects.equals(ParseUser.getCurrentUser().getObjectId(), message.getSenderID())) {

                tvMessageContent.setBackgroundResource(R.drawable.bg_send_message);

            }

        }
    }
}
