package com.mirado.twittersearch.utils;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Query;
import twitter4j.TwitterListener;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by gabordudas on 14/02/16.
 * Copyright (c) 2015 TwitterSearch. All rights reserved.
 */

/**
 * A helper class which handles the requests
 */
public class TwitterRequests {
    public static final String TAG = TwitterRequests.class.getSimpleName();

    private static TwitterRequests sInstance = null;

    private ConfigurationBuilder mConfigurationBuilder = new ConfigurationBuilder();
    private AsyncTwitterFactory mTwitterFactory = null;
    private AsyncTwitter mTwitter = null;
    private TwitterListener mTwitterListener;

    private TwitterRequests(TwitterListener twitterListener) {
        mTwitterListener = twitterListener;

        initTwitter();
    }

    public static TwitterRequests newInstance(TwitterListener twitterListener) {
        if (sInstance == null) {
            sInstance = new TwitterRequests(twitterListener);
        }

        return sInstance;
    }

    /**
     * Initializing twitter
     */
    private void initTwitter() {
        mConfigurationBuilder.setDebugEnabled(Constants.DEBUG)
                .setOAuthConsumerKey(Constants.CONSUMER_KEY)
                .setOAuthConsumerSecret(Constants.CONSUMER_SECRET)
                .setOAuthAccessToken(Constants.ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(Constants.ACCESS_TOKEN_SECRET);

        mTwitterFactory = new AsyncTwitterFactory(mConfigurationBuilder.build());
        mTwitter = mTwitterFactory.getInstance();
        mTwitter.addListener(mTwitterListener);
    }

    /**
     * Searching for a specific keyword
     * @param keyword
     */
    public void search(final String keyword) {
        mTwitter.search(new Query(keyword));
    }
}
