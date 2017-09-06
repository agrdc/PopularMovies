package com.example.android.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class NetworkUtils {

    final static String LOG_TAG = NetworkUtils.class.getSimpleName();

    final static String MOVIEDB_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";


    final static String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";
    final static String PATH_POPULAR = "popular";
    final static String PATH_TOP_RATED = "top_rated";

    //TODO enter your themoviedb.org API key here
    final static String API_KEY = "433f36e76544735f18173fa0dce254d5";
    final static String PARAM_API_KEY = "api_key";

    public static Uri buildImageUri(String posterPath) {
        Uri builtUri;
        builtUri = Uri.parse(MOVIEDB_IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(posterPath)
                .build();
        Log.d(LOG_TAG, "image uri =" + builtUri.toString());
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
