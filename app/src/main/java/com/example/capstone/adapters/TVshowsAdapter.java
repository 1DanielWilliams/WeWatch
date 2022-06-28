package com.example.capstone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.models.VideoContent;

import java.util.List;

public class TVshowsAdapter extends RecyclerView.Adapter<TVshowsAdapter.ViewHolder> {
    private Context context;
    private List<VideoContent> tvShows;

    public TVshowsAdapter(Context context, List<VideoContent> tvShows) {
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
        VideoContent tvShow = tvShows.get(position);
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
        public void bind(VideoContent tvShow) {
            //bind data from event object to different views above

        }
    }
}
