package com.example.capstone.adapters;

import android.app.Activity;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capstone.R;
import com.example.capstone.fragments.VideoContentDetailFragment;
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
        ImageView tvBackdropVideoContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // assign views to ids
            rbVoterAverageVideoContent = itemView.findViewById(R.id.rbVoterAverageVideoContent);
            tvTitleVideoContent = itemView.findViewById(R.id.tvTitleVideoContent);
            tvBackdropVideoContent = itemView.findViewById(R.id.tvBackdropVideoContent);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    VideoContent videoContent = videoContents.get(position);
                    FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
                    VideoContentDetailFragment videoContentDetailFragment = VideoContentDetailFragment.newInstance(videoContent);
                    videoContentDetailFragment.show(fm, "fragment_edit_name");

                }
            });

        }
        public void bind(VideoContent videoContent) {
            //bind data from event object to different views above
            tvTitleVideoContent.setText(videoContent.getTitle());
            float voteAverage = videoContent.getVoteAverage().floatValue() / 2.0f;
            Log.i("adapter", "bind: " + videoContent.getTitle() + " " + voteAverage);
            // todo: rating bar will not show correct number
            rbVoterAverageVideoContent.setRating(voteAverage);

            // todo: have placeholder if it goes wrong
            Glide.with(context).load(videoContent.getBackdropUrl()).into(tvBackdropVideoContent);
            tvBackdropVideoContent.setColorFilter(Color.argb(50, 0, 0 , 0));

        }
    }
}
