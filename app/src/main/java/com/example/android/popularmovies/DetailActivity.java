package com.example.android.popularmovies;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.adapter.ReviewsAdapter;
import com.example.android.popularmovies.adapter.TrailersAdapter;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieProvider;
import com.example.android.popularmovies.utils.DbUtils;
import com.example.android.popularmovies.utils.JsonUtils;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements ReviewsAdapter.ReviewsAdapterOnClickHandler, TrailersAdapter.TrailersAdapterOnClickHandler {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private final String TAG_MOVIE_DATA = "MOVIE_DATA";

    private TextView mTitleTextView;
    private TextView mOverviewTextView;
    private TextView mRatingTextView;
    private TextView mReleaseDateTextView;
    private TextView mDurationTextView;

    private TextView mErrorLoadingImageTextView;

    private Button mDisplayTrailers;
    private Button mDisplayReviews;

    private ProgressBar mDurationLoadingIndicator;
    private ProgressBar mContentLoadingIndicator;


    private ImageView mPosterImageView;

    private Movie mMovie;

    private Uri mImageUri;

    private Menu mMenu;

    private Toast mToastActive;

    private TrailersAdapter mTrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;

    private LinearLayout mTrailersLayout;
    private LinearLayout mReviewsLayout;

    private ConstraintLayout mConstraintLayout;

    private View mDivider0View;
    private View mDivider1View;
    private View mDivider2View;

    private Boolean isFavorite = null;

    private RecyclerView mTrailersRecyclerView;
    private RecyclerView mReviewsRecyclerView;

    private static final int ID_CHECK_IF_MOVIE_FAVORITE_LOADER = 420;
    private static final int ID_MOVIE_DURATION_LOADER = 780;
    public static final int ID_MOVIE_TRAILERS_LOADER = 970;
    public static final int ID_MOVIE_REVIEWS_LOADER = 890;


    private LoaderManager.LoaderCallbacks<Boolean> checkIfMovieIsFavoriteLoaderCallback = new LoaderManager.LoaderCallbacks<Boolean>() {
        @Override
        public Loader<Boolean> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case ID_CHECK_IF_MOVIE_FAVORITE_LOADER:
                    final Context context = getApplicationContext();
                    return new AsyncTaskLoader<Boolean>(context) {
                        @Override
                        protected void onStartLoading() {
                            if (mMovie.getMovieId() == 0) {
                                throw new UnsupportedOperationException("Movie ID is null");
                            }
                            forceLoad();
                            super.onStartLoading();
                        }

                        @Override
                        public Boolean loadInBackground() {
                            return DbUtils.isMovieFavorite(context, mMovie.getMovieId());
                        }
                    };
                default:
                    throw new UnsupportedOperationException("Loader not implemented. Id =" + id);
            }
        }

        @Override
        public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
            isFavorite = data;
            Log.d(LOG_TAG, "onLoadFinished check if movie is favorite loader");
            if (data) {
                mMenu.getItem(0).setIcon(R.drawable.ic_favorite_selected);
            } else {
                mMenu.getItem(0).setIcon(R.drawable.ic_favorite_unselected);
            }
        }

        @Override
        public void onLoaderReset(Loader<Boolean> loader) {
        }
    };

    private LoaderManager.LoaderCallbacks<Integer> durationLoaderCallback = new LoaderManager.LoaderCallbacks<Integer>() {
        @Override
        public Loader<Integer> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case ID_MOVIE_DURATION_LOADER:
                    final Context context = getApplicationContext();
                    return new AsyncTaskLoader<Integer>(context) {
                        Integer mDuration;

                        @Override
                        protected void onStartLoading() {
                            if (mDuration != null) {
                                deliverResult(mDuration);
                            } else {
                                mDurationLoadingIndicator.setVisibility(View.VISIBLE);
                                forceLoad();
                            }
                            super.onStartLoading();
                        }

                        @Override
                        public Integer loadInBackground() {
                            if (mMovie.getMovieId() == 0) {
                                throw new UnsupportedOperationException("Movie ID is null");
                            }
                            try {
                                URL url = NetworkUtils.buildMovieUrl(mMovie.getMovieId());
                                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                                return JsonUtils.getMovieDurationFromHttpResponse(jsonResponse);
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        public void deliverResult(Integer data) {
                            mDuration = data;
                            super.deliverResult(data);
                        }
                    };
                default:
                    throw new UnsupportedOperationException("Loader not implemented. Id =" + id);
            }
        }

        @Override
        public void onLoadFinished(Loader<Integer> loader, Integer data) {
            mDurationLoadingIndicator.setVisibility(View.INVISIBLE);
            if (data != null && data > 0) {
                mMovie.setDuration(data);
                mDurationTextView.setVisibility(View.VISIBLE);
                String duration = String.format(getString(R.string.movie_duration), data);
                mDurationTextView.setText(duration);
            } else {
                String text = "Failed to load duration, please check your connection.";
                showActiveToastShort(text);
            }
        }

        @Override
        public void onLoaderReset(Loader<Integer> loader) {
        }
    };

    private LoaderManager.LoaderCallbacks<List<String[]>> trailersAndReviewsLoaderCallback = new LoaderManager.LoaderCallbacks<List<String[]>>() {
        @Override
        public Loader<List<String[]>> onCreateLoader(final int id, Bundle args) {
            final Context context = getApplicationContext();
            if (id == ID_MOVIE_TRAILERS_LOADER || id == ID_MOVIE_REVIEWS_LOADER) {
                return new AsyncTaskLoader<List<String[]>>(context) {
                    List<String[]> mData;

                    @Override
                    protected void onStartLoading() {
                        if (mData != null && mData.size() > 0) {
                            deliverResult(mData);
                        } else {
                            forceLoad();
                        }
                        super.onStartLoading();
                    }

                    @Override
                    public List<String[]> loadInBackground() {
                        if (mMovie.getMovieId() == 0) {
                            throw new UnsupportedOperationException("Movie ID is null");
                        }
                        List<String[]> data = null;
                        try {
                            URL url = NetworkUtils.buildMovieExtrasUrl(mMovie.getMovieId(), id);
                            String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                            data = JsonUtils.getMovieExtrasFromHttpResponse(jsonResponse, id);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                        return data;
                    }

                    @Override
                    public void deliverResult(List<String[]> data) {
                        mData = data;
                        super.deliverResult(data);
                    }
                };
            } else {
                throw new UnsupportedOperationException("Loader not implemented. Id =" + id);
            }
        }

        @Override
        public void onLoadFinished(Loader<List<String[]>> loader, List<String[]> data) {
            switch (loader.getId()) {
                case ID_MOVIE_TRAILERS_LOADER:
                    mTrailersRecyclerView.setAdapter(mTrailersAdapter);
                    mTrailersAdapter.setMovieTrailersData(data);
                    if (data != null && data.size() > 0) {
                        mTrailersLayout.setVisibility(View.VISIBLE);
                        if (mReviewsLayout.getVisibility() == (View.GONE)) {
                            mDivider2View.setVisibility(View.GONE);
                        } else if (mReviewsLayout.getVisibility() == View.VISIBLE) {
                            mDivider2View.setVisibility(View.VISIBLE);
                        }
                    } else {
                        String text = "Sorry! There are no trailers for this movie.";
                        showActiveToastShort(text);
                    }
                    break;
                case ID_MOVIE_REVIEWS_LOADER:
                    mReviewsRecyclerView.setAdapter(mReviewsAdapter);
                    mReviewsAdapter.setMovieReviewsData(data);
                    if (data != null && data.size() > 0) {
                        mReviewsLayout.setVisibility(View.VISIBLE);
                        if (mTrailersLayout.getVisibility() == (View.VISIBLE)) {
                            mDivider2View.setVisibility(View.VISIBLE);
                        }
                    } else {
                        String text = "Sorry! There are no reviews for this movie.";
                        showActiveToastShort(text);
                    }
                    break;
            }
        }

        @Override
        public void onLoaderReset(Loader<List<String[]>> loader) {
            switch (loader.getId()) {
                case ID_MOVIE_TRAILERS_LOADER:
                    mTrailersAdapter.setMovieTrailersData(null);
                    break;
                case ID_MOVIE_REVIEWS_LOADER:
                    mReviewsAdapter.setMovieReviewsData(null);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleTextView = (TextView) findViewById(R.id.tv_title_movie);
        mOverviewTextView = (TextView) findViewById(R.id.tv_overview_movie);
        mRatingTextView = (TextView) findViewById(R.id.tv_rating_movie);
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_release_date_movie);
        mDurationTextView = (TextView) findViewById(R.id.tv_duration_movie);

        mErrorLoadingImageTextView = (TextView) findViewById(R.id.tv_error_loading_image);

        mDurationLoadingIndicator = (ProgressBar) findViewById(R.id.pb_duration_loader);
        mContentLoadingIndicator = (ProgressBar) findViewById(R.id.pb_content);

        mDisplayTrailers = (Button) findViewById(R.id.btn_fetch_trailers);
        mDisplayReviews = (Button) findViewById(R.id.btn_fetch_reviews);

        mDivider2View = findViewById(R.id.divider2);
        mDivider1View = findViewById(R.id.divider1);
        mDivider0View = findViewById(R.id.divider0);

        mPosterImageView = (ImageView) findViewById(R.id.iv_poster_movie);

        mTrailersLayout = (LinearLayout) findViewById(R.id.ll_movie_trailers);
        mReviewsLayout = (LinearLayout) findViewById(R.id.ll_movie_reviews);

        mConstraintLayout = (ConstraintLayout) findViewById(R.id.cl_detail);

        mTrailersRecyclerView = (RecyclerView) findViewById(R.id.rv_videos);
        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);

        Intent receivedIntent = getIntent();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);

        mTrailersAdapter = new TrailersAdapter(this);
        mReviewsAdapter = new ReviewsAdapter(this);

        mTrailersRecyclerView.setHasFixedSize(true);
        mTrailersRecyclerView.setLayoutManager(linearLayoutManager);
        mReviewsRecyclerView.setHasFixedSize(true);
        mReviewsRecyclerView.setLayoutManager(linearLayoutManager2);

        mDisplayTrailers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTrailersLayout.getVisibility() == (View.VISIBLE)) {
                    mTrailersLayout.setVisibility(View.GONE);
                } else if (mTrailersLayout.getVisibility() == (View.GONE)) {
                    getSupportLoaderManager().restartLoader(ID_MOVIE_TRAILERS_LOADER, null, trailersAndReviewsLoaderCallback);
                }
            }
        });

        mDisplayReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReviewsLayout.getVisibility() == (View.VISIBLE)) {
                    mReviewsLayout.setVisibility(View.GONE);
                } else if (mReviewsLayout.getVisibility() == (View.GONE)) {
                    getSupportLoaderManager().restartLoader(ID_MOVIE_REVIEWS_LOADER, null, trailersAndReviewsLoaderCallback);
                }
            }
        });

        mContentLoadingIndicator.setVisibility(View.VISIBLE);
        if (receivedIntent.hasExtra(TAG_MOVIE_DATA)) {
            mMovie = receivedIntent.getParcelableExtra(TAG_MOVIE_DATA);
            if (mMovie != null) {
                String moviePosterPath = mMovie.getImagePath();
                mImageUri = NetworkUtils.buildLargerImageUri(moviePosterPath);
                Picasso.with(this).load(mImageUri).into(mPosterImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        ConstraintSet set = new ConstraintSet();
                        set.clone(mConstraintLayout);
                        set.connect(mDivider0View.getId(), ConstraintSet.TOP, mPosterImageView.getId(), ConstraintSet.BOTTOM);
                        set.applyTo(mConstraintLayout);
                        loadUiData();
                    }

                    @Override
                    public void onError() {
                        mErrorLoadingImageTextView.setVisibility(View.VISIBLE);
                        ConstraintSet set = new ConstraintSet();
                        set.clone(mConstraintLayout);
                        set.connect(mDivider0View.getId(), ConstraintSet.TOP, mDisplayReviews.getId(), ConstraintSet.BOTTOM);
                        set.applyTo(mConstraintLayout);
                        loadUiData();
                    }
                });
            }
        }
    }

    private void loadUiData() {
        mDivider0View.setVisibility(View.VISIBLE);
        mDivider1View.setVisibility(View.VISIBLE);
        mContentLoadingIndicator.setVisibility(View.INVISIBLE);
        mDisplayTrailers.setVisibility(View.VISIBLE);
        mDisplayReviews.setVisibility(View.VISIBLE);
        mTitleTextView.setText(mMovie.getTitle());
        mOverviewTextView.setText(mMovie.getOverview());
        mReleaseDateTextView.setText(mMovie.getReleaseDate());
        mRatingTextView.setText(String.format(getString(R.string.movie_rating), mMovie.getRating()));
        int movieDuration = mMovie.getDuration();
        if (movieDuration == 0) {
            getSupportLoaderManager().initLoader(ID_MOVIE_DURATION_LOADER, null, durationLoaderCallback);
        } else {
            mDurationTextView.setVisibility(View.VISIBLE);
            String duration = String.format(getString(R.string.movie_duration), movieDuration);
            mDurationTextView.setText(duration);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_detail_menu, menu);
        mMenu = menu;
        getSupportLoaderManager().initLoader(ID_CHECK_IF_MOVIE_FAVORITE_LOADER, null, checkIfMovieIsFavoriteLoaderCallback);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.detail_menu_favorite) {
            final AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(getContentResolver()) {
                @Override
                protected void onInsertComplete(int token, Object cookie, Uri uri) {
                    super.onInsertComplete(token, cookie, uri);
                    if (uri != null) {
                        String text = "Movie was added to favorites!";
                        showActiveToastShort(text);
                        getSupportLoaderManager().restartLoader(ID_CHECK_IF_MOVIE_FAVORITE_LOADER, null, checkIfMovieIsFavoriteLoaderCallback);
                    } else {
                        String text = "Error inserting movie to favorites";
                        showActiveToastShort(text);
                    }
                }

                @Override
                protected void onDeleteComplete(int token, Object cookie, int result) {
                    super.onDeleteComplete(token, cookie, result);
                    if (result > 0) {
                        String text = "Movie was deleted from favorites!";
                        showActiveToastShort(text);
                        getSupportLoaderManager().restartLoader(ID_CHECK_IF_MOVIE_FAVORITE_LOADER, null, checkIfMovieIsFavoriteLoaderCallback);
                    } else {
                        String text = "Error deleting movie to favorites";
                        showActiveToastShort(text);
                    }
                }
            };
            if (!isFavorite) {
                asyncQueryHandler.startInsert(0, null, MovieContract.MovieEntry.FAVORITE_MOVIES_CONTENT_URI, getMovieInsertContentValues());
            } else if (isFavorite) {
                String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + mMovie.getMovieId();
                asyncQueryHandler.startDelete(0, null, MovieContract.MovieEntry.FAVORITE_MOVIES_CONTENT_URI, selection, null);
            }
        }
        else if (item.getItemId() == R.id.detail_menu_refresh) {
            if (mMovie.getDuration()==0)
            getSupportLoaderManager().restartLoader(ID_MOVIE_DURATION_LOADER,null,durationLoaderCallback);
            if (mImageUri!=null && mErrorLoadingImageTextView.getVisibility()==View.VISIBLE) {
                Picasso.with(this).load(mImageUri).into(mPosterImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mErrorLoadingImageTextView.setVisibility(View.INVISIBLE);
                        ConstraintSet set = new ConstraintSet();
                        set.clone(mConstraintLayout);
                        set.connect(mDivider0View.getId(), ConstraintSet.TOP, mPosterImageView.getId(), ConstraintSet.BOTTOM);
                        set.applyTo(mConstraintLayout);
                        loadUiData();
                    }

                    @Override
                    public void onError() {
                        mErrorLoadingImageTextView.setVisibility(View.VISIBLE);
                        ConstraintSet set = new ConstraintSet();
                        set.clone(mConstraintLayout);
                        set.connect(mDivider0View.getId(), ConstraintSet.TOP, mDisplayReviews.getId(), ConstraintSet.BOTTOM);
                        set.applyTo(mConstraintLayout);
                        loadUiData();
                    }
                });
            }
            showActiveToastShort("Updated.");
        }
        return super.onOptionsItemSelected(item);
    }

    private ContentValues getMovieInsertContentValues() {
        if (mMovie == null) {
            return null;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.getMovieId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_DURATION, mMovie.getDuration());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, mMovie.getRating());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, mMovie.getImagePath());
        return contentValues;
    }

    @Override
    public void onReviewClick(String movieReviewUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieReviewUrl));
        startActivity(intent);
    }

    @Override
    public void onTrailerClick(String movieTrailerKey) {
        Intent intent = new Intent(Intent.ACTION_VIEW, NetworkUtils.buildYoutubeVideoUri(movieTrailerKey));
        startActivity(intent);
    }

    public void showActiveToastShort(String text) {
        if (mToastActive != null)
            mToastActive.cancel();
        mToastActive = Toast.makeText(getApplicationContext(), text,
                Toast.LENGTH_SHORT);
        mToastActive.show();
    }
}
