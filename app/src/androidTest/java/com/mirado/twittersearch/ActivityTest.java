package com.mirado.twittersearch;

import android.os.SystemClock;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by gabordudas on 15/02/16.
 * Copyright (c) 2015 TwitterSearch. All rights reserved.
 */

/**
 * This class is for testing the UI
 * run "./gradlew cAT" in terminal
 */
@RunWith(AndroidJUnit4.class)
public class ActivityTest {

    private static final String KEY_WORD = "#Android";
    private static final int SLEEP_INTERVAL = 3000;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    public void placeSearchRequest() {
        // To expand the search view
        onView(withId(R.id.action_search)).perform(click());
        // To place the request
        onView(isAssignableFrom(EditText.class)).perform(typeText(KEY_WORD), pressKey(KeyEvent.KEYCODE_ENTER));
    }


    public void performSwipeRefresh() {
        placeSearchRequest();

        // Perform swipe to refresh
        onView(withId(R.id.swipeRefreshMain)).perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(85)));
    }

    @Test
    public void performOpenListItem() {
        mActivityRule.getActivity();

        performSwipeRefresh();

        // Perform item click in recycler view
        onView(withId(R.id.recyclerMain)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        SystemClock.sleep(SLEEP_INTERVAL);
    }

    public static ViewAction withCustomConstraints(final ViewAction action, final Matcher<View> constraints) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return constraints;
            }

            @Override
            public String getDescription() {
                return action.getDescription();
            }

            @Override
            public void perform(UiController uiController, View view) {
                action.perform(uiController, view);
            }
        };
    }

}
