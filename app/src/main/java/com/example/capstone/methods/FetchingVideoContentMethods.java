package com.example.capstone.methods;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.capstone.R;
import com.example.capstone.activities.MovieSelectionActivity;
import com.example.capstone.adapters.MoviesAdapter;
import com.example.capstone.adapters.TVshowsAdapter;
import com.example.capstone.models.Event;
import com.example.capstone.models.VideoContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Headers;

public class FetchingVideoContentMethods {
    public static final String TOP_RATED_URL = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + MovieSelectionActivity.TMDB_KEY + "&language=en-US&page=";
    public static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + MovieSelectionActivity.TMDB_KEY + "&language=en-US&page=";

    public static void fetchMovies(String URL, List<VideoContent> queriedMovies, List<VideoContent> allMovies, MoviesAdapter adapter, AsyncHttpClient client, int page) {
        URL += String.valueOf(page);
        client.get(URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;

                try {
                    JSONArray results = jsonObject.getJSONArray("results");

                    List<VideoContent> movies = VideoContent.fromJsonArray(results, "Movie");
                    queriedMovies.addAll(movies);
                    allMovies.addAll(movies);
                    int size = queriedMovies.size();
                    adapter.notifyDataSetChanged();
                    for (int i = 0; i < size; i++) {
                        VideoContent movie = queriedMovies.get(i);
                        int id = movie.getTmdbID();
                        String watchProvidersUrl = "https://api.themoviedb.org/3/movie/" + id + "/watch/providers?api_key=" + MovieSelectionActivity.TMDB_KEY;
                        int finalI = i;
                        client.get(watchProvidersUrl, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                JSONObject object = json.jsonObject;
                                List<String> platforms = new ArrayList<>();
                                try {
                                    JSONArray results = object.getJSONObject("results").getJSONObject("US").getJSONArray("flatrate");

                                    for (int i = 0; i < results.length(); i++) {
                                        JSONObject platform = results.getJSONObject(i);
                                        Log.i("VideoContent", "onSuccess: " + platform.get("provider_name").toString());
                                        platforms.add(platform.get("provider_name").toString());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                movie.setPlatforms(platforms);
                                adapter.notifyItemChanged(finalI);
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("MovieSelectionAdctiit", "onFailure: ", throwable);
                            }
                        });
                    }
                } catch (JSONException e) {
                    Log.e("MovieSelectionActivity", "onSuccess: ", e);
                }


            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e("MovieSelectionActivity", "onFailure: ", throwable);
            }
        });

    }


    public static void fetchTvShows(AsyncHttpClient client, String URL, List<VideoContent> queriedTVShows, List<VideoContent> allTVShows, TVshowsAdapter adapter, int page) {
        URL += String.valueOf(page);
        client.get(URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;

                try {
                    JSONArray results = jsonObject.getJSONArray("results");
//                    Log.i("MovieSelectionActivity", "Results: " + results.toString());
                    List<VideoContent> tvShows = VideoContent.fromJsonArray(results, "TV Show");
                    queriedTVShows.addAll(tvShows);
                    allTVShows.addAll(tvShows);
                    int size = queriedTVShows.size();
                    for (int i = 0; i < size; i++) {
                        VideoContent tvShow = queriedTVShows.get(i);
                        int id = tvShow.getTmdbID();
                        String watchProvidersUrl = "https://api.themoviedb.org/3/tv/" + id + "/watch/providers?api_key=" + MovieSelectionActivity.TMDB_KEY;
                        client.get(watchProvidersUrl, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                JSONObject object = json.jsonObject;
                                List<String> platforms = new ArrayList<>();
                                try {
                                    JSONArray results = object.getJSONObject("results").getJSONObject("US").getJSONArray("flatrate");
                                    for (int i = 0; i < results.length(); i++) {
                                        JSONObject platform = results.getJSONObject(i);
//                                        Log.i("VideoContent", "onSuccess: " + platform.get("provider_name").toString());
                                        platforms.add(platform.get("provider_name").toString());
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                tvShow.setPlatforms(platforms);

                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("TVShowselectionActivity", "onFailure: ", throwable);
                            }
                        });
                    }
                } catch (JSONException e) {
                    Log.e("TVShowSeelctionActivity", "onSuccess: ", e);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e("TVShowSelectionActivity", "onFailure: ", throwable);
            }
        });
    }

    public static void setUpFilterMenuMovies(Context context, TextView tvToolBarMovies, List<VideoContent> queriedMovies, List<VideoContent> allMovies, MoviesAdapter adapter, AsyncHttpClient client, TextView tvFilterMovies, AtomicReference<String> currFilter) {
        PopupMenu filterMenu = new PopupMenu(context, tvToolBarMovies, Gravity.CENTER, 0, R.style.PopupMenuMoreCentralized);
        filterMenu.getMenuInflater().inflate(R.menu.popup_menu_movies_filter, filterMenu.getMenu());
        filterMenu.setOnMenuItemClickListener(item -> {
            int itemID = item.getItemId();

            if (itemID == R.id.optionPopularMovies && !Objects.equals(currFilter.get(), MovieSelectionActivity.POPULAR_FILTER)){
                queriedMovies.clear();
                FetchingVideoContentMethods.fetchMovies(MovieSelectionActivity.POPULAR_URL, queriedMovies, allMovies, adapter, client, 1);
                tvFilterMovies.setText("Popular");
                currFilter.set(MovieSelectionActivity.POPULAR_FILTER);
                return true;
            } else if (itemID == R.id.optionTopRatedMovies && !Objects.equals(currFilter.get(), MovieSelectionActivity.TOP_RATED_FILTER)){
                queriedMovies.clear();
                FetchingVideoContentMethods.fetchMovies(TOP_RATED_URL, queriedMovies, allMovies, adapter, client, 1);
                tvFilterMovies.setText("Top Rated");
                currFilter.set(MovieSelectionActivity.TOP_RATED_FILTER);
                return true;
            } else if (itemID == R.id.optionNowPlayingMovies && !Objects.equals(currFilter.get(), MovieSelectionActivity.NOW_PLAYING_FILTER)) {
                queriedMovies.clear();
                FetchingVideoContentMethods.fetchMovies(NOW_PLAYING_URL, queriedMovies, allMovies, adapter, client, 1);
                tvFilterMovies.setText("Now Playing");
                currFilter.set(MovieSelectionActivity.NOW_PLAYING_FILTER);
                return true;
            }

            return false;
        });
        filterMenu.show();
    }

}
