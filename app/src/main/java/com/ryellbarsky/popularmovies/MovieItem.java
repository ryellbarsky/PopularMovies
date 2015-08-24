package com.ryellbarsky.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Domain class to hold movie data
 */
public class MovieItem implements Parcelable {

    private int movieId;
    private String moviePosterImageUrl;
    private String movieTitle;
    private String movieBackdropImageUrl;
    private String movieReleaseDate;
    private String movieRating;
    private String movieSynopsis;

    public MovieItem(){

    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMoviePosterImageUrl() {
        return moviePosterImageUrl;
    }

    public void setMoviePosterImageUrl(String moviePosterImageUrl) {
        this.moviePosterImageUrl = moviePosterImageUrl;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieBackdropImageUrl() {
        return movieBackdropImageUrl;
    }

    public void setMovieBackdropImageUrl(String movieBackdropImageUrl) {
        this.movieBackdropImageUrl = movieBackdropImageUrl;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public void setMovieReleaseDate(String movieReleaseDate) {
        this.movieReleaseDate = movieReleaseDate;
    }

    public String getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(String movieRating) {
        this.movieRating = movieRating;
    }

    public String getMovieSynopsis() {
        return movieSynopsis;
    }

    public void setMovieSynopsis(String movieSynopsis) {
        this.movieSynopsis = movieSynopsis;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieId);
        dest.writeString(moviePosterImageUrl);
        dest.writeString(movieTitle);
        dest.writeString(movieBackdropImageUrl);
        dest.writeString(movieReleaseDate);
        dest.writeString(movieRating);
        dest.writeString(movieSynopsis);
    }

    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>() {
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    private MovieItem(Parcel in) {
        movieId = in.readInt();
        moviePosterImageUrl = in.readString();
        movieTitle = in.readString();
        movieBackdropImageUrl = in.readString();
        movieReleaseDate = in.readString();
        movieRating = in.readString();
        movieSynopsis = in.readString();
    }


}
