package com.example.capstone.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capstone.R;
import com.example.capstone.activities.DetailEventActivity;
import com.example.capstone.fragments.OtherDatesFragment;
import com.example.capstone.methods.DisplayPlatforms;
import com.example.capstone.models.DateIndex;
import com.example.capstone.models.Event;
import com.example.capstone.models.VideoContent;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private Context context;
    private List<Event> events;

    public EventsAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        try {
            holder.bind(event);
        } catch (ParseException e) {
            Log.e("EventsAdapter", "onBindViewHolder: ", e);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //declare different views of post up here

        TextView tvScreenName;
        TextView tvUsername;
        TextView tvTitleEvent;
        TextView tvTypeOfContent;
        TextView tvNumInterested;
        TextView tvOtherDates;
        ImageView ivBackdropEvent;
        Button btnDate;
        Button btnLive;
        ImageView ivFirstPlatformEvent;
        ImageView ivSecondPlatformEvent;
        ImageView ivThirdPlatformEvent;
        ImageView ivFourthPlatformEvent;
        TextView tvNumPlatformsLeft;
        TextView tvAvailableOnEvent;



        public ViewHolder (@NonNull View itemView) {
            super(itemView);

            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTitleEvent = itemView.findViewById(R.id.tvTitleEvent);
            tvTypeOfContent = itemView.findViewById(R.id.tvTypeOfContent);
            tvNumInterested = itemView.findViewById(R.id.tvNumInterested);
            ivBackdropEvent = itemView.findViewById(R.id.ivBackdropEvent);
            btnDate = itemView.findViewById(R.id.btnDate);
            btnLive = itemView.findViewById(R.id.btnLive);
            tvOtherDates = itemView.findViewById(R.id.tvOtherDates);
            ivFirstPlatformEvent = itemView.findViewById(R.id.ivFirstPlatformEvent);
            ivSecondPlatformEvent = itemView.findViewById(R.id.ivSecondPlatformEvent);
            ivThirdPlatformEvent = itemView.findViewById(R.id.ivThirdPlatformEvent);
            ivFourthPlatformEvent = itemView.findViewById(R.id.ivFourthPlatformEvent);
            tvNumPlatformsLeft = itemView.findViewById(R.id.tvNumPlatformsLeft);
            tvAvailableOnEvent = itemView.findViewById(R.id.tvAvailableOnEvent);


            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                Intent i = new Intent(context, DetailEventActivity.class);
                i.putExtra("event", events.get(position));
                context.startActivity(i);
            });

            btnDate.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
                OtherDatesFragment otherDatesFragment = OtherDatesFragment.newInstance(events.get(position));
                otherDatesFragment.show(fm, "other_dates");
            });


        }

        public void bind(Event event) throws ParseException {
            ParseUser user = event.getUsers().get(event.getEarliestUserIndex()).fetch();
            tvScreenName.setText(user.getString("screenName"));
            tvUsername.setText("@" + user.getUsername());
            tvTypeOfContent.setText(event.getTypeOfContent());
            tvTitleEvent.setText(event.getTitle());
            tvNumInterested.setText(String.valueOf(event.getInterestedUsers().get(event.getEarliestUserIndex()).size()) + " attending");

            VideoContent videoContent = event.getVideContent().fetchIfNeeded();
            List<String> platforms = videoContent.getPlatforms();
            int platformSize = platforms.size();
            if (platformSize > 0) {
                int numFeatured = 1;
                for (String platform : platforms) {
                    if (numFeatured == 1) {
                        DisplayPlatforms.displayIcon(ivFirstPlatformEvent, platform);
                    } else if (numFeatured == 2) {
                        DisplayPlatforms.displayIcon(ivSecondPlatformEvent, platform);
                    } else if (numFeatured == 3) {
                        DisplayPlatforms.displayIcon(ivThirdPlatformEvent, platform);
                    } else if (numFeatured == 4) {
                        DisplayPlatforms.displayIcon(ivFourthPlatformEvent, platform);
                    } else {
                        break;
                    }
                    numFeatured++;
                }
                int numPlatformsLeft = platforms.size() - numFeatured;
                if (numPlatformsLeft < 1) {
                    tvNumPlatformsLeft.setText("");
                } else {
                    tvNumPlatformsLeft.setText("+ " + numPlatformsLeft);
                }
            } else {
                tvAvailableOnEvent.setText("");
                tvNumPlatformsLeft.setText("");
            }




            // todo: needs placeholder
            Glide.with(context).load(event.getBackdropUrl()).into(ivBackdropEvent);
            ivBackdropEvent.setColorFilter(Color.argb(60, 0 , 0 , 0));
            if (event.getIsLive()) {
                btnLive.setVisibility(View.VISIBLE);
                btnDate.setVisibility(View.GONE);
            } else {
                btnLive.setVisibility((View.GONE));
                btnDate.setVisibility(View.VISIBLE);
                int numOtherDates = event.getDates().size() - 1;

                if (numOtherDates > 1) {
                    tvOtherDates.setVisibility(View.VISIBLE);
                    tvOtherDates.setText(String.valueOf(numOtherDates) + " other dates");
                } else if (numOtherDates == 1) {
                    tvOtherDates.setVisibility(View.VISIBLE);
                    tvOtherDates.setText(String.valueOf(numOtherDates) + " other date");
                }

                DateIndex dateIndex = event.getDates().get(0).fetchIfNeeded();
                btnDate.setText(dateIndex.getDate());
            }
        }
    }
}
