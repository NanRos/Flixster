package com.example.flixster;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    // API call URL + API KEY
    public static final String NOW_PLAYING_URL = "https://api.themoviedb" +
            ".org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    // TAG for log
    public static final String TAG = "MainActivity";
    //list of Movie Objects
    List<Movie> movies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  replace the ActionBar title with customized XML
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        // layout
        setContentView(R.layout.activity_main);
        //define RV
        RecyclerView rvMovies = findViewById(R.id.rvMovies);
        //instantiate movie list modify later(adapter points to movie)
        movies = new ArrayList<>();
        //Create the adapter
        final MovieAdapter movieAdapter = new MovieAdapter(this, movies);
        //Set the adapter on the recycler view
        rvMovies.setAdapter(movieAdapter);
        //set a Layout Manager on the recycler view
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        //add divider decoration to rv
        rvMovies.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        //use ASYNCHTTPCLIENT library
        //create AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        //use URL with client.get
        client.get(NOW_PLAYING_URL ,new JsonHttpResponseHandler(){

            @Override
            //success connection to API
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.v(TAG,"onSuccess"); //logcat
                JSONObject jsonObject = json.jsonObject; //holder for JSONObject results
                try {
                    JSONArray results = jsonObject.getJSONArray("results"); //get API
                    // results->with key name "results"
                    Log.i(TAG,"Results: "+ results.toString());//logcat
                    //call fromJsonArray method from Movie class
                    //returns a List of Movie objects
                    movies.addAll(Movie.fromJsonArray(results));
                    movieAdapter.notifyDataSetChanged();
                    Log.i(TAG,"Movies: " + movies.size()); //logcat
                } catch (JSONException e) {
                    Log.e(TAG,"Hit json exception",e); //logcat
                    e.printStackTrace();
                }
            }

            @Override
            //failed connection to API
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.v(TAG,"onFailure");
            }
        });
    }

}