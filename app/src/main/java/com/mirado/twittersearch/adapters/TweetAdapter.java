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
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.TweetEntity;

/**
 * Created by gabordudas on 12/02/16.
 * Copyright (c) 2015 TwitterSearch. All rights reserved.
 */
public class TweetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = TweetAdapter.class.getSimpleName();

    public static final SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");

    private Context mContext;
    private List<Status> mTweets;

    public TweetAdapter(Context context, List<Status> tweets) {
        mContext = context;
        mTweets = tweets;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TweetHolder(LayoutInflater.from(mContext).inflate(R.layout.item_tweet, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vHolder, int position) {
        TweetHolder holder = (TweetHolder) vHolder;

        Status tweet = mTweets.get(position);


        ImageLoader.getInstance().displayImage(
                tweet.getUser().getOriginalProfileImageURL(),
                holder.imageTweet,
                MainActivity.sDisplayImageLoaderOptions);

        holder.textName.setText(tweet.getUser().getName());
        holder.textDate.setText(sDateFormat.format(tweet.getCreatedAt()));
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

    public static class TweetHolder extends RecyclerView.ViewHolder {
        public ImageView imageTweet;
        public TextView textName;
        public TextView textDate;
        public TextView textDescription;

        public TweetHolder(View itemView) {
            super(itemView);

            imageTweet = (ImageView) itemView.findViewById(R.id.imageTweet);
            textName = (TextView) itemView.findViewById(R.id.textNameTweet);
            textDate = (TextView) itemView.findViewById(R.id.textDateTweet);
            textDescription = (TextView) itemView.findViewById(R.id.textDescriptionTweet);

        }
    }
}
