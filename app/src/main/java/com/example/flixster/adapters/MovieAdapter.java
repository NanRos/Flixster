package com.example.flixster.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Parcel;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flixster.DetailActivity;
import com.example.flixster.MainActivity;
import com.example.flixster.R;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    Context context; //where adapter being constructed from
    List<Movie> movies; //data

    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    //usually involves inflating a layout from  XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("MovieAdapter","onCreateViewHolder");
        //inflate view from context
        View movieView = LayoutInflater.from(context).inflate(R.layout.item_movie,parent,false);
        return new ViewHolder(movieView);
    }

    //Involves populating data into the item through the holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         Log.d("MovieAdapter","onBindViewHolder"+position);
        //get the movie at the passed position
        Movie movie = movies.get(position);
        //Bind the movie data into the view holder
        holder.bind(movie);
    }

    //reruns the total count of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //views
        RelativeLayout container;
        TextView tvTitle;
        TextView tvOverview;
        ImageView ivPoster;
        ImageView ivPlayIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //get views form itemView
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            ivPlayIcon=itemView.findViewById(R.id.ivPlayIcon);
            container=itemView.findViewById(R.id.container);

        }

        public void bind(final Movie movie) {
            tvTitle.setText(movie.getTitle());
            tvOverview.setText(movie.getOverview());
            String imageUrl;
            //if landscape
            if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                //them imageURL =back drop image
                imageUrl = movie.getBackdropPath();
            }else{
                 //else imageURL =poster path image
                imageUrl = movie.getPosterPath();
            }
            //android does not have an inbuilt way to render remote images
            //use glide library  with(who).load(from).into(who)
            //placeholder image shown while requested is in process
            int radius = 40; // corner radius, higher value = more rounded
            int margin = 10; // crop margin, set to 0 for corners with no crop
            Glide.with(context)
                    .load(imageUrl)
                    .fitCenter()
                    .transform(new RoundedCornersTransformation(radius, margin))
                    .placeholder(R.drawable.placeholder)
                    .into(ivPoster);

            //check popularity
            if(movie.isPopularity()){
                ivPlayIcon.setVisibility(View.VISIBLE);
            }
            /*Test click on title
            tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,movie.getTitle(),Toast.LENGTH_SHORT).show();

                }
            });*/
            //1.Register click listener on the whole row (container)
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //2.Navigate to a new activity on tap
                    Intent i = new Intent(context, DetailActivity.class);
                    //i.putExtra("title", movie.getTitle());
                    //put whole movie object as Extra
                    i.putExtra("movie", Parcels.wrap(movie));
                    ActivityOptionsCompat options =
                            ActivityOptionsCompat.makeSceneTransitionAnimation((MainActivity) context, container,
                                    "profile");
                    context.startActivity(i,options.toBundle());

                }
            });

        }
    }
}
