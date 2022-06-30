package com.example.capstone.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public VideoContent() {}

    public void setTitle(String title) { put(KEY_TITLE, title); }
    public String getTitle() {return  getString(KEY_TITLE); }

    public void setTypeOfContent(String typeOfContent) { put(KEY_TYPE_OF_CONTENT, typeOfContent);}
    public String getTypeOfContent() {return getString(KEY_TYPE_OF_CONTENT); }

    public void setVoteAverage(Double voteAverage) {put(KEY_VOTE_AVERAGE, voteAverage); }
    public Double getVoteAverage() {return getDouble(KEY_VOTE_AVERAGE); }

    public void setOverview(String overview) {put(KEY_OVERVIEW, overview); }
    public String getOverview() {return getString(KEY_OVERVIEW); }

    //TODO: get streamable platforms

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
        setVoteAverage(jsonObject.getDouble("vote_average"));
        setOverview(jsonObject.getString("overview"));
        setTypeOfContent(typeOfContent);
        // assumes this is a new VideoContent being pushed to back4App
        setNumPosted(0);
        setNumInterestedIn(0);

        setPosterUrl(String.format("https://image.tmdb.org/t/p/w342/%s", jsonObject.getString("poster_path")));
        setBackdropUrl(String.format("https://image.tmdb.org/t/p/w342/%s", jsonObject.getString("backdrop_path")));
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
