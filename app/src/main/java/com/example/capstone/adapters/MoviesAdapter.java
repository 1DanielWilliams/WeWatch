package com.example.capstone.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capstone.R;
import com.example.capstone.models.VideoContent;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private Context context;
    private List<VideoContent> videoContents;

    public MoviesAdapter(Context context, List<VideoContent> videoContents) {
        this.context = context;
        this.videoContents = videoContents;
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

        TextView tvTitleVideoContent;
        RatingBar rbVoterAverageVideoContent;
//        TextView tvOverviewVideoContent;
        ImageView tvBackdropVideoContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // assign views to ids
//            tvOverviewVideoContent = itemView.findViewById(R.id.tvOverviewVideoContent);
            rbVoterAverageVideoContent = itemView.findViewById(R.id.rbVoterAverageVideoContent);
            tvTitleVideoContent = itemView.findViewById(R.id.tvTitleVideoContent);
            tvBackdropVideoContent = itemView.findViewById(R.id.tvBackdropVideoContent);

        }
        public void bind(VideoContent videoContent) {
            //bind data from event object to different views above
            tvTitleVideoContent.setText(videoContent.getTitle());
            float voteAverage = videoContent.getVoteAverage().floatValue() / 2.0f;
            Log.i("adapter", "bind: " + videoContent.getTitle() + " " + voteAverage);
            // todo: rating bar will not show correct number
            rbVoterAverageVideoContent.setRating(voteAverage);
//            tvOverviewVideoContent.setText(videoContent.getOverview());

            // todo: have placeholder if it goes wrong
            Glide.with(context).load(videoContent.getBackdropUrl()).into(tvBackdropVideoContent);
            tvBackdropVideoContent.setColorFilter(Color.argb(50, 0, 0 , 0));

        }
    }
}
