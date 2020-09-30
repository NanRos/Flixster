package com.example.flixster;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

//extend YouTubeBaseActivity. One current drawback is that this library does not inherit from
// AppCompatActivity so some of your styles may not match those that are defined in styles.xml.
public class DetailActivity extends YouTubeBaseActivity {

    //url for video list from api
    private static final String VIDEOS_URL="https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    //videos from youtube
    private static final String SITE_TYPE = "YouTube";
    // TAG for log
    public static final String TAG = "DetailActivity";
    private  String YOUTUBE_API_KEY ="" ;

    //define variables for views
    TextView tvTitle;
    TextView tvOverview;
    RatingBar ratingBar;
    ImageView ivPoster;
    YouTubePlayerView youTubePlayerView;
    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //youtube api key
        YOUTUBE_API_KEY =getString(R.string.youTube_api_key);
        //grab view from layout by id
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        ratingBar = findViewById(R.id.ratingBar);
        youTubePlayerView = findViewById(R.id.player);
        ivPoster = findViewById(R.id.ivPoster);

        //grab data sent from intent
        //String title = getIntent().getStringExtra("title");
        movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));

        //set view data
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        ratingBar.setRating((float)movie.getRating());
        Glide.with(this).load(movie.getBackdropPath()).placeholder(R.drawable.placeholder).into(ivPoster);

        //use ASYNCHTTPCLIENT library
        //create AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        //use URL with client.get
        client.get(String.format(VIDEOS_URL, movie.getMovieId()), new JsonHttpResponseHandler() {
            //success connection to API
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    //results ->holder for JSONObject results
                    //get array->results
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    Log.i(TAG,"Results: "+ results.toString());//logcat
                    String youTubeKey="";
                    //NO results replace video with image
                    if(results.length() ==0){
                        youTubePlayerView.setVisibility(View.GONE);
                        ivPoster.setVisibility(View.VISIBLE);
                    }else{
                        //check for youtube video trailer
                        for(int i=0;i<results.length();i++){
                            String site=results.getJSONObject(i).getString("site");
                            String type=results.getJSONObject(i).getString("type");
                            if(site.equals(SITE_TYPE) && type.equals("Trailer")){
                                //grab key
                                youTubeKey = results.getJSONObject(i).getString("key");
                                break;
                            }
                        }
                    }
                    //key not retrieved show image else show video
                    if(youTubeKey.isEmpty()){
                        youTubePlayerView.setVisibility(View.GONE);
                        ivPoster.setVisibility(View.VISIBLE);
                    }else{
                        Log.e(TAG, youTubeKey);
                        initializeYouTube(youTubeKey); //call method with youtube movie key
                    }

                } catch (JSONException e) {
                    Log.e(TAG,"Failed to parse JSON",e); //logcat
                    e.printStackTrace();
                }
            }
            //No successful connection to API
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.v(TAG,"onFailure"); //LOG
            }
        });
    }

    private void initializeYouTube(final String youTubeKey) {
        //initialize youtube player with api key
        youTubePlayerView.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d(TAG, "onInitializationSuccess");//LOG
                // do any work here to cue video, play video, etc.
                //if popular movie load video else cue video
                if(movie.isPopularity()){
                    youTubePlayer.loadVideo(youTubeKey);
                }else{
                    youTubePlayer.cueVideo(youTubeKey);
                }
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(TAG, "onInitializationFailure");//LOG
            }
        });
    }
}