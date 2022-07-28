package com.example.capstone.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capstone.R;
import com.example.capstone.fragments.VideoContentDetailFragment;
import com.example.capstone.methods.DisplayPlatforms;
import com.example.capstone.models.VideoContent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TVshowsAdapter extends RecyclerView.Adapter<TVshowsAdapter.ViewHolder> {
    private Context context;
    private List<VideoContent> tvShows;
    private boolean isScrollUp = false;

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
        holder.setAnimation(holder.itemView);
    }

    public void setScrollUp(boolean scrollUp) {
        this.isScrollUp = scrollUp;
    }

    @Override
    public int getItemCount() {
        return tvShows.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitleVideoContent;
        RatingBar rbVoterAverageVideoContent;
        ImageView tvBackdropVideoContent;
        TextView tvAvailableOnContent;
        TextView tvExtraPlatformsContent;
        ImageView ivFirst;
        ImageView ivSecond;





        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rbVoterAverageVideoContent = itemView.findViewById(R.id.rbVoterAverageVideoContent);
            tvTitleVideoContent = itemView.findViewById(R.id.tvTitleVideoContent);
            tvBackdropVideoContent = itemView.findViewById(R.id.tvBackdropVideoContent);
            tvAvailableOnContent = itemView.findViewById(R.id.tvAvailableOnContent);
            tvExtraPlatformsContent = itemView.findViewById(R.id.tvExtraPlatformsContent);
            ivFirst = itemView.findViewById(R.id.ivFirst);
            ivSecond = itemView.findViewById(R.id.ivSecond);

            itemView.setOnClickListener(v -> onViewClicked());

        }

        public void bind(VideoContent tvShow) {
            tvTitleVideoContent.setText(tvShow.getTitle());
            float voteAverage = tvShow.getVoteAverage().floatValue() / 2.0f;
            rbVoterAverageVideoContent.setRating(voteAverage);


            Glide.with(context).load(tvShow.getBackdropUrl()).placeholder(R.drawable.no_image_available).error(R.drawable.no_image_available).into(tvBackdropVideoContent);
            tvBackdropVideoContent.setColorFilter(Color.argb(50, 0, 0 , 0));

            List<String> platforms = tvShow.getPlatforms();
            if (platforms == null) {
                tvAvailableOnContent.setVisibility(View.GONE);
                tvExtraPlatformsContent.setVisibility(View.GONE);
                return;
            }
            int platformSize = platforms.size();

            if (platformSize == 0) {
                tvAvailableOnContent.setVisibility(View.GONE);
                tvExtraPlatformsContent.setVisibility(View.GONE);
            } else {
                int numFeatured = 1;
                for (String platform : platforms) {
                    if (numFeatured == 1) {
                        DisplayPlatforms.displayIcon(ivFirst, platform);
                    } else if (numFeatured == 2) {
                        DisplayPlatforms.displayIcon(ivSecond, platform);

                    } else {
                        break;
                    }
                    numFeatured++;
                }
                int extraPlatforms = platformSize - numFeatured;
                if (extraPlatforms < 1) {
                    tvExtraPlatformsContent.setVisibility(View.GONE);
                } else {
                    tvExtraPlatformsContent.setText("+ " + String.valueOf(extraPlatforms));
                }
                tvAvailableOnContent.setVisibility(View.VISIBLE);
            }

        }

        private void onViewClicked() {
            int position = getBindingAdapterPosition();
            // Retrieve the local videoContent object
            AtomicReference<VideoContent> videoContent = new AtomicReference<>(tvShows.get(position));
            ParseQuery<VideoContent> query = ParseQuery.getQuery(VideoContent.class);
            query.whereEqualTo("title", videoContent.get().getTitle());
            query.whereEqualTo("typeOfContent", videoContent.get().getTypeOfContent());

            // Search the parse database to see if this movie already exist so that the program will not save two versions of the same VideoContent
            query.findInBackground((shows, e) -> {
                if(shows.size() !=0) {
                    videoContent.set(shows.get(0));
                }

                //get the title of the video content
                FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
                VideoContentDetailFragment videoContentDetailFragment = VideoContentDetailFragment.newInstance(videoContent.get());
                videoContentDetailFragment.show(fm, "fragment_edit_name");
            });

        }

        private void setAnimation(View viewToAnimate) {
            Animation animation;
            if (isScrollUp) {
                animation = AnimationUtils.loadAnimation(context, R.transition.slide_in_right);
            } else {
                animation = AnimationUtils.loadAnimation(context, R.transition.slide_in_left);
            }
            viewToAnimate.startAnimation(animation);
        }
    }
}
