package com.example.capstone.adapters;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.models.Message;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.time.Duration;
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
        holder.tvMessageContent.setTag(message.getSenderID());
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
        private ConstraintLayout cvItemMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // assign ids to views
            tvMessageContent = itemView.findViewById(R.id.tvMessageContent);
            tvScreenNameMessage = itemView.findViewById(R.id.tvScreenNameMessage);
            tvDateTIme = itemView.findViewById(R.id.tvDateTIme);
            tvUsernameMessage = itemView.findViewById(R.id.tvUsernameMessage);
            cvItemMessage = itemView.findViewById(R.id.cvItemMessage);

            }

        public void bind(Message message) {
            // bind data to views
            int position = getBindingAdapterPosition();
            tvMessageContent.setText(message.getMessage_content());
            Date date = new Date(message.getDate_time());
            String[] dateStr = date.toString().split(" ");
            String[] fullTime = dateStr[3].split(":");
            String time = fullTime[0] + ":" + fullTime[1];
            tvDateTIme.setText(dateStr[0] + " " + dateStr[1] + " " + dateStr[2] + " " + time);
            ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
            query.whereEqualTo("objectId", message.getSenderID());
            query.findInBackground((users, e) -> {
                tvScreenNameMessage.setText(users.get(0).getString("screenName"));
                tvUsernameMessage.setText("@" + users.get(0).getUsername());
            });

            // Changes how the text message appears depending on who sent it
            if (Objects.equals(ParseUser.getCurrentUser().getObjectId(), message.getSenderID())) {
                tvMessageContent.setBackgroundResource(R.drawable.bg_send_message);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(cvItemMessage);
                constraintSet.clear(tvMessageContent.getId());
                constraintSet.applyTo(cvItemMessage);
                cvItemMessage.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.END));
            }

            // check if the message two above is of the same id, if not remove the bottom padding

            // removes message details if message before it is the same person
            if (position != messages.size() - 1) {
                Message lastMessage = messages.get(position + 1);
                Date lastDate = new Date(message.getDate_time());
                if (Objects.equals(lastMessage.getSenderID(), message.getSenderID()) && lastDate.toInstant().plus(Duration.ofHours(1)).isAfter(date.toInstant())) {
                    tvScreenNameMessage.setVisibility(View.GONE);
                    tvDateTIme.setVisibility(View.GONE);
                    tvUsernameMessage.setVisibility(View.GONE);
                    int right_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());
                    int bottom_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());

                    cvItemMessage.setPadding(0, 0, right_px, bottom_px);
                }
            }


        }
    }
}
