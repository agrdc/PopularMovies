package com.example.android.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.DetailActivity;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.PopularMoviesPreferences;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class NetworkUtils {

    final static String LOG_TAG = NetworkUtils.class.getSimpleName();

    final static String MOVIEDB_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    final static String MOVIEDB_LARGER_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500/";

    final static String YOUTUBE_BASE_VIDEO_URL = "http://www.youtube.com/watch?v=";

    final static String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";
    final static String PATH_TRAILERS = "videos";
    final static String PATH_REVIEWS = "reviews";
    final static String PATH_POPULAR = "popular";
    final static String PATH_TOP_RATED = "top_rated";

    //TODO enter your themoviedb.org API key here
    final static String API_KEY = "";
    final static String PARAM_API_KEY = "api_key";

    public boolean hasNetworkConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static Uri buildImageUri(String posterPath) {
        Uri builtUri;
        builtUri = Uri.parse(MOVIEDB_IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(posterPath)
                .build();
        Log.d(LOG_TAG, "image uri =" + builtUri.toString());
        return builtUri;
    }

    public static Uri buildLargerImageUri(String posterPath) {
        Uri builtUri;
        builtUri = Uri.parse(MOVIEDB_LARGER_IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(posterPath)
                .build();
        Log.d(LOG_TAG, "larger image uri =" + builtUri.toString());
        return builtUri;
    }

    public static Uri buildYoutubeVideoUri(String key) {
        Uri builtUri = Uri.parse(YOUTUBE_BASE_VIDEO_URL + key);
        Log.d(LOG_TAG, "youtube uri =" + builtUri.toString());
        return builtUri;
    }

    public static URL buildPopularMoviesUrl() {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendPath(PATH_POPULAR)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();
        Log.d(LOG_TAG, "popular movies uri =" + builtUri.toString());
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildMovieUrl(long movieId) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendEncodedPath(Long.toString(movieId))
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();
        Log.d(LOG_TAG, "movie uri =" + builtUri.toString());
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildMovieExtrasUrl(long movieId, int id) {
        Uri builtUri = null;
        if (id==DetailActivity.ID_MOVIE_TRAILERS_LOADER) {
            builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                    .appendEncodedPath(Long.toString(movieId))
                    .appendPath(PATH_TRAILERS)
                    .appendQueryParameter(PARAM_API_KEY, API_KEY)
                    .build();
            Log.d(LOG_TAG, "movie trailers uri =" + builtUri.toString());
        } if (id==DetailActivity.ID_MOVIE_REVIEWS_LOADER) {
            builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                    .appendEncodedPath(Long.toString(movieId))
                    .appendPath(PATH_REVIEWS)
                    .appendQueryParameter(PARAM_API_KEY, API_KEY)
                    .build();
            Log.d(LOG_TAG, "movie reviews uri =" + builtUri.toString());
        }
        if (builtUri==null) {
            return null;
        }
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildMovieTrailersUrl(long movieId) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendEncodedPath(Long.toString(movieId))
                .appendPath(PATH_TRAILERS)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();
        Log.d(LOG_TAG, "movie trailers uri =" + builtUri.toString());
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildMovieReviewsUrl(long movieId) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendEncodedPath(Long.toString(movieId))
                .appendPath(PATH_REVIEWS)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();
        Log.d(LOG_TAG, "movie reviews uri =" + builtUri.toString());
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildMoviesListUrl(Context context) {
        String sortByPreferenceValue = PopularMoviesPreferences.getSortByPreferenceValue(context);
        if (sortByPreferenceValue.equals(context.getString(R.string.pref_top_rated))) {
            return buildTopRatedMoviesUrl();
        } else if (sortByPreferenceValue.equals(context.getString(R.string.pref_most_popular))) {
            return buildPopularMoviesUrl();
        }
        return null;
    }

    public static URL buildTopRatedMoviesUrl() {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendPath(PATH_TOP_RATED)
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();
        Log.d(LOG_TAG, "top rated movies uri =" + builtUri.toString());
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        //setting connection timeout to 5s
        urlConnection.setConnectTimeout(5000);
        //setting read data timeout to 7s
        urlConnection.setReadTimeout(7000);
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
