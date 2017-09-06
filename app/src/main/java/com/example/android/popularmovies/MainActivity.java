package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.PopularMoviesPreferences;
import com.example.android.popularmovies.utils.JsonUtils;
import com.example.android.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity 
        implements MoviesAdapter.MoviesAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private MoviesAdapter mMoviesAdapter;
    private RecyclerView mRecyclerView;

    private static final String[] PROJECTION_FAVORITE_MOVIES = {
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    public static final int INDEX_TITLE = 0;
    public static final int INDEX_MOVIE_ID = 1;
    public static final int OVERVIEW = 2;
    public static final int RATING = 3;
    public static final int RELEASE_DATE = 4;
    public static final int POSTER_PATH = 5;

    private static final String TAG_MOVIE_DATA = "MOVIE_DATA";
    private static final String TAG_HTTP_RESPONSE = "HTTP_RESPONSE";

    private static final int ID_MOVIES_LOADER = 324;

    private TextView mErrorMessageTextView;

    private LinearLayout mLoadingLayout;

    // TODO check if mHttpResponse is necessary (guess it wont be)
    private String mHttpResponse = null;

    private String mSortByPreferenceValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // grid layout manager with 2 columns.
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_loading_movies);

        mLoadingLayout = (LinearLayout) findViewById(R.id.ll_loading_movies);
        mLoadingLayout.setVisibility(View.VISIBLE);

        mRecyclerView.setHasFixedSize(true);

        mSortByPreferenceValue = PopularMoviesPreferences.getSortByPreferenceValue(this);
        Log.d(LOG_TAG,"onCreate testing persistence: pref =" + mSortByPreferenceValue);

        mMoviesAdapter = new MoviesAdapter(this);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mMoviesAdapter);

        if (savedInstanceState != null) {
            Log.d(LOG_TAG, "savedInstanceState!=null");
            mHttpResponse = savedInstanceState.getString(TAG_HTTP_RESPONSE);
            List<String[]> moviesData = null;
            if (mHttpResponse != null) {
                displayMovieView();
                try {
                    moviesData = JsonUtils.getMoviesDataFromHttpResponse(mHttpResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                displayErrorMessage();
            }
            mMoviesAdapter.setMoviesData(moviesData);
        } else {
            Log.d(LOG_TAG, "savedInstanceState==null");
            loadPopularMoviesData();
        }
    }

    @Override
    protected void onResume() {
        //TODO check where is the best place to reset the mSortByPreference value.
        if (!mSortByPreferenceValue.equals(PopularMoviesPreferences.getSortByPreferenceValue(this))) {
            //TODO restart loader
        }
        Log.d(LOG_TAG,"onResume testing persistence: pref =" + mSortByPreferenceValue);
        super.onResume();
    }

    @Override
    protected void onStart() {
        //TODO check where is the best place to reset the mSortByPreference value.
        if (!mSortByPreferenceValue.equals(PopularMoviesPreferences.getSortByPreferenceValue(this))) {
            //TODO restart loader
        }
        Log.d(LOG_TAG,"onStart testing persistence: pref =" + mSortByPreferenceValue);
        super.onStart();
    }

    public boolean hasNetworkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intentSettingsActivity = new Intent(this,SettingsActivity.class);
                startActivity(intentSettingsActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(String[] movieSelectedData) {
        Intent intentDetailActivity = new Intent(this, DetailActivity.class);
        intentDetailActivity.putExtra(TAG_MOVIE_DATA, movieSelectedData);
        startActivity(intentDetailActivity);
    }

    private void loadPopularMoviesData() {
        if (hasNetworkConnection()) {
            displayMovieView();
            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
            fetchMoviesTask.execute(NetworkUtils.buildPopularMoviesUrl());
        }
        else {
            displayErrorMessage();
        }
    }

    private void loadTopRatedMoviesData() {
        displayMovieView();
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute(NetworkUtils.buildTopRatedMoviesUrl());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_MOVIES_LOADER:
                String sortByPreferenceValue = PopularMoviesPreferences.getSortByPreferenceValue(this);
                if (sortByPreferenceValue.equals(R.string.pref_favorites)) {
                // load favorite movies data from db
                    return new CursorLoader(this,
                            MovieContract.MovieEntry.FAVORITE_MOVIES_CONTENT_URI,
                            PROJECTION_FAVORITE_MOVIES,
                            null,
                            null,
                            MovieContract.MovieEntry._ID);
                } else {
                    //if
                    return new AsyncTaskLoader<Cursor>(this) {
                        @Override
                        public Cursor loadInBackground() {
                            return null;
                        }
                    };
                }
            default:
                throw new UnsupportedOperationException("Loader not implemented. Id ="+id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount()!=0) {
            displayMovieView();
        } else {
            displayErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class FetchMoviesTask extends AsyncTask<URL, Void, List<String[]>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<String[]> doInBackground(URL... urls) {
            URL url = urls[0];
            String httpResponse = null;
            List<String[]> moviesData = null;
            try {
                httpResponse = NetworkUtils.getResponseFromHttpUrl(url);
                moviesData = JsonUtils.getMoviesDataFromHttpResponse(httpResponse);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mHttpResponse = httpResponse;
            return moviesData;
        }

        @Override
        protected void onPostExecute(List<String[]> moviesData) {

            if (moviesData != null) {
                displayMovieView();
                mMoviesAdapter.setMoviesData(moviesData);
            } else {
                displayErrorMessage();
            }
            super.onPostExecute(moviesData);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mHttpResponse != null)
            outState.putString(TAG_HTTP_RESPONSE, mHttpResponse);
    }


    private void displayErrorMessage() {
        mLoadingLayout.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void displayMovieView() {
        mLoadingLayout.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
    }

}
