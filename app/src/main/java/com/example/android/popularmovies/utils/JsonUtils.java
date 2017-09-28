package com.example.android.popularmovies.utils;

import android.content.ContentValues;
import android.util.Log;

import com.example.android.popularmovies.DetailActivity;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    final static String LOG_TAG = JsonUtils.class.getSimpleName();

    private final static String RESULT_MOVIES = "results";

    private final static String TRAILER_NAME = "name";
    private final static String TRAILER_KEY = "key";
    private final static String TRAILER_TYPE = "type";

    private final static String REVIEW_AUTHOR = "author";
    private final static String REVIEW_CONTENT = "content";
    private final static String REVIEW_URL = "url";


    private final static String MOVIE_ID = "id";

    private final static String MOVIE_DURATION = "runtime";
    private final static String POSTER_PATH = "poster_path";
    private final static String MOVIE_TITLE = "title";
    private final static String MOVIE_OVERVIEW = "overview";
    private final static String MOVIE_USER_RATING = "vote_average";
    private final static String MOVIE_RELEASE_DATE = "release_date";

    public static List<Movie> getMoviesDataFromHttpResponse(String httpResponse) throws JSONException {
        JSONObject moviesDataJson = new JSONObject(httpResponse);

        JSONArray moviesDataArray = moviesDataJson.getJSONArray(RESULT_MOVIES);
        List<Movie> moviesDataList = new ArrayList<>(moviesDataArray.length());

        for (int x = 0; x < moviesDataArray.length(); x++) {
            Movie movie = new Movie();
            JSONObject movieObject = moviesDataArray.getJSONObject(x);

            String title = movieObject.getString(MOVIE_TITLE);
            long movieId = movieObject.getLong(MOVIE_ID);
            String overview = movieObject.getString(MOVIE_OVERVIEW);
            double rating = movieObject.getDouble(MOVIE_USER_RATING);
            String releaseDate = movieObject.getString(MOVIE_RELEASE_DATE);
            String imagePath = movieObject.getString(POSTER_PATH);

            movie.setTitle(title);
            movie.setMovieId(movieId);
            movie.setOverview(overview);
            movie.setRating(rating);
            movie.setReleaseDate(releaseDate);
            movie.setImagePath(imagePath);
            moviesDataList.add(movie);
        }
        return moviesDataList;
    }

    public static Integer getMovieDurationFromHttpResponse(String httpResponse) throws JSONException {
        JSONObject movieDataJson = new JSONObject(httpResponse);
        return movieDataJson.getInt(MOVIE_DURATION);
    }

    public static List<String[]> getMovieTrailersFromHttpResponse(String httpResponse) throws JSONException {
        JSONObject movieTrailersDataJson = new JSONObject(httpResponse);

        JSONArray movieTrailersDataArray = movieTrailersDataJson.getJSONArray(RESULT_MOVIES);
        List<String[]> movieTrailersList = new ArrayList<>(movieTrailersDataArray.length());

        //number of parameters we're going to get
        int numParams = 2;

        for (int x = 0; x < movieTrailersDataArray.length(); x++) {
            String[] trailer = new String[numParams];
            JSONObject trailerObject = movieTrailersDataArray.getJSONObject(x);

            String name = trailerObject.getString(TRAILER_NAME);
            String key = trailerObject.getString(TRAILER_KEY);
            String type = trailerObject.getString(TRAILER_TYPE);

            trailer[0] = name;
            trailer[1] = key;

            if (type.equals("Trailer")) {
                movieTrailersList.add(trailer);
            }
        }
        return movieTrailersList;
    }

    public static List<String[]> getMovieExtrasFromHttpResponse (String httpResponse, int id) throws JSONException {
        if (id == DetailActivity.ID_MOVIE_REVIEWS_LOADER) {
            return getMovieReviewsFromHttpResponse(httpResponse);
        }
        if (id == DetailActivity.ID_MOVIE_TRAILERS_LOADER) {
            return getMovieTrailersFromHttpResponse(httpResponse);
        }
        return null;
    }

    public static List<String[]> getMovieReviewsFromHttpResponse(String httpResponse) throws JSONException {
        JSONObject movieReviewsDataJson = new JSONObject(httpResponse);

        JSONArray movieReviewsDataArray = movieReviewsDataJson.getJSONArray(RESULT_MOVIES);
        List<String[]> movieReviewsList = new ArrayList<>(movieReviewsDataArray.length());

        //number of parameters we're going to get
        int numParams = 3;

        for (int x = 0; x < movieReviewsDataArray.length(); x++) {
            String[] review = new String[numParams];
            JSONObject reviewObject = movieReviewsDataArray.getJSONObject(x);

            String author = reviewObject.getString(REVIEW_AUTHOR);
            String content = reviewObject.getString(REVIEW_CONTENT);
            String url = reviewObject.getString(REVIEW_URL);

            review[0] = author;
            review[1] = content;
            review[2] = url;

            movieReviewsList.add(review);
        }
        return movieReviewsList;
    }

    public static ContentValues[] getMoviesContentValuesFromHttpResponse(String httpResponse) throws JSONException {
        JSONObject moviesDataJson = new JSONObject(httpResponse);

        JSONArray moviesDataArray = moviesDataJson.getJSONArray(RESULT_MOVIES);

        ContentValues[] movieDataContentValuesArray = new ContentValues[moviesDataArray.length()];

        for (int x = 0; x < moviesDataArray.length(); x++) {
            JSONObject movieObject = moviesDataArray.getJSONObject(x);
            ContentValues contentValues = new ContentValues();

            String title = movieObject.getString(MOVIE_TITLE);
            long movieId = movieObject.getLong(MOVIE_ID);
            String overview = movieObject.getString(MOVIE_OVERVIEW);
            double rating = movieObject.getDouble(MOVIE_USER_RATING);
            String releaseDate = movieObject.getString(MOVIE_RELEASE_DATE);
            String imagePath = movieObject.getString(POSTER_PATH);

            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
            contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, rating);
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, imagePath);

            movieDataContentValuesArray[x] = contentValues;
        }
        return movieDataContentValuesArray;
    }
}
