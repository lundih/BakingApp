package com.lundih.android.bakingapp;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.lundih.android.bakingapp.utils.InternetCheck;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;


@RunWith(AndroidJUnit4.class)
public class TestMainActivity {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickFABRefreshRecipes_loadsRecipes() {
        onView(withId(R.id.fabRefreshRecipes)).check(matches(isDisplayed()));
        onView(withId(R.id.fabRefreshRecipes)).perform(click());
        if (InternetCheck.canPing()) {
            onView(withId(R.id.recyclerViewRecipes)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.textViewNetworkResourceRequestMessage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
            onView(withId(R.id.progressBarLoadingRecipes)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        } else {
            onView(withId(R.id.recyclerViewRecipes)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
            onView(withId(R.id.textViewNetworkResourceRequestMessage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.progressBarLoadingRecipes)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        }
    }

    @Test
    public void onPopulateTextViewNetworkRequestMessage_hidesProgressBarLoadingRecipesAndTextViewNetworkResourceRequestMessage() {
        if (!InternetCheck.canPing()) {
            onView(withId(R.id.recyclerViewRecipes)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
            onView(withId(R.id.progressBarLoadingRecipes)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        }
    }
}
