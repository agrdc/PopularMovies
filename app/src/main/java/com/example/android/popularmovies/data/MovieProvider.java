package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Created by lsitec207.neto on 05/09/17.
 */

public class MovieProvider extends ContentProvider {

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();

    private MovieDbHelper movieDbHelper;

    private static UriMatcher sUriMatcher = buildUriMatcher();

    private static final int CODE_FAVORITE_MOVIES = 33040;

    private static final int CODE_FAVORITE_MOVIE_WITH_ID = 33540;

    @Override
    public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate");
        movieDbHelper = new MovieDbHelper(getContext());
        return false;
    }

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI
                (MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITE_MOVIES, CODE_FAVORITE_MOVIES);
        matcher.addURI
                (MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITE_MOVIES + "/#", CODE_FAVORITE_MOVIE_WITH_ID);
        return matcher;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = movieDbHelper.getReadableDatabase();
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE_MOVIE_WITH_ID:

                String movieIdString = uri.getLastPathSegment();

                cursor = db.query(MovieContract.MovieEntry.FAVORITE_MOVIES_TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{movieIdString},
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_FAVORITE_MOVIES:
                cursor = db.query(MovieContract.MovieEntry.FAVORITE_MOVIES_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: +" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE_MOVIES:

                db.beginTransaction();
                int rowsAdded = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.FAVORITE_MOVIES_TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsAdded++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowsAdded > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsAdded;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        // setting "1" to selection to return the number of rows deleted on table.
        if (selection == null) selection = "1";
        int rowsDeleted;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE_MOVIES:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.FAVORITE_MOVIES_TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: +" + uri);
        }
        if (rowsDeleted >= 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
