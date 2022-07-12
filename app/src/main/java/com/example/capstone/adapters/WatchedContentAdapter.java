package com.example.capstone.adapters;

import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.models.VideoContent;
import com.parse.ParseException;

import java.util.List;

public class WatchedContentAdapter extends RecyclerView.Adapter<WatchedContentAdapter.ViewHolder> {
    private Context context;
    private List<VideoContent> watchedContent;

    public WatchedContentAdapter(Context context, List<VideoContent> watchedContent) {
        this.context = context;
        this.watchedContent = watchedContent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_watched_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoContent videoContent = null;
        try {
            videoContent = watchedContent.get(position).fetchIfNeeded();
            holder.bind(videoContent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return watchedContent.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTypeOfContentWatched;
        private TextView tvTitleWatched;
        private TextView tvRatingWatched;

        public ViewHolder (@NonNull View itemView) {
            super(itemView);

            tvTypeOfContentWatched = itemView.findViewById(R.id.tvTypeOfContentWatched);
            tvTitleWatched = itemView.findViewById(R.id.tvTitleWatched);
            tvRatingWatched = itemView.findViewById(R.id.tvRatingWatched);
        }

        public void bind(VideoContent videoContent) {
            tvRatingWatched.setText(Double.toString((videoContent.getVoteAverage() / 2.0d)));
            tvTitleWatched.setText(videoContent.getTitle());
            tvTypeOfContentWatched.setText(videoContent.getTypeOfContent());
        }
    }
}
