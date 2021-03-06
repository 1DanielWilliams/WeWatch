package com.example.capstone.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.models.Event;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DatesAdapter extends RecyclerView.Adapter<DatesAdapter.ViewHolder> {
    private Context context;
    private List<String> dates;
    private List<ParseUser> authors;
    private List<List<ParseUser>> interestedUsers;
    private List<AtomicBoolean> isInterested;
    private Event event;

    public DatesAdapter(Context context, List<String> dates, List<ParseUser> authors, List<List<ParseUser>> interestedUsers, Event event) {
        this.context = context;
        this.dates = dates;
        this.authors = authors;
        this.interestedUsers = interestedUsers;
        this.event = event;

        int interestedUsersSize = interestedUsers.size();
        this.isInterested =  new ArrayList<>();
        String currObjectID = ParseUser.getCurrentUser().getObjectId();

        for (int i = 0; i < interestedUsersSize; i++) {
            List<ParseUser> parseUsers = interestedUsers.get(i);
            int parseUsersSize = parseUsers.size();
            for (int j = 0; j < parseUsersSize; j++) {

                if (parseUsers.get(j).getObjectId() == currObjectID) {
                    isInterested.add(i, new AtomicBoolean(true));
                    break;
                } else {
                    isInterested.add(i, new AtomicBoolean(false));
                }
            }
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_date, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String date = dates.get(position);
        String username = null;
        String screenName = null;
        try {
            username = authors.get(position).fetchIfNeeded().getUsername();
            screenName = authors.get(position).fetchIfNeeded().getString("screenName");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int numInterested = interestedUsers.get(position).size();
        holder.bind(date, username, screenName, numInterested, isInterested.get(position).get());
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //declare views up here
        private TextView tvScreenNameDates;
        private TextView tvUsernameDates;
        private TextView tvDateDates;
        private TextView tvNumInterestedDates;
        private Button btnInterestedDates;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //assign views to ids
            tvScreenNameDates = itemView.findViewById(R.id.tvScreenNameDates);
            tvUsernameDates = itemView.findViewById(R.id.tvUsernameDates);
            tvDateDates = itemView.findViewById(R.id.tvDateDates);
            tvNumInterestedDates = itemView.findViewById(R.id.tvNumInterestedDates);
            btnInterestedDates = itemView.findViewById(R.id.btnInterestedDates);

            btnInterestedDates.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (!isInterested.get(position).get()) {
                    //want to add this user to the interested users at this position:
                    List<List<ParseUser>> interestedUsers = event.getInterestedUsers();
                    List<ParseUser> interestedUsersTime = interestedUsers.get(position);
                    interestedUsersTime.add(ParseUser.getCurrentUser());
                    event.setInterestedUsers(interestedUsers);
                    event.saveInBackground();

                    isInterested.get(position).set(true);
                    notifyItemChanged(position);
                } else {
                    List<List<ParseUser>> interestedUsers = event.getInterestedUsers();
                    List<ParseUser> interestedUsersTime = interestedUsers.get(position);
                    interestedUsersTime.remove(ParseUser.getCurrentUser());
                    event.setInterestedUsers(interestedUsers);
                    event.saveInBackground();

                    isInterested.get(position).set(false);
                    notifyItemChanged(position);
                }
            });
        }

        public void bind(String date, String username, String screenName, int numInterested, boolean isInterestedInEvent) {
                //bind data to views
                tvDateDates.setText(date);
                tvScreenNameDates.setText(screenName);
                tvUsernameDates.setText("@" + username);
                tvNumInterestedDates.setText(String.valueOf(numInterested) + " attending");

                if (isInterestedInEvent) {
                    btnInterestedDates.setBackgroundColor(Color.argb(80, 0, 255, 0));
                } else {
                    btnInterestedDates.setBackgroundColor(Color.argb(0, 0, 255, 0));
                }
        }
    }
}
