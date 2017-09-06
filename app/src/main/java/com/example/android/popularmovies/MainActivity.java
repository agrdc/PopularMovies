package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

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

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    private String LOG_TAG = MainActivity.class.getSimpleName();

    private MoviesAdapter mMoviesAdapter;
    private RecyclerView mRecyclerView;

    private final String TAG_MOVIE_DATA = "MOVIE_DATA";
    private final String TAG_HTTP_RESPONSE = "HTTP_RESPONSE";

    private TextView mErrorMessageTextView;

    private LinearLayout mLoadingLayout;

    private String mHttpResponse = null;

    private String mSortByPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // grid layout manager with 2 columns.
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_loading_movies);

        mLoadingLayout = (LinearLayout) findViewById(R.id.ll_loading_movies);

        mRecyclerView.setHasFixedSize(true);

        mSortByPref = PopularMoviesPreferences.getSortByPreferenceValue(this);
        Log.d(LOG_TAG,"onCreate testing persistence: pref =" + mSortByPref);

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
        //TODO check if shared preference equals the saved preference selected. if it doesn't restart loader.
        Log.d(LOG_TAG,"onResume testing persistence: pref =" + mSortByPref);
        super.onResume();
    }

    @Override
    protected void onStart() {
        //TODO check if shared preference equals the saved preference selected. if it doesn't restart loader.
        Log.d(LOG_TAG,"onStart testing persistence: pref =" + mSortByPref);
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
            mLoadingLayout.setVisibility(View.INVISIBLE);
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
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void displayMovieView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
    }

}
