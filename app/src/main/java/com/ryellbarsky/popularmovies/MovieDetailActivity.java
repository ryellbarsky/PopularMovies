package com.ryellbarsky.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is responsible for displaying the movie movie details
 */
public class MovieDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    private String PARCELABLE_MOVIE_ITEM_EXTRA = "parselableMovieItem";
    private final String RELEASE_DATE = "Release Date";
    private final String RATING = "Rating";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        MovieItem movieItem = (MovieItem) intent.getParcelableExtra(PARCELABLE_MOVIE_ITEM_EXTRA);

        ImageView backdropImage = (ImageView) findViewById(R.id.movieImageLarge);
        ImageView posterImage = (ImageView) findViewById(R.id.movieImageSmall);
        Picasso.with(this).load(movieItem.getMovieBackdropImageUrl()).into(backdropImage);
        Picasso.with(this).load(movieItem.getMoviePosterImageUrl()).into(posterImage);

        TextView titleTextView = (TextView) findViewById(R.id.movieTitle);
        TextView releaseDateTextView = (TextView) findViewById(R.id.movieReleaseDate);
        TextView ratingTextView = (TextView) findViewById(R.id.movieRating);
        TextView synopsisTextView = (TextView) findViewById(R.id.movieSynopsis);
        titleTextView.setText(removeNullText(movieItem.getMovieTitle()));
        releaseDateTextView.setText(RELEASE_DATE + " : " + parseDate(movieItem.getMovieReleaseDate()));
        ratingTextView.setText(RATING + " : " + movieItem.getMovieRating() + "/10");
        synopsisTextView.setText(removeNullText(movieItem.getMovieSynopsis()));

    }

    /**
     * This method will check for text that has been set to 'null' and just remove it rather than displaying 'null' on the screen
     * @param text the text to check for null
     * @return either the original text or an empty string
     */
    private String removeNullText(String text){
        if (text.equalsIgnoreCase("null"))
            return "";

        return text;
    }

    /**
     * This method takes the release date that is formatted in yyyy-MM-dd and transforms it to MMMM d, yyyy
     * @param oldReleaseDate the original release date string
     * @return tne transformed date format
     */
    private String parseDate(String oldReleaseDate){
        DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputDateFormat = new SimpleDateFormat("MMMM d, yyyy");

        Date rd = null;
        try {
            rd = inputDateFormat.parse(oldReleaseDate);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "error parsing release date", e);
        }
        String releaseDate = outputDateFormat.format(rd);
        return releaseDate;
    }



}
