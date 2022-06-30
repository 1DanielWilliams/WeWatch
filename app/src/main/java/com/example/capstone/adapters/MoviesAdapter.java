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
import com.example.capstone.models.Event;
import com.example.capstone.models.VideoContent;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private Context context;
    private List<VideoContent> movies;

    public MoviesAdapter(Context context, List<VideoContent> videoContents) {
        this.context = context;
        this.movies = videoContents;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tv_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoContent movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitleVideoContent;
        RatingBar rbVoterAverageVideoContent;
        ImageView tvBackdropVideoContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rbVoterAverageVideoContent = itemView.findViewById(R.id.rbVoterAverageVideoContent);
            tvTitleVideoContent = itemView.findViewById(R.id.tvTitleVideoContent);
            tvBackdropVideoContent = itemView.findViewById(R.id.tvBackdropVideoContent);

            itemView.setOnClickListener(v -> onViewClicked() );

        }
        public void bind(VideoContent movie) {

            tvTitleVideoContent.setText(movie.getTitle());
            float voteAverage = movie.getVoteAverage().floatValue() / 2.0f;
            rbVoterAverageVideoContent.setRating(voteAverage);

            // todo: have placeholder if it goes wrong
            Glide.with(context).load(movie.getBackdropUrl()).into(tvBackdropVideoContent);
            tvBackdropVideoContent.setColorFilter(Color.argb(50, 0, 0 , 0));

        }

        private void onViewClicked() {
            int position = getBindingAdapterPosition();
            // Retrieve the local videoContent object
            VideoContent videoContent = movies.get(position);
            ParseQuery<VideoContent> query = ParseQuery.getQuery(VideoContent.class);
            query.whereEqualTo("title", videoContent.getTitle());
            query.whereEqualTo("typeOfContent", videoContent.getTypeOfContent());

            // Search the parse database to see if this movie already exist so that the program will not save two versions of the same VideoContent
            try {
                List<VideoContent> videoContents = query.find();
                if (videoContents.size() != 0) {
                    videoContent = videoContents.get(0);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //get the title of the video content
            FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
            VideoContentDetailFragment videoContentDetailFragment = VideoContentDetailFragment.newInstance(videoContent);
            videoContentDetailFragment.show(fm, "fragment_edit_name");
        }
    }
}
