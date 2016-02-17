package com.mirado.twittersearch;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.mirado.twittersearch.fragments.DetailsFragment;
import com.mirado.twittersearch.fragments.MainFragment;
import com.mirado.twittersearch.utils.SerializeHelper;
import com.mirado.twittersearch.utils.TwitterRequests;

import java.text.SimpleDateFormat;
import java.util.Locale;

import twitter4j.QueryResult;

/**
 * The main activity to store and manage whole UI
 */
public class MainActivity extends BaseActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private TwitterRequests mTwitterRequests;
    public static final SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mTwitterRequests = TwitterRequests.newInstance(this);

        // Setting the main fragment as a base one
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, MainFragment.newInstance(), MainFragment.TAG)
                .commitAllowingStateLoss();
    }

    /**
     * Callback of twitter request
     * @param queryResult
     */
    @Override
    public void searched(final QueryResult queryResult) {
        Log.d(TAG, queryResult.getTweets().toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                SerializeHelper.serialize(MainActivity.this, queryResult.getTweets(), "");
            }
        }).start();

        // Updating the UI in the main thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);

                if (mainFragment != null) {
                    mainFragment.setTweets(queryResult.getTweets());
                }
            }
        });
    }

    /**
     * Returns the main toolbar
     * @return
     */
    public Toolbar getToolbar() {
        return mToolbar;
    }

    /**
     * Returns the twitter request helper
     * @return
     */
    public TwitterRequests getTwitterRequests() {
        return mTwitterRequests;
    }

    /**
     * Hiding keyboard and remove the cursor if required.
     *
     * @param removeCursor - true if cursor to be removed from focus.
     */
    public static void hideKeyboard(Activity activity, boolean removeCursor) {
        // Check if no view has focus:
        View view = activity.getWindow().getCurrentFocus();
        if (removeCursor && view instanceof EditText) {
            ((EditText) view).setCursorVisible(false);
        }
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Handling hardware back button press
     */
    @Override
    public void onBackPressed() {
        int index = getSupportFragmentManager().getBackStackEntryCount() - 1;
        if (index > -1) {
            String entryName = getSupportFragmentManager().getBackStackEntryAt(index).getName();

            if (DetailsFragment.TAG.equals(entryName)) {
                MainFragment mainFragment
                        = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
                mainFragment.setToolbar();
            }
        }

        super.onBackPressed();

    }

}
