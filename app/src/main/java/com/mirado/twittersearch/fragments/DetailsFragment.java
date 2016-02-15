package com.mirado.twittersearch.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mirado.twittersearch.BaseActivity;
import com.mirado.twittersearch.MainActivity;
import com.mirado.twittersearch.R;
import com.mirado.twittersearch.utils.SerializeHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;

/**
 * Created by gabordudas on 10/02/16.
 * Copyright (c) 2015 TwitterSearch. All rights reserved.
 */
public class DetailsFragment extends Fragment {
    public static final String TAG = MainFragment.class.getSimpleName();

    private static final String PARAM_POSITION = "position";

    private MainActivity mActivity;
    private Status mTweet;

    private ImageView mImage;
    private TextView mName;
    private TextView mAlias;
    private TextView mDescription;
    private TextView mDate;

    private int mPosition;

    public DetailsFragment() {
    }

    public static DetailsFragment newInstance(int position) {
        DetailsFragment fragment = new DetailsFragment();

        Bundle args = new Bundle();
        args.putInt(PARAM_POSITION, position);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mPosition = args.getInt(PARAM_POSITION, 0);
        }

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();

        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mImage = (ImageView) view.findViewById(R.id.imageDetails);
        mName = (TextView) view.findViewById(R.id.textNameDetails);
        mAlias = (TextView) view.findViewById(R.id.textAliasDetails);
        mDescription = (TextView) view.findViewById(R.id.textDescriptionDetails);
        mDate = (TextView) view.findViewById(R.id.textDateDetails);

        new AsyncTask<Integer, Void, Status>() {
            @Override
            protected twitter4j.Status doInBackground(Integer... params) {
                int index = params[0];
                List<twitter4j.Status> statuses = SerializeHelper.deserialize(mActivity, ArrayList.class, "");

                if (statuses != null && index < statuses.size()) {
                    return statuses.get(index);
                }
                return null;
            }

            @Override
            protected void onPostExecute(twitter4j.Status status) {
                super.onPostExecute(status);

                mTweet = status;

                setUI();
            }
        }.execute(mPosition);
    }

    private void setUI() {
        if (mTweet != null) {
            ImageLoader.getInstance()
                    .displayImage(
                            mTweet.getUser().getOriginalProfileImageURL(),
                            mImage,
                            BaseActivity.sDisplayImageLoaderOptions);

            mName.setText(mTweet.getUser().getName());
            mAlias.setText("@" + mTweet.getUser().getScreenName());
            mDescription.setText(mTweet.getText());
            mDate.setText(MainActivity.sDateFormat.format(mTweet.getCreatedAt()));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                getFragmentManager().popBackStack();

                return false;
            default:
                break;
        }

        return false;
    }


//    public void setTweet(Status tweet) {
//        mTweet = tweet;
//
//        ImageLoader.getInstance()
//                .displayImage(
//                        tweet.getUser().getOriginalProfileImageURL(),
//                        mImage,
//                        BaseActivity.sDisplayImageLoaderOptions);
//
//        mName.setText(tweet.getUser().getName());
//        mAlias.setText(tweet.getUser().getScreenName());
//    }

}
