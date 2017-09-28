package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;

import java.util.List;

/**
 * Created by lsitec207.neto on 25/09/17.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {

    private String LOG_TAG = TrailersAdapter.class.getSimpleName();
    private List<String[]> mMovieTrailersData;
    private Context mContext;
    private final TrailersAdapterOnClickHandler mClickHandler;

    public interface TrailersAdapterOnClickHandler {
        void onTrailerClick(String movieTrailerKey);
    }

    public TrailersAdapter(TrailersAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.trailer_list_item,parent,false);
        return new TrailersAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrailersAdapterViewHolder holder, int position) {
        String name = mMovieTrailersData.get(position)[0];
        holder.textViewNameTrailer.setText(name);
    }

    @Override
    public int getItemCount() {
        if (mMovieTrailersData == null) {
            return 0;
        } else
            Log.d(LOG_TAG, "size " + mMovieTrailersData.size());
        return mMovieTrailersData.size();
    }

    public void setMovieTrailersData(List<String[]> movieTrailersData) {
        mMovieTrailersData = movieTrailersData;
        notifyDataSetChanged();
    }

    public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView textViewNameTrailer;

        public TrailersAdapterViewHolder(View itemView) {
            super(itemView);
            textViewNameTrailer = (TextView) itemView.findViewById(R.id.tv_trailer_name);
            //imageViewMoviePoster.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String movieTrailerKey = mMovieTrailersData.get(getAdapterPosition())[1];
            mClickHandler.onTrailerClick(movieTrailerKey);
        }
    }
}
