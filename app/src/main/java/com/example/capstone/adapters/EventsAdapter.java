package com.example.capstone.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capstone.R;
import com.example.capstone.activities.feed.DetailEventActivity;
import com.example.capstone.activities.feed.FeedActivity;
import com.example.capstone.fragments.OtherDatesFragment;
import com.example.capstone.methods.BinarySearch;
import com.example.capstone.methods.DisplayPlatforms;
import com.example.capstone.methods.NavigationMethods;
import com.example.capstone.models.Event;
import com.example.capstone.models.VideoContent;
import com.google.android.material.button.MaterialButton;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.livequery.ParseLiveQueryClient;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private Context context;
    private List<Event> events;
    private ParseLiveQueryClient parseLiveQueryClient;
    private final ColorStateList WHITE = ColorStateList.valueOf(Color.argb(255, 255, 255, 255));
    private final ColorStateList BLACK = ColorStateList.valueOf(Color.argb(255, 0, 0, 0));
    private final ColorStateList RED = ColorStateList.valueOf(Color.argb(255, 214, 41, 41));
    private boolean isScrollUp = false;



    public EventsAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
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

        holder.setAnimation(holder.itemView);
    }

    public void setScrollUp(boolean scrollUp) {
        this.isScrollUp = scrollUp;
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public int itemCreated(Event createdEvent) {
        //take events list and binary search it to add this thing there
        int indexToAdd = BinarySearch.indexOfEvents(events, createdEvent.getEarliestDate());
        events.add(indexToAdd, createdEvent);
        notifyItemInserted(indexToAdd);

        return indexToAdd;
    }

    public Pair<Integer, Event> findEventIndex(Event modifiedEvent) {
        int eventPosition = -1;
        int eventSize = events.size();
        Event oldEvent = null;

        //finds the event in the list of events
        for (int eventIndex = 0; eventIndex < eventSize; eventIndex++) {
            Log.i("EventsAdapter", "findEventIndex: " + events.get(eventIndex).getObjectId() + " Static: " + modifiedEvent.getObjectId());
            if (Objects.equals(events.get(eventIndex).getObjectId(), modifiedEvent.getObjectId())) {
                eventPosition = eventIndex;
                oldEvent = events.get(eventIndex);
                break;
            }
        }

        return new Pair<>(eventPosition, oldEvent);
    }

    public void itemUpdated(Event updatedEvent) {
        Pair<Integer, Event> eventData = findEventIndex(updatedEvent);

        List<String> updatedEventDates = updatedEvent.getDates();
        int oldEventDatesSize = eventData.second.getDates().size();
        int updatedEventDateSize = updatedEventDates.size();

        // if the earliest date has changed
        if (!updatedEvent.getEarliestDate().toInstant().equals(events.get(eventData.first).getEarliestDate().toInstant())) {
            events.remove((int) eventData.first);
            notifyItemRemoved((int) eventData.first);
            int updatedEventIndex = BinarySearch.indexOfEvents(events, updatedEvent.getEarliestDate());
            events.add(updatedEventIndex, updatedEvent);
            notifyItemInserted(updatedEventIndex);
            Toast.makeText(context, "New Date added for " + updatedEvent.getTitle(), Toast.LENGTH_SHORT).show();
            updatedEvent.setIsNewDate(true);
            return;
        }
            //find out where to place new event
            //delete old event
        if (updatedEventDateSize > oldEventDatesSize) {
            //check if the earliest date has changed
            Toast.makeText(context, "New Date added for " + updatedEvent.getTitle(), Toast.LENGTH_SHORT).show();
            //set a flag that means that a new date was added
            updatedEvent.setIsNewDate(true);

        } else if (updatedEventDateSize < oldEventDatesSize){
            //a date was removed
            updatedEvent.setIsRemovedDate(true);
        }

        events.set(eventData.first, updatedEvent);
        notifyItemChanged(eventData.first);
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
        MaterialButton btnDate;
        Button btnLive;
        ImageView ivFirstPlatformEvent;
        ImageView ivSecondPlatformEvent;
        ImageView ivThirdPlatformEvent;
        ImageView ivFourthPlatformEvent;
        TextView tvNumPlatformsLeft;
        TextView tvAvailableOnEvent;
        TextView tvNewDate;
        TextView tvRemovedDate;


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
            tvNewDate = itemView.findViewById(R.id.tvNewDate);
            tvRemovedDate = itemView.findViewById(R.id.tvRemovedDate);

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

            tvUsername.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                NavigationMethods.navToProfile(context, events.get(position).getUsers().get(0).getObjectId());
            });

            tvScreenName.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                NavigationMethods.navToProfile(context, events.get(position).getUsers().get(0).getObjectId());
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
                tvAvailableOnEvent.setText("Available On:");
            } else {
                tvAvailableOnEvent.setText("");
                tvNumPlatformsLeft.setText("");
            }


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

                btnDate.setText(event.getDates().get(0));
            }

            Date currDate = new Date(System.currentTimeMillis());
            // if the event has a new date and the event has been updated within the last 20 seconds
            if(event.getIsNewDate() && currDate.toInstant().isBefore(event.getUpdatedAt().toInstant().plus(Duration.ofSeconds(20)))) {
                tvNewDate.setVisibility(View.VISIBLE);
                btnDate.setStrokeColor(WHITE);
                event.setIsNewDate(false);
            } else if (!event.getIsNewDate()){
                tvNewDate.setVisibility(View.GONE);
                btnDate.setStrokeColor(BLACK);
            } // if an event's date was removed and the event has been updated within the last 20 seconds
            else if(event.getIsRemovedDate() && currDate.toInstant().isBefore(event.getUpdatedAt().toInstant().plus(Duration.ofSeconds(20)))) {
                tvRemovedDate.setVisibility(View.VISIBLE);
                btnDate.setStrokeColor(RED);
                event.setIsRemovedDate(false);
            } else if (!event.getIsRemovedDate()){
                tvRemovedDate.setVisibility(View.GONE);
                btnDate.setStrokeColor(BLACK);
            }
        }

        private void setAnimation(View viewToAnimate) {
            Animation animation;
            if (isScrollUp) {
                animation = AnimationUtils.loadAnimation(context, R.transition.slide_in_right);
            } else {
                animation = AnimationUtils.loadAnimation(context, R.transition.slide_in_left);
            }
            viewToAnimate.startAnimation(animation);
        }
    }
}
