package com.example.capstone.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.models.DateIndex;
import com.example.capstone.models.Event;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DatesAdapter extends RecyclerView.Adapter<DatesAdapter.ViewHolder> {
    private Context context;
    private List<DateIndex> dates;
    private List<ParseUser> authors;
    private List<List<ParseUser>> interestedUsers;
    private List<AtomicBoolean> isInterested;

    public DatesAdapter(Context context, List<DateIndex> dates, List<ParseUser> authors, List<List<ParseUser>> interestedUsers) {
        this.context = context;
        this.dates = dates;
        this.authors = authors;
        this.interestedUsers = interestedUsers;

        int interestedUsersSize = interestedUsers.size();
        this.isInterested =  new ArrayList<>(interestedUsersSize);
        String currObjectID = ParseUser.getCurrentUser().getObjectId();

        for (int i = 0; i < interestedUsersSize; i++) {
            List<ParseUser> parseUsers = interestedUsers.get(i);
            int parseUsersSize = parseUsers.size();
            for (int j = 0; j < parseUsersSize; j++) {

                if (parseUsers.get(j).getObjectId() == currObjectID) {
                    isInterested.set(i, new AtomicBoolean(true));
                    break;
                } else {
                    isInterested.set(i, new AtomicBoolean(false));
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
        String date = dates.get(position).getDate();
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
                //todo: need the events list in the adapter
                if (!isInterested.get(position).get()) {

                } else {

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
                    btnInterestedDates.setBackgroundColor(Color.argb(70, 0, 255, 0));
                } else {
                    btnInterestedDates.setBackgroundColor(Color.argb(0, 0, 255, 0));
                }
        }
    }
}
