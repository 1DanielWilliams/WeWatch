package com.example.capstone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.models.Conversation;
import com.example.capstone.models.Movie;

import java.util.List;

public class TVshowsAdapter extends RecyclerView.Adapter<TVshowsAdapter.ViewHolder> {
    private Context context;
    private List<Movie> tvShows;

    public TVshowsAdapter(Context context, List<Movie> tvShows) {
        this.context = context;
        this.tvShows = tvShows;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tv_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie tvShow = tvShows.get(position);
        holder.bind(tvShow);
    }

    @Override
    public int getItemCount() {
        return tvShows.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //declare different views of post up here

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // assign views to ids

        }
        public void bind(Movie tvShow) {
            //bind data from event object to different views above

        }
    }
}
