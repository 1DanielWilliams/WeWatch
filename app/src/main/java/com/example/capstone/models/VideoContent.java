package com.example.capstone.models;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.capstone.activities.MovieSelectionActivity;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Headers;

@ParseClassName("VideoContent")
public class VideoContent extends ParseObject {

    public static final String KEY_TITLE = "title";
    public static final String KEY_TYPE_OF_CONTENT = "typeOfContent";
    public static final String KEY_VOTE_AVERAGE = "voteAverage";
    public static final String KEY_OVERVIEW = "overview";
    public static final String KEY_PLATFORMS = "platforms";
    public static final String KEY_NUM_POSTED = "numPosted";
    public static final String KEY_NUM_INTERESTED_IN = "numInterestedIn";
    public static final String KEY_POSTER_URL = "posterUrl";
    public static final String KEY_BACKDROP_URL = "backdropUrl";
    public static final String KEY_TMDB_ID = "tmdbID";

    public VideoContent() {}

    public void setTitle(String title) { put(KEY_TITLE, title); }
    public String getTitle() {return  getString(KEY_TITLE); }

    public void setTypeOfContent(String typeOfContent) { put(KEY_TYPE_OF_CONTENT, typeOfContent);}
    public String getTypeOfContent() {return getString(KEY_TYPE_OF_CONTENT); }

    public void setVoteAverage(Double voteAverage) {put(KEY_VOTE_AVERAGE, voteAverage); }
    public Double getVoteAverage() {return getDouble(KEY_VOTE_AVERAGE); }

    public void setOverview(String overview) {put(KEY_OVERVIEW, overview); }
    public String getOverview() {return getString(KEY_OVERVIEW); }

    public void setTmdbID(int tmdbID) { put(KEY_TMDB_ID, tmdbID); }
    public int getTmdbID() {return getInt(KEY_TMDB_ID); }

    public void setPlatforms(List<String> platforms) { put(KEY_PLATFORMS, platforms); }
    public List<String> getPlatforms() {return getList(KEY_PLATFORMS); }

    public void setNumPosted(int numPosted) {put(KEY_NUM_POSTED, numPosted); }
    public int getNumPosted() {return getInt(KEY_NUM_POSTED); }

    public void setNumInterestedIn(int numInterestedIn) {put(KEY_NUM_INTERESTED_IN, numInterestedIn); }
    public int getNumInterestedIn() {return getInt(KEY_NUM_INTERESTED_IN); }

    public void setPosterUrl(String posterUrl) { put(KEY_POSTER_URL, posterUrl); }
    public String getPosterUrl() {return  getString(KEY_POSTER_URL); }

    public void setBackdropUrl(String backdropUrl) { put(KEY_BACKDROP_URL, backdropUrl); }
    public String getBackdropUrl() {return  getString(KEY_BACKDROP_URL); }

    public VideoContent(JSONObject jsonObject, String typeOfContent) throws JSONException {
        if (Objects.equals(typeOfContent, "Movie")) {
            setTitle(jsonObject.getString("title"));
        } else if (Objects.equals(typeOfContent, "TV Show")){
            setTitle(jsonObject.getString("name"));
        }
        int id = jsonObject.getInt("id");
        setTmdbID(id);
        setVoteAverage(jsonObject.getDouble("vote_average"));
        setOverview(jsonObject.getString("overview"));
        setTypeOfContent(typeOfContent);
        setNumPosted(1);
        setNumInterestedIn(1);

        setPosterUrl(String.format("https://image.tmdb.org/t/p/w342/%s", jsonObject.getString("poster_path")));
        setBackdropUrl(String.format("https://image.tmdb.org/t/p/w342/%s", jsonObject.getString("backdrop_path")));

        //Adds to the platform array
        AsyncHttpClient client = new AsyncHttpClient();
//        if(Objects.equals(typeOfContent, "Movie")) {
//            String watchProvidersUrl = "https://api.themoviedb.org/3/movie/" + id + "/watch/providers?api_key=" + MovieSelectionActivity.TMDB_KEY;
//            client.get(watchProvidersUrl, new JsonHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Headers headers, JSON json) {
//                    JSONObject object = json.jsonObject;
//                    List<String> platforms = new ArrayList<>();
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
//                    setPlatforms(platforms);
//                }
//
//                @Override
//                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//
//                }
//            });
//        } else if (Objects.equals(typeOfContent, "TV Show")) {
//            String watchProvidersUrl = "https://api.themoviedb.org/3/tv/" + id + "/watch/providers?api_key=" + MovieSelectionActivity.TMDB_KEY;
//            client.get(watchProvidersUrl, new JsonHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Headers headers, JSON json) {
//                    JSONObject object = json.jsonObject;
//                    List<String> platforms = new ArrayList<>();
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
//                    setPlatforms(platforms);
//                }
//
//                @Override
//                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//
//                }
//            });
//        }

    }

    public static List<VideoContent> fromJsonArray(JSONArray videContentJsonArray, String typeOfContent) throws JSONException {
        List<VideoContent> videoContents = new ArrayList<>();
        for (int i = 0; i < videContentJsonArray.length(); i++) {
            VideoContent videoContent = new VideoContent(videContentJsonArray.getJSONObject(i), typeOfContent);
            videoContents.add(videoContent);
        }
        return videoContents;
    }

}
