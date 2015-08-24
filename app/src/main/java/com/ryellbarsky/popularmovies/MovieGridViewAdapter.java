package com.ryellbarsky.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter class to display individual movies with their poster and title
 */
public class MovieGridViewAdapter extends ArrayAdapter<MovieItem>{

    private Context mContext;
    private int mLayoutId;
    private ArrayList<MovieItem> mMovieItems = new ArrayList<>();


    static class ViewHolder {
        TextView movieTitleTextView;
        ImageView moviePosterImageView;
    }

    public MovieGridViewAdapter(Context context, int layoutId, ArrayList<MovieItem> movieItems){
        super(context, layoutId, movieItems);
        mContext = context;
        mLayoutId = layoutId;
        mMovieItems = movieItems;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View movieView = convertView;
        ViewHolder movieViewHolder;

        if (movieView == null){
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            movieView = inflater.inflate(mLayoutId, parent, false);
            movieViewHolder = new ViewHolder();
            movieViewHolder.movieTitleTextView = (TextView) movieView.findViewById(R.id.movie_title);
            movieViewHolder.moviePosterImageView = (ImageView) movieView.findViewById(R.id.movie_poster);
            movieView.setTag(movieViewHolder);
        }
        else{
            movieViewHolder = (ViewHolder) movieView.getTag();
        }

        MovieItem item = mMovieItems.get(position);

        Picasso.with(mContext).load(item.getMoviePosterImageUrl()).into(movieViewHolder.moviePosterImageView);
        movieViewHolder.movieTitleTextView.setText(item.getMovieTitle());

        return movieView;
    }


    public void populateMovieGridView(ArrayList<MovieItem> movies){
        mMovieItems = movies;
        notifyDataSetChanged();
    }


}
