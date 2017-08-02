package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
            case R.id.menu_sort_by:
                View menuAnchorView = findViewById(R.id.menu_sort_by);
                displayPopupMenu(menuAnchorView);
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

    private void displayPopupMenu(final View menuAnchorView) {
        PopupMenu popupMenu = new PopupMenu(this, menuAnchorView);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.activity_main_popup_actions, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_action_load_popular_movies:
                        mMoviesAdapter.setMoviesData(null);
                        loadPopularMoviesData();
                        return true;

                    case R.id.menu_action_load_toprated_movies:
                        mMoviesAdapter.setMoviesData(null);
                        loadTopRatedMoviesData();
                        return true;
                }
                return MainActivity.super.onOptionsItemSelected(item);
            }
        });
        popupMenu.show();
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
