package com.example.capstone.methods;

import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.SearchView;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.capstone.adapters.MoviesAdapter;
import com.example.capstone.adapters.TVshowsAdapter;
import com.example.capstone.models.VideoContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class SearchVideoContentMethods {
    public static void setUpSearchView(SearchView searchView, List<VideoContent> queriedContent, List<VideoContent> allContent, TextView tvToolBarTitle, AsyncHttpClient client, MoviesAdapter moviesAdapter, TVshowsAdapter tVshowsAdapter) {
        if (moviesAdapter != null) {
            setUpSearchViewMovies(searchView, queriedContent, allContent, tvToolBarTitle, client, moviesAdapter);
        } else {
            setUpSearchViewTVShows(searchView, queriedContent, allContent, tvToolBarTitle, client, tVshowsAdapter);
        }

    }


    private static void setUpSearchViewMovies(SearchView searchView, List<VideoContent> queriedContent, List<VideoContent> allContent, TextView tvToolBarTitle, AsyncHttpClient client, MoviesAdapter adapter) {
        searchView.setQueryHint("Hit enter to search");
        searchView.setOnSearchClickListener(v -> {
            queriedContent.clear();
            adapter.notifyDataSetChanged();
            tvToolBarTitle.setVisibility(View.GONE);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String searchMovie ="https://api.themoviedb.org/3/search/movie?api_key=61dda6141b919bc26c4c8a5d43de0b7e&language=en-US&query=" + query + "&page=1&include_adult=false";
                client.get(searchMovie, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        JSONObject jsonObject = json.jsonObject;
                        queriedContent.clear();
                        try {
                            JSONArray results = jsonObject.getJSONArray("results");
                            Log.i("TVShowSelctionActivity", "onSuccess: " + results.toString());
                            List<VideoContent> tvShows = VideoContent.fromJsonArray(results, "Movie");
                            for (VideoContent tvShow: tvShows) {
                                //todo add platforms to videocontent
                                tvShow.setPlatforms(new ArrayList<>());
                            }
                            queriedContent.addAll(tvShows);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e("SearchVideContentMethods", "onFailure: ", throwable);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    queriedContent.clear();
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            tvToolBarTitle.setVisibility(View.VISIBLE);
            queriedContent.addAll(allContent);
            adapter.notifyDataSetChanged();
            return false;
        });
    }

    private static void setUpSearchViewTVShows(SearchView searchView, List<VideoContent> queriedContent, List<VideoContent> allContent, TextView tvToolBarTitle, AsyncHttpClient client, TVshowsAdapter adapter) {
        searchView.setQueryHint("Hit enter to search");
        searchView.setOnSearchClickListener(v -> {
            queriedContent.clear();
            adapter.notifyDataSetChanged();
            tvToolBarTitle.setVisibility(View.GONE);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String searchTVShow ="https://api.themoviedb.org/3/search/tv?api_key=61dda6141b919bc26c4c8a5d43de0b7e&language=en-US&query=" + query + "&page=1&include_adult=false";
                client.get(searchTVShow, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        JSONObject jsonObject = json.jsonObject;
                        queriedContent.clear();
                        try {
                            JSONArray results = jsonObject.getJSONArray("results");
                            Log.i("SearchVideContentMethods", "onSuccess: " + results.toString());
                            List<VideoContent> tvShows = VideoContent.fromJsonArray(results, "TV Show");
                            for (VideoContent tvShow: tvShows) {
                                //todo add platforms to videocontent
                                tvShow.setPlatforms(new ArrayList<>());
                            }
                            queriedContent.addAll(tvShows);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e("SearchVideContentMethods", "onFailure: ", throwable);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    queriedContent.clear();
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            tvToolBarTitle.setVisibility(View.VISIBLE);
            queriedContent.addAll(allContent);
            adapter.notifyDataSetChanged();
            return false;
        });
    }
}
