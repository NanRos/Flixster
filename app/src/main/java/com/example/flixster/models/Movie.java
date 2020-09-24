package com.example.flixster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    String backdropPath;
    String posterPath;
    String title;
    String overview;

    //parameter: jsonObject
    //get needed data from jsonObject via key names
    public Movie(JSONObject jsonObject) throws JSONException {
        backdropPath = jsonObject.getString("backdrop_path");
        posterPath = jsonObject.getString("poster_path");
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
    }
    //parameter:JSONArray
    //Returns:List<Movie>
    public static List<Movie> fromJsonArray(JSONArray movieJsonArray) throws JSONException {
        List<Movie> movies =  new ArrayList<>(); //holder for Movie objects
        //iterate through movieJsonArray
        // for each JSONObject instantiate a Movie object then add to movies List.
        for(int i=0;i<movieJsonArray.length();i++){
            movies.add(new Movie(movieJsonArray.getJSONObject(i)));
        }
        //return list
        return movies;
    }

    public String getPosterPath() {
        //poster path is a relative path
        //making api request to configuration API response
        //to get base url for selected size and append relative path
        //shortcut: copy paste ready url
        return String.format("https://image.tmdb.org/t/p/w342/%s",posterPath);
    }
    public String getBackdropPath(){
        return String.format("https://image.tmdb.org/t/p/w342/%s",backdropPath);
    }
    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }
}
