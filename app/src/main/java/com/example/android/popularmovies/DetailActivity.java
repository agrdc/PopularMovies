package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private final String TAG_MOVIE_DATA = "MOVIE_DATA";

    private TextView mTitleTextView;
    private TextView mOverviewTextView;
    private TextView mRatingTextView;
    private TextView mReleaseDateTextView;

    private ImageView mPosterImageView;

    private String[] mMovieData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleTextView = (TextView) findViewById(R.id.tv_title_movie);
        mOverviewTextView = (TextView) findViewById(R.id.tv_overview_movie);
        mRatingTextView = (TextView) findViewById(R.id.tv_rating_movie);
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_release_date_movie);

        mPosterImageView = (ImageView) findViewById(R.id.iv_poster_movie);

        Intent receivedIntent = getIntent();

        if (receivedIntent.hasExtra(TAG_MOVIE_DATA)) {
            mMovieData = receivedIntent.getStringArrayExtra(TAG_MOVIE_DATA);
            if (mMovieData != null) {
                String moviePosterPath = mMovieData[0];
                Uri imageUri = NetworkUtils.buildImageUri(moviePosterPath);

                String title = mMovieData[1];
                String overview = mMovieData[2];
                String rating = mMovieData[3] + "/10";
                String releaseDate = mMovieData[4];

                mTitleTextView.setText(title);
                mOverviewTextView.setText(overview);
                mReleaseDateTextView.setText(releaseDate);
                mRatingTextView.setText(rating);

                Picasso.with(this).load(imageUri).into(mPosterImageView);
            }
        }
    }
}
