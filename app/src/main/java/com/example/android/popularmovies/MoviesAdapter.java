package com.example.android.popularmovies;


import android.content.Context;
import android.content.res.Resources;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private String LOG_TAG = MoviesAdapter.class.getSimpleName();
    private List<String[]> mMoviesData;
    private Context mContext;
    private final MoviesAdapterOnClickHandler mClickHandler;

    public MoviesAdapter(MoviesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public interface MoviesAdapterOnClickHandler {
        void onClick(String[] movieSelectedData);
    }

    @Override
    public MoviesAdapter.MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.movie_grid_item, parent, false);
        return new MoviesAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.MoviesAdapterViewHolder holder, int position) {
        String movieImagePath = mMoviesData.get(position)[0];
        Uri imageUri = NetworkUtils.buildImageUri(movieImagePath);
        // get width and height from display, to resize the movie image to half width/height of the screen
        int height = Resources.getSystem().getDisplayMetrics().heightPixels / 2;
        int width = Resources.getSystem().getDisplayMetrics().widthPixels / 2;
        Picasso.with(mContext).load(imageUri).resize(width, height).centerInside().into(holder.imageViewMoviePoster);
    }

    @Override
    public int getItemCount() {
        if (mMoviesData == null) {
            return 0;
        } else return mMoviesData.size();
    }

    public void setMoviesData(List<String[]> moviesData) {
        mMoviesData = moviesData;
        notifyDataSetChanged();
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView imageViewMoviePoster;

        public MoviesAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewMoviePoster = (ImageView) itemView.findViewById(R.id.iv_item_movie);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String[] movieSelectedData = mMoviesData.get(getAdapterPosition());
            mClickHandler.onClick(movieSelectedData);
        }
    }

}
