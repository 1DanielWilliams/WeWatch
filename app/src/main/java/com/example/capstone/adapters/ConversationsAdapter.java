package com.example.capstone.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.activities.ConversationDetailActivity;
import com.example.capstone.models.GroupDetail;

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvGcName = itemView.findViewById(R.id.tvGcName);
            tvMessagePreview = itemView.findViewById(R.id.tvMessagePreview);
            tvTimeDateConversation = itemView.findViewById(R.id.tvTimeDateConversation);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                Intent i = new Intent(context, ConversationDetailActivity.class);
                i.putExtra("group_id", groupDetails.get(position).getId());
                context.startActivity(i);
            });


        }
        public void bind(GroupDetail groupDetail) {
            String[] nameTime = groupDetail.getName().split("@");

            String name =nameTime[0];
            if (name.length() > 25) {
                name = name.substring(0, 25) + "... @" + nameTime[1];
            } else {
                name += "@" + nameTime[1];
            }
            tvGcName.setText(name);
            tvMessagePreview.setText(groupDetail.getMessage().getMessage_content());
            Date msgDate = new Date(groupDetail.getMessage().getDate_time());
            String[] msgDateStr = msgDate.toString().split(" ");

            tvTimeDateConversation.setText(msgDateStr[1] + " " + msgDateStr[2] + " " + msgDateStr[3]);
        }
    }
}
