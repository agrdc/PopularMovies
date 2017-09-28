package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by lsitec207.neto on 05/09/17.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITE_MOVIES = "favorite_movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri FAVORITE_MOVIES_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE_MOVIES)
                .build();

        public static final String FAVORITE_MOVIES_TABLE_NAME = PATH_FAVORITE_MOVIES;

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster";

        public static Uri buildMovieUriWithID(long id) {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_FAVORITE_MOVIES)
                    .appendPath(Long.toString(id))
                    .build();
        }

    }
}
