package com.example.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by lsitec207.neto on 20/09/17.
 */

public class Movie implements Parcelable {

    private String title;
    private long movieId;
    private String overview;
    private double rating;
    private String releaseDate;
    private String imagePath;
    private int duration;

    public Movie() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeLong(this.movieId);
        dest.writeString(this.overview);
        dest.writeDouble(this.rating);
        dest.writeString(this.releaseDate);
        dest.writeString(this.imagePath);
        dest.writeInt(this.duration);
    }

    protected Movie(Parcel in) {
        this.title = in.readString();
        this.movieId = in.readLong();
        this.overview = in.readString();
        this.rating = in.readDouble();
        this.releaseDate = in.readString();
        this.imagePath = in.readString();
        this.duration = in.readInt();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
