package com.mirado.twittersearch.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mirado.twittersearch.MainActivity;
import com.mirado.twittersearch.R;
import com.mirado.twittersearch.adapters.TweetAdapter;
import com.mirado.twittersearch.interfaces.TwitterItemClickListener;
import com.mirado.twittersearch.utils.TwitterRequests;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;

/**
 * Created by gabordudas on 10/02/16.
 * Copyright (c) 2015 TwitterSearch. All rights reserved.
 */

/**
 * This fragment contains the main screen when the app is loaded (vertical list)
 */
public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, TwitterItemClickListener {
    public static final String TAG = MainFragment.class.getSimpleName();

    private MainActivity mActivity;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TweetAdapter mAdapter;
    private TwitterRequests mTwitterRequests;
    private List<Status> mTweets = new ArrayList<>();
    private SearchView mSearchView;
    private String mSearchKey;

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        mTwitterRequests = mActivity.getTwitterRequests();

        if (mActivity.getSupportActionBar() == null) {
            mActivity.setSupportActionBar(mActivity.getToolbar());
        }

        // Disabling the back navigation button on top
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mActivity.getSupportActionBar().setHomeButtonEnabled(false);

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setting the reference to the basic UI views
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshMain);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerMain);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new TweetAdapter(mActivity, mTweets, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * This inherited method is for creating toolbar menu, in this case it loads a SearchView
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            mSearchView = (SearchView) searchItem.getActionView();
        }
        if (mSearchView != null) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {

                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    mSearchKey = query;
                    setRefreshing(true);
                    mTwitterRequests.search(mSearchKey);
                    MainActivity.hideKeyboard(mActivity, false);

                    return true;
                }
            };

            mSearchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:

                // Expanding/collapsing SearchView when the user is clicking on it
                if (mSearchView.isIconified()) {
                    mSearchView.setIconified(false);
                } else {
                    mSearchView.setIconified(true);
                }

                return false;
            default:
                break;
        }

        return false;
    }


    /**
     * Setting the responses for async loading
     * @param tweets
     */
    public void setTweets(List<Status> tweets) {
        mTweets = tweets;

        setRefreshing(false);

        if (mAdapter != null) {
            mAdapter.setTweets(mTweets);
        }
    }

    /**
     * Sets the toolbar to default values
     */
    public void setToolbar() {
        if (mActivity.getSupportActionBar() == null) {
            mActivity.setSupportActionBar(mActivity.getToolbar());
        }

        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mActivity.getSupportActionBar().setHomeButtonEnabled(false);
    }

    /**
     * Enabling/disabling the refresh icon
     * @param refreshing
     */
    public void setRefreshing(final boolean refreshing) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(refreshing);
                }
            });
        }
    }

    /**
     * Callback for SwipeRefreshLayout, it's called by user interaction
     */
    @Override
    public void onRefresh() {
        if (mSearchKey != null && mSearchKey.length() > 0) {
            mTwitterRequests.search(mSearchKey);
        } else {
            Toast.makeText(mActivity, R.string.empty_search_refresh, Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRefreshing(false);
                }
            }, 1000);
        }
    }

    /**
     * Callback for list item click
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "clicked on item " + position);

        DetailsFragment detailsFragment = DetailsFragment.newInstance(position);

        getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, 0, android.R.anim.fade_out, 0)
                .add(R.id.container, detailsFragment, DetailsFragment.TAG)
                .addToBackStack(DetailsFragment.TAG)
                .commitAllowingStateLoss();

    }
}
