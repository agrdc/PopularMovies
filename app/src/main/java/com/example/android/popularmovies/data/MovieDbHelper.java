package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by lsitec207.neto on 05/09/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = MovieDbHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "popularmovies.db";

    private static final int DATABASE_VERSION = 2;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITES_MOVIE_TABLE =
                "CREATE TABLE " + MovieContract.MovieEntry.FAVORITE_MOVIES_TABLE_NAME + " (" +
                        MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, "+
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE, "+
                        MovieContract.MovieEntry.COLUMN_DURATION + " INTEGER NOT NULL, "+
                        MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "+
                        MovieContract.MovieEntry.COLUMN_RATING + " REAL NOT NULL, "+
                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "+
                        MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "+
                        " UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        Log.d(LOG_TAG,"sql create favorite movies table = " +SQL_CREATE_FAVORITES_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.FAVORITE_MOVIES_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
