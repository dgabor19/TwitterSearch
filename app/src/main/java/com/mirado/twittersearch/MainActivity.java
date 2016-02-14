package com.mirado.twittersearch;

import android.os.Bundle;
import android.util.Log;

import com.mirado.twittersearch.fragments.MainFragment;
import com.mirado.twittersearch.utils.TwitterRequests;

import twitter4j.QueryResult;

public class MainActivity extends BaseActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private TwitterRequests mTwitterRequests;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTwitterRequests = TwitterRequests.newInstance(this);

        MainFragment mainFragment = MainFragment.newInstance();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mainFragment, MainFragment.TAG)
                .commitAllowingStateLoss();
    }

    @Override
    public void searched(final QueryResult queryResult) {
        Log.d(TAG, queryResult.getTweets().toString());

        final MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);

        if (mainFragment != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainFragment.setTweets(queryResult.getTweets());
                }
            });

        }
    }

    public TwitterRequests getTwitterRequests() {
        return mTwitterRequests;
    }

}
