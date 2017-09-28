package com.example.android.popularmovies.adapter;

import android.content.Context;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utils.DbUtils;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

public class FavoriteMoviesAdapter extends RecyclerView.Adapter<FavoriteMoviesAdapter.PopularMoviesAdapterViewHolder> {

    private String LOG_TAG = FavoriteMoviesAdapter.class.getSimpleName();
    private Cursor mMoviesData;
    private Context mContext;
    private final MoviesAdapterOnClickHandler mClickHandler;

    public FavoriteMoviesAdapter(MoviesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public interface MoviesAdapterOnClickHandler {
        void onClick(Movie movieSelected);
    }

    @Override
    public FavoriteMoviesAdapter.PopularMoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.movie_grid_item, parent, false);
        return new FavoriteMoviesAdapter.PopularMoviesAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FavoriteMoviesAdapter.PopularMoviesAdapterViewHolder holder, int position) {
        mMoviesData.moveToPosition(position);
        Uri imageUri = NetworkUtils.buildImageUri(mMoviesData.getString(DbUtils.INDEX_POSTER_PATH));
        Picasso.with(mContext).load(imageUri)//.resize(width, height).centerInside()
                .into(holder.imageViewMoviePoster);
    }

    @Override
    public int getItemCount() {
        if (mMoviesData == null) {
            return 0;
        } else return mMoviesData.getCount();
    }

    public void swapCursor(Cursor moviesData) {
        mMoviesData = moviesData;
        notifyDataSetChanged();
    }

    public class PopularMoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView imageViewMoviePoster;

        public PopularMoviesAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewMoviePoster = (ImageView) itemView.findViewById(R.id.iv_item_movie);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mMoviesData.moveToPosition(getAdapterPosition());
            Movie movieSelected = new Movie();
            movieSelected.setTitle(mMoviesData.getString(DbUtils.INDEX_TITLE));
            movieSelected.setMovieId(mMoviesData.getLong(DbUtils.INDEX_MOVIE_ID));
            movieSelected.setDuration(mMoviesData.getInt(DbUtils.INDEX_DURATION));
            movieSelected.setOverview(mMoviesData.getString(DbUtils.INDEX_OVERVIEW));
            movieSelected.setRating(mMoviesData.getDouble(DbUtils.INDEX_RATING));
            movieSelected.setReleaseDate(mMoviesData.getString(DbUtils.INDEX_RELEASE_DATE));
            movieSelected.setImagePath(mMoviesData.getString(DbUtils.INDEX_POSTER_PATH));
            mClickHandler.onClick(movieSelected);
        }
    }

}
