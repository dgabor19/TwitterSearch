package com.mirado.twittersearch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mirado.twittersearch.MainActivity;
import com.mirado.twittersearch.R;
import com.mirado.twittersearch.interfaces.TwitterItemClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import twitter4j.Status;

/**
 * Created by gabordudas on 12/02/16.
 * Copyright (c) 2015 TwitterSearch. All rights reserved.
 */

/**
 * Adapter to create the list items of tweets
 */
public class TweetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = TweetAdapter.class.getSimpleName();

    private Context mContext;
    private List<Status> mTweets;
    private TwitterItemClickListener mListener;

    public TweetAdapter(Context context, List<Status> tweets, TwitterItemClickListener itemClickListener) {
        mContext = context;
        mTweets = tweets;
        mListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TweetHolder(LayoutInflater.from(mContext).inflate(R.layout.item_tweet, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vHolder, int position) {
        TweetHolder holder = (TweetHolder) vHolder;

        Status tweet = mTweets.get(position);

        // Async image loading
        ImageLoader.getInstance().displayImage(
                tweet.getUser().getOriginalProfileImageURL(),
                holder.imageTweet,
                MainActivity.sDisplayImageLoaderOptions);

        holder.textName.setText(tweet.getUser().getName());
        holder.textDate.setText(MainActivity.sDateFormat.format(tweet.getCreatedAt()));
        holder.textDescription.setText(tweet.getText());
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public void setTweets(List<Status> tweets) {
        mTweets = tweets;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder pattern for RecyclerView for reusing list items which are out of the screen
     */
    public static class TweetHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageTweet;
        public TextView textName;
        public TextView textDate;
        public TextView textDescription;
        private TwitterItemClickListener mListener;

        public TweetHolder(View itemView, TwitterItemClickListener listener) {
            super(itemView);

            imageTweet = (ImageView) itemView.findViewById(R.id.imageTweet);
            textName = (TextView) itemView.findViewById(R.id.textNameTweet);
            textDate = (TextView) itemView.findViewById(R.id.textDateTweet);
            textDescription = (TextView) itemView.findViewById(R.id.textDescriptionTweet);
            mListener = listener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
