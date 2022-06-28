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

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private Context context;
    private List<VideoContent> videoContents;

    public MoviesAdapter(Context context, List<VideoContent> posts) {
        this.context = context;
        this.videoContents = posts;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tv_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoContent videoContent = videoContents.get(position);
        holder.bind(videoContent);
    }

    @Override
    public int getItemCount() {
        return videoContents.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //declare different views of post up here

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // assign views to ids

        }
        public void bind(VideoContent videoContent) {
            //bind data from event object to different views above

        }
    }
}
