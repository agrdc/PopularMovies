package com.example.android.popularmovies.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    final static String LOG_TAG = JsonUtils.class.getSimpleName();

    final static String RESULT_MOVIES = "results";

    final static String POSTER_PATH = "poster_path";
    final static String MOVIE_TITLE = "title";
    final static String MOVIE_OVERVIEW = "overview";
    final static String MOVIE_USER_RATING = "vote_average";
    final static String MOVIE_RELEASE_DATE = "release_date";

    public static List<String[]> getMoviesDataFromHttpResponse(String httpResponse) throws JSONException {
        JSONObject moviesDataJson = new JSONObject(httpResponse);

        JSONArray moviesDataArray = moviesDataJson.getJSONArray(RESULT_MOVIES);
        ArrayList<String[]> moviesDataList = new ArrayList<>(moviesDataArray.length());

        //number of parameters we're going to get from movie object;
        int movieParameters = 5;

        if (moviesDataArray != null) {
            for (int x = 0; x < moviesDataArray.length(); x++) {
                String[] movieData = new String[movieParameters];
                JSONObject movieObject = moviesDataArray.getJSONObject(x);

                String imagePath = movieObject.getString(POSTER_PATH);
                String title = movieObject.getString(MOVIE_TITLE);
                String overview = movieObject.getString(MOVIE_OVERVIEW);
                String userRating = movieObject.getString(MOVIE_USER_RATING);
                String releaseDate = movieObject.getString(MOVIE_RELEASE_DATE);
                // movieData[x]
                // 0 - image path / 1 - title / 2 - overview / 3- user rating / 4- release date
                movieData[0] = imagePath;
                movieData[1] = title;
                movieData[2] = overview;
                movieData[3] = userRating;
                movieData[4] = releaseDate;
                moviesDataList.add(movieData);
            }
        }
        return moviesDataList;
    }
}
