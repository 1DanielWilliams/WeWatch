package com.example.capstone.methods;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
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

import okhttp3.Headers;

public class FetchingVideoContentMethods {
    public static void fetchMovies(String POPULAR_URL, List<VideoContent> queriedMovies, List<VideoContent> allMovies, MoviesAdapter adapter, AsyncHttpClient client, int page) {
        POPULAR_URL += String.valueOf(page);
        client.get(POPULAR_URL, new JsonHttpResponseHandler() {
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


    public static void fetchTvShows(AsyncHttpClient client, String POPULAR_URL, List<VideoContent> queriedTVShows, List<VideoContent> allTVShows, TVshowsAdapter adapter, int page) {
        POPULAR_URL += String.valueOf(page);
        client.get(POPULAR_URL, new JsonHttpResponseHandler() {
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

}
