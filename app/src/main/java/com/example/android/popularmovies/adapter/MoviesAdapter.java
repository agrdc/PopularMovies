package com.example.android.popularmovies.adapter;


import android.content.Context;
import android.content.res.Resources;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private String LOG_TAG = MoviesAdapter.class.getSimpleName();
    private List<Movie> mMovieData;
    private Context mContext;
    private final MoviesAdapterOnClickHandler mClickHandler;

    public MoviesAdapter(MoviesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public interface MoviesAdapterOnClickHandler {
        void onClick(Movie movieSelected);
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
        String movieImagePath = mMovieData.get(position).getImagePath();
        Uri imageUri = NetworkUtils.buildImageUri(movieImagePath);
        // get width and height from display
        int height = Resources.getSystem().getDisplayMetrics().heightPixels / 2;
        int width = Resources.getSystem().getDisplayMetrics().widthPixels / 2;
        Log.d(LOG_TAG, "testing screen height " + height + " width " + width);
        if (width > height) {
            imageUri = NetworkUtils.buildLargerImageUri(movieImagePath);
        }
        //DONE. MAKE THE ITEM FILL THE VIEW ON LANDSCAPE AND VICE-VERSA
        Picasso.with(mContext).load(imageUri)//.resize(width, height).centerInside()
                .into(holder.imageViewMoviePoster);
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null) {
            return 0;
        } else
            Log.d(LOG_TAG, "size " + mMovieData.size());
        return mMovieData.size();
    }

    public void setMovieData(List<Movie> moviesData) {
        mMovieData = moviesData;
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
            Movie movieSelected = mMovieData.get(getAdapterPosition());
            mClickHandler.onClick(movieSelected);
        }
    }

}
