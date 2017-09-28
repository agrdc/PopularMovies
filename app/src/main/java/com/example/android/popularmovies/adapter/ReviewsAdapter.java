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

import java.util.List;

/**
 * Created by lsitec207.neto on 26/09/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {
    private String LOG_TAG = ReviewsAdapter.class.getSimpleName();
    private List<String[]> mMovieReviewsData;
    private Context mContext;
    private final ReviewsAdapterOnClickHandler mClickHandler;

    public interface ReviewsAdapterOnClickHandler {
        void onReviewClick(String movieReviewUrl);
    }

    public ReviewsAdapter(ReviewsAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }
    @Override
    public ReviewsAdapter.ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.review_list_item,parent,false);
        return new ReviewsAdapter.ReviewsAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ReviewsAdapterViewHolder holder, int position) {
        String author = mMovieReviewsData.get(position)[0];
        String content = mMovieReviewsData.get(position)[1];

        holder.textViewReviewAuthor.setText(author);
        holder.textViewReview.setText(content);
    }

    @Override
    public int getItemCount() {
        if (mMovieReviewsData == null) {
            return 0;
        } else
            Log.d(LOG_TAG, "size " + mMovieReviewsData.size());
        return mMovieReviewsData.size();
    }

    public void setMovieReviewsData(List<String[]> movieReviewsData) {
        mMovieReviewsData = movieReviewsData;
        notifyDataSetChanged();
    }

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView textViewReviewAuthor;
        public final TextView textViewReview;


        public ReviewsAdapterViewHolder(View itemView) {
            super(itemView);
            textViewReviewAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
            textViewReview = (TextView) itemView.findViewById(R.id.tv_review);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String movieReviewUrl = mMovieReviewsData.get(getAdapterPosition())[2];
            mClickHandler.onReviewClick(movieReviewUrl);
        }
    }
}
