package com.mirado.twittersearch;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.mirado.twittersearch.fragments.MainFragment;
import com.mirado.twittersearch.utils.SerializeHelper;
import com.mirado.twittersearch.utils.TwitterRequests;

import java.text.SimpleDateFormat;
import java.util.List;

import twitter4j.QueryResult;
import twitter4j.Status;

public class MainActivity extends BaseActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private TwitterRequests mTwitterRequests;
    public static final SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                SerializeHelper.serialize(MainActivity.this, queryResult.getTweets(), "");
            }
        }).start();


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
}
