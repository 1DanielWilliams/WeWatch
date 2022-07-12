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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.capstone.R;
import com.example.capstone.activities.MovieSelectionActivity;
import com.example.capstone.fragments.VideoContentDetailFragment;
import com.example.capstone.methods.DisplayPlatforms;
import com.example.capstone.models.VideoContent;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

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
        ImageView ivFirst;
        ImageView ivSecond;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rbVoterAverageVideoContent = itemView.findViewById(R.id.rbVoterAverageVideoContent);
            tvTitleVideoContent = itemView.findViewById(R.id.tvTitleVideoContent);
            tvBackdropVideoContent = itemView.findViewById(R.id.tvBackdropVideoContent);
            ivFirst = itemView.findViewById(R.id.ivFirst);
            ivSecond = itemView.findViewById(R.id.ivSecond);
            itemView.setOnClickListener(v -> onViewClicked() );

        }
        public void bind(VideoContent movie) {

            tvTitleVideoContent.setText(movie.getTitle());
            float voteAverage = movie.getVoteAverage().floatValue() / 2.0f;
            rbVoterAverageVideoContent.setRating(voteAverage);
            List<String> platforms = movie.getPlatforms();
//            AsyncHttpClient client = new AsyncHttpClient();
//            List<String> platforms = new ArrayList<>();
//            int id = movie.getTmdbID();
//            String watchProvidersUrl = "https://api.themoviedb.org/3/movie/" + id + "/watch/providers?api_key=" + MovieSelectionActivity.TMDB_KEY;
//            client.get(watchProvidersUrl, new JsonHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Headers headers, JSON json) {
//                    JSONObject object = json.jsonObject;
//                    try {
//                        JSONArray results = object.getJSONObject("results").getJSONObject("US").getJSONArray("flatrate");
//                        for (int i = 0; i < results.length(); i++) {
//                            JSONObject platform = results.getJSONObject(i);
//                            Log.i("VideoContent", "onSuccess: " + platform.get("provider_name").toString());
//                            platforms.add(platform.get("provider_name").toString());
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    movie.setPlatforms(platforms);
//
//                    int numFeatured = 1;
//                    for (String platform : platforms) {
//                        if (numFeatured == 1) {
//                            DisplayPlatforms.displayIcon(ivFirst, platform);
//                        } else if (numFeatured == 2) {
//                            DisplayPlatforms.displayIcon(ivSecond, platform);
//
//                        } else if (numFeatured == 3) {
////                    DisplayPlatforms.displayIcon(ivFirst, platform); w third iv
//                        } else {
//                            break;
//                        }
//                        numFeatured++;
//                    }
//
//                }
//
//                @Override
//                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//
//                }
//            });
            if (platforms == null) {
                Log.i("Movies", "bind: " + getBindingAdapterPosition());
            }
            int numFeatured = 1;
            for (String platform : platforms) {
                if (numFeatured == 1) {
                    DisplayPlatforms.displayIcon(ivFirst, platform);
                } else if (numFeatured == 2) {
                    DisplayPlatforms.displayIcon(ivSecond, platform);

                } else if (numFeatured == 3) {
//                    DisplayPlatforms.displayIcon(ivFirst, platform); w third iv
                } else {
                    break;
                }
                numFeatured++;
            }


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
