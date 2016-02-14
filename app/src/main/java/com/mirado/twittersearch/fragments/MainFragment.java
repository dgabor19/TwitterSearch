package com.mirado.twittersearch.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
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

import com.mirado.twittersearch.MainActivity;
import com.mirado.twittersearch.R;
import com.mirado.twittersearch.adapters.TweetAdapter;
import com.mirado.twittersearch.utils.TwitterRequests;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;

/**
 * Created by gabordudas on 10/02/16.
 * Copyright (c) 2015 TwitterSearch. All rights reserved.
 */
public class MainFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
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

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshMain);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerMain);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new TweetAdapter(mActivity, mTweets);
        mRecyclerView.setAdapter(mAdapter);
    }

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


                    Log.i("onQueryTextChange", newText);

                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.i("onQueryTextSubmit", query);

                    mSearchKey = query;
                    setRefreshing(true);
                    mTwitterRequests.search(mSearchKey);

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


    public void setTweets(List<Status> tweets) {
        mTweets = tweets;

        setRefreshing(false);

        if (mAdapter != null) {
            mAdapter.setTweets(mTweets);
        }
    }

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

    @Override
    public void onRefresh() {
        mTwitterRequests.search(mSearchKey);
    }
}
