package com.example.capstone.adapters;

import android.content.Context;
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

import java.util.List;

public class DatesAdapter extends RecyclerView.Adapter<DatesAdapter.ViewHolder> {
    private Context context;
    private List<String> dates;
    private List<ParseUser> authors;
    private List<List<ParseUser>> interestedUsers;

    public DatesAdapter(Context context, List<String> dates, List<ParseUser> authors, List<List<ParseUser>> interestedUsers) {
        this.context = context;
        this.dates = dates;
        this.authors = authors;
        this.interestedUsers = interestedUsers;
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
        holder.bind(date, username, screenName, numInterested);
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
        }

        public void bind(String date, String username, String screenName, int numInterested) {
                //bind data to views
                tvDateDates.setText(date);
                tvScreenNameDates.setText(screenName);
                tvUsernameDates.setText("@" + username);
                tvNumInterestedDates.setText(String.valueOf(numInterested) + " attending");
        }
    }
}
