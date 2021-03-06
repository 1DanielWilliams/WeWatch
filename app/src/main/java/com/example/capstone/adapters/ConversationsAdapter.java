package com.example.capstone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.capstone.R;
import com.example.capstone.methods.GroupChatMethods;
import com.example.capstone.models.GroupDetail;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder>{
    private Context context;
    private List<GroupDetail> groupDetails;

    public ConversationsAdapter(Context context, List<GroupDetail> groupDetails) {
        this.context = context;
        this.groupDetails = groupDetails;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupDetail groupDetail = groupDetails.get(position);
        holder.bind(groupDetail);
    }

    @Override
    public int getItemCount() {
        return groupDetails.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGcName;
        private TextView tvMessagePreview;
        private TextView tvTimeDateConversation;
        private LottieAnimationView lavTypingIndicatorConversation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvGcName = itemView.findViewById(R.id.tvGcName);
            tvMessagePreview = itemView.findViewById(R.id.tvMessagePreview);
            tvTimeDateConversation = itemView.findViewById(R.id.tvTimeDateConversation);
            lavTypingIndicatorConversation = itemView.findViewById(R.id.lavTypingIndicatorConversation);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                GroupDetail groupDetail = groupDetails.get(position);
                if (groupDetail.getName().contains("@")) {
                    GroupChatMethods.toConversationDetail(context, groupDetails.get(position).getId(), true, "");
                } else {
                    int toUserIdPosition = toUserIdPosition(groupDetail.getUsers());
                    GroupChatMethods.toConversationDetail(context, groupDetails.get(position).getId(), false, groupDetail.getUsers().get(toUserIdPosition));
                }
            });


        }
        public void bind(GroupDetail groupDetail) {
            String name = groupDetail.getName();
            if (name.contains("@")) {
                String[] nameTime = name.split("@");

                name = nameTime[0];
                if (name.length() > 25) {
                    name = name.substring(0, 25) + "... @" + nameTime[1];
                } else {
                    name += "@" + nameTime[1];
                }
                tvGcName.setText(name);
            } else {
                int toUserIdPosition = toUserIdPosition(groupDetail.getUsers());
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("objectId", groupDetail.getUsers().get(toUserIdPosition));
                query.findInBackground((users, e) -> tvGcName.setText(users.get(0).getString("screenName")));
            }

            //if typing == true: display typingn other wise display message
            tvMessagePreview.setText(groupDetail.getMessage().getMessage_content());
            Date msgDate = new Date(groupDetail.getMessage().getDate_time());
            String[] msgDateStr = msgDate.toString().split(" ");

            tvTimeDateConversation.setText(msgDateStr[1] + " " + msgDateStr[2] + " " + msgDateStr[3]);

            if (groupDetail.getTyping_detail().isTyping()) {
                tvMessagePreview.setVisibility(View.GONE);
                lavTypingIndicatorConversation.setVisibility(View.VISIBLE);
            } else {
                tvMessagePreview.setVisibility(View.VISIBLE);
                lavTypingIndicatorConversation.setVisibility(View.GONE);

            }
        }

        public int toUserIdPosition(List<String> groupUsers) {
            if (groupUsers.indexOf(ParseUser.getCurrentUser().getObjectId()) == 0) {
                return 1;
            } else {
                return 0;
            }
        }


    }
}
