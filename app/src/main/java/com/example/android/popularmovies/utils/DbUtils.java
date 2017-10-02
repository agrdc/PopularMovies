package com.example.android.popularmovies.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieContract;

/**
 * Created by lsitec207.neto on 20/09/17.
 */

public class DbUtils {


    public DbUtils() {

    }

    public static final String[] PROJECTION_FAVORITE_MOVIES = {
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_DURATION,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    public static final int INDEX_TITLE = 0;
    public static final int INDEX_MOVIE_ID = 1;
    public static final int INDEX_DURATION = 2;
    public static final int INDEX_OVERVIEW = 3;
    public static final int INDEX_RATING = 4;
    public static final int INDEX_RELEASE_DATE = 5;
    public static final int INDEX_POSTER_PATH = 6;

    public static boolean isMovieFavorite(Context context, long movieId) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri queryUri = MovieContract.MovieEntry.buildMovieUriWithID(movieId);
        Cursor queryResult = contentResolver.query(
                queryUri,
                PROJECTION_FAVORITE_MOVIES,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + movieId,
                null,
                null);
        if (queryResult != null && queryResult.getCount() > 0) {
            return true;
        } else
            return false;
    }

    public static Movie getFavoriteMovieData(Context context, long movieId) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor queryResult = contentResolver.query(
                MovieContract.MovieEntry.FAVORITE_MOVIES_CONTENT_URI,
                PROJECTION_FAVORITE_MOVIES,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + movieId,
                null,
                null);
        if (queryResult != null && queryResult.getCount() > 0) {
            Movie movie = new Movie();
            movie.setTitle(queryResult.getString(INDEX_TITLE));
            movie.setMovieId(queryResult.getLong(INDEX_MOVIE_ID));
            movie.setDuration(queryResult.getInt(INDEX_DURATION));
            movie.setOverview(queryResult.getString(INDEX_OVERVIEW));
            movie.setRating(queryResult.getDouble(INDEX_RATING));
            movie.setReleaseDate(queryResult.getString(INDEX_RELEASE_DATE));
            movie.setImagePath(queryResult.getString(INDEX_POSTER_PATH));
            return movie;
        } else
            return null;
    }
}
