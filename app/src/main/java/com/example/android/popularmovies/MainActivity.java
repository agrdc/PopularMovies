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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmovies.adapter.MoviesAdapter;
import com.example.android.popularmovies.adapter.FavoriteMoviesAdapter;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.PopularMoviesPreferences;
import com.example.android.popularmovies.utils.DbUtils;
import com.example.android.popularmovies.utils.JsonUtils;
import com.example.android.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.MoviesAdapterOnClickHandler, FavoriteMoviesAdapter.MoviesAdapterOnClickHandler {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private MoviesAdapter mMoviesAdapter;
    private FavoriteMoviesAdapter mFavoriteMoviesAdapter;

    private RecyclerView mRecyclerView;

    private static final String TAG_MOVIE_DATA = "MOVIE_DATA";

    private static final int ID_FAVORITE_MOVIES_LOADER = 324;
    private static final int ID_MOVIES_LOADER = 326;

    private TextView mErrorMessageTextView;

    private LinearLayout mLoadingLayout;

    private String mSortByPreferenceValue;

    private LoaderManager.LoaderCallbacks<List<Movie>> movieDataLoaderCallback = new LoaderManager.LoaderCallbacks<List<Movie>>() {
        @Override
        public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case ID_MOVIES_LOADER:
                    Context context = getApplicationContext();
                    String sortByPreferenceValue = PopularMoviesPreferences.getSortByPreferenceValue(context);
                    mSortByPreferenceValue = sortByPreferenceValue;
                    if (!sortByPreferenceValue.equals(getString(R.string.pref_favorites))) {
                        final URL url = NetworkUtils.buildMoviesListUrl(context);
                        return new AsyncTaskLoader<List<Movie>>(context) {
                            List<Movie> mMoviesData;

                            @Override
                            protected void onStartLoading() {
                                displayLoadingIndicator();
                                if (mMoviesData != null && mMoviesData.size() > 0) {
                                    Log.d(LOG_TAG, "onStartLoading movies data!=null");
                                    deliverResult(mMoviesData);
                                } else
                                    forceLoad();
                                super.onStartLoading();
                            }

                            @Override
                            public List<Movie> loadInBackground() {
                                String httpResponse;
                                List<Movie> moviesData = null;
                                try {
                                    httpResponse = NetworkUtils.getResponseFromHttpUrl(url);
                                    moviesData = JsonUtils.getMoviesDataFromHttpResponse(httpResponse);
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                                return moviesData;
                            }

                            @Override
                            public void deliverResult(List<Movie> data) {
                                Log.d(LOG_TAG, "deliverResult movies loader");
                                mMoviesData = data;
                                super.deliverResult(data);
                            }
                        };
                    }
                default:
                    throw new UnsupportedOperationException("Loader not implemented. Id =" + id);
            }
        }

        @Override
        public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
            mRecyclerView.setAdapter(mMoviesAdapter);
            Log.d(LOG_TAG, "onLoadFinished movies loader");
            mMoviesAdapter.setMovieData(data);
            if (data != null) {
                Log.d(LOG_TAG, "onLoadFinished movies loader data!=null");
                displayMovieView();
            } else {
                displayErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Movie>> loader) {
            mMoviesAdapter.setMovieData(null);
        }
    };

    private LoaderManager.LoaderCallbacks<Cursor> favoriteMovieDataLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case ID_FAVORITE_MOVIES_LOADER:

                    displayLoadingIndicator();
                    Context context = getApplicationContext();
                    String sortByPreferenceValue = PopularMoviesPreferences.getSortByPreferenceValue(context);
                    mSortByPreferenceValue = sortByPreferenceValue;
                    if (sortByPreferenceValue.equals(getString(R.string.pref_favorites))) {
                        // load favorite movies data from db
                        return new CursorLoader(context,
                                MovieContract.MovieEntry.FAVORITE_MOVIES_CONTENT_URI,
                                DbUtils.PROJECTION_FAVORITE_MOVIES,
                                null,
                                null,
                                MovieContract.MovieEntry._ID);
                    }
                default:
                    throw new UnsupportedOperationException("Loader not implemented. Id =" + id);
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mRecyclerView.setAdapter(mFavoriteMoviesAdapter);
            mFavoriteMoviesAdapter.swapCursor(data);
            if (data != null && data.getCount() > 0) {
                Log.i(LOG_TAG, "onLoadFinished favorite movies data!=null");
                displayMovieView();
            } else {
                displayErrorMessage();
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mFavoriteMoviesAdapter.swapCursor(null);
        }
    };

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
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mMoviesAdapter = new MoviesAdapter(this);
        mFavoriteMoviesAdapter = new FavoriteMoviesAdapter(this);

        mSortByPreferenceValue = PopularMoviesPreferences.getSortByPreferenceValue(this);
        Log.d(LOG_TAG, "onCreate testing persistence: pref =" + mSortByPreferenceValue);

        startLoader();
    }


    @Override
    protected void onResume() {
        restartLoaders();
        super.onResume();
    }

    @Override
    protected void onStart() {
        restartLoaders();
        super.onStart();
    }


    private void restartLoaders() {
        String sortByPrefValueNow = PopularMoviesPreferences.getSortByPreferenceValue(this);
        if (sortByPrefValueNow.equals(getString(R.string.pref_favorites))) {
            getSupportLoaderManager().destroyLoader(ID_MOVIES_LOADER);
            getSupportLoaderManager().restartLoader(ID_FAVORITE_MOVIES_LOADER, null, favoriteMovieDataLoaderCallback);
        } else {
            getSupportLoaderManager().destroyLoader(ID_FAVORITE_MOVIES_LOADER);
            getSupportLoaderManager().restartLoader(ID_MOVIES_LOADER, null, movieDataLoaderCallback);
        }
    }

    private void startLoader() {
        if (mSortByPreferenceValue.equals(getString(R.string.pref_favorites))) {
            getSupportLoaderManager().initLoader(ID_FAVORITE_MOVIES_LOADER, null, favoriteMovieDataLoaderCallback);
        } else {
            getSupportLoaderManager().initLoader(ID_MOVIES_LOADER, null, movieDataLoaderCallback);
        }
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
                Intent intentSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(intentSettingsActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movieSelected) {
        Intent intentDetailActivity = new Intent(this, DetailActivity.class);
        intentDetailActivity.putExtra(TAG_MOVIE_DATA, movieSelected);
        startActivity(intentDetailActivity);
    }


    private void displayLoadingIndicator() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mLoadingLayout.setVisibility(View.VISIBLE);
    }

    private void displayErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingLayout.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void displayMovieView() {
        mLoadingLayout.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

}
