package edu.vt.cs.cs5254.dreamcatcher;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.vt.cs.cs5254.dreamcatcher.model.DreamLab;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.core.internal.deps.dagger.internal.Preconditions.checkNotNull;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class BaseDreamCatcherPart3Test {

    DreamLab mDreamLab = DreamLab.getInstance(InstrumentationRegistry.getTargetContext());

    private static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    @Rule
    public IntentsTestRule<DreamListActivity> myActivityRule =
            new IntentsTestRule<>(DreamListActivity.class);

    @After
    public void clearDatabase() {
        mDreamLab.clearDatabase();
    }

    // ==========================================================
    // Please ensure your application passes these tests
    // before submitting your project
    // ==========================================================

    @Test
    public void appContextGivesCorrectPackageName() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("edu.vt.cs.cs5254.dreamcatcher", appContext.getPackageName());
    }

    // ----------------------------------------------------------
    // Basic Functionality
    // ----------------------------------------------------------

    @Test
    public void createDream_CheckTitleRealizedDeferred() {
        // create dream "My Dream" and select realized
        // check title / realized / deferred

        onView(withId(R.id.new_dream)).perform(click());
        onView(withId(R.id.dream_title)).perform(replaceText("My Dream"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.dream_realized)).perform(click());

        onView(withId(R.id.dream_title)).check(matches(withText("My Dream")));
        onView(withId(R.id.dream_realized)).check(matches(isChecked()));
        onView(withId(R.id.dream_deferred)).check(matches(not(isEnabled())));
    }

    @Test
    public void createDream_CheckEntries() {
        // create dream "My Dream" and select realized
        // check revealed entry and realized entry

        onView(withId(R.id.new_dream)).perform(click());
        onView(withId(R.id.dream_title)).perform(replaceText("My Dream"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.dream_realized)).perform(click());

        onView(withId(R.id.dream_entry_recycler_view))
                .check(matches(atPosition(0, hasDescendant(anyOf(
                        withText(containsString("Revealed")),
                        withText(containsString("revealed")),
                        withText(containsString("REVEALED")))))));
        onView(withId(R.id.dream_entry_recycler_view))
                .check(matches(atPosition(1, hasDescendant(anyOf(
                        withText(containsString("Realized")),
                        withText(containsString("realized")),
                        withText(containsString("REALIZED")))))));
    }

    @Test
    public void createDream_CheckListView() {
        // create dream "My Dream" and select realized
        // check list view title and realized icon

        onView(withId(R.id.new_dream)).perform(click());
        onView(withId(R.id.dream_title)).perform(replaceText("My Dream"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.dream_realized)).perform(click());
        Espresso.pressBack();

        onView(withId(R.id.dream_recycler_view))
                .check(matches(atPosition(0,
                        hasDescendant(withText("My Dream")))));
        onView(withId(R.id.dream_recycler_view))
                .check(matches(atPosition(0,
                        hasDescendant(withTagValue(is(R.drawable.dream_realized_icon))))));
    }

    @Test
    public void createAndShareDream_CheckText() {
        // create dream "My Dream" and select realized
        // select share dream
        // check intent for correct action, subject, and text

        onView(withId(R.id.new_dream)).perform(click());
        onView(withId(R.id.dream_title)).perform(replaceText("My Dream"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.dream_realized)).perform(click());

        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
        onView(withId(R.id.share_dream)).perform(click());

        intended(allOf(hasAction(Intent.ACTION_CHOOSER),
                hasExtra(is(Intent.EXTRA_INTENT),
                        allOf(hasAction(Intent.ACTION_SEND),
                                hasExtra(Intent.EXTRA_SUBJECT, "My Dream"),
                                hasExtra(is(Intent.EXTRA_TEXT), containsString("My Dream")),
                                hasExtra(is(Intent.EXTRA_TEXT), anyOf(
                                        containsString("Realized"),
                                        containsString("realized"),
                                        containsString("REALIZED")))
                        ))));
    }

    @Test
    public void createDreamAndTakePhoto_CheckImageView() {
        // create dream "My Dream" and select realized
        // select take dream photo
        // check intents for correct action and output-uri

        onView(withId(R.id.new_dream)).perform(click());
        onView(withId(R.id.dream_title)).perform(replaceText("My Dream"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.dream_realized)).perform(click());

        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
        onView(withId(R.id.take_dream_photo)).perform(click());

        intended(hasAction(MediaStore.ACTION_IMAGE_CAPTURE));
    }

    @Test
    public void createDreamAndComments_CheckComments() {
        // create dream "My Dream"
        // create "Comment 1" and "Comment 2"
        // select realized
        // check comments

        onView(withId(R.id.new_dream)).perform(click());
        onView(withId(R.id.dream_title)).perform(replaceText("My Dream"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.add_comment_fab)).perform(click());
        onView(withId(R.id.comment_text)).perform(replaceText("Comment 1"));
        onView(withText(android.R.string.ok)).perform(click());
        onView(withId(R.id.add_comment_fab)).perform(click());
        onView(withId(R.id.comment_text)).perform(replaceText("Comment 2"));
        onView(withText(android.R.string.ok)).perform(click());
        onView(withId(R.id.dream_realized)).perform(click());

        onView(withId(R.id.dream_entry_recycler_view))
                .check(matches(atPosition(1, hasDescendant(
                        withText(containsString("Comment 1"))))));
        onView(withId(R.id.dream_entry_recycler_view))
                .check(matches(atPosition(2, hasDescendant(
                        withText(containsString("Comment 2"))))));
    }

    @Test
    public void createDreamAndCommentsDeleteComment_CheckEntries() {
        // create dream "My Dream"
        // create "Comment 1" and "Comment 2"
        // select realized
        // swipe to delete comment 2
        // check entries

        onView(withId(R.id.new_dream)).perform(click());
        onView(withId(R.id.dream_title)).perform(replaceText("My Dream"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.add_comment_fab)).perform(click());
        onView(withId(R.id.comment_text)).perform(replaceText("Comment 1"));
        onView(withText(android.R.string.ok)).perform(click());
        onView(withId(R.id.add_comment_fab)).perform(click());
        onView(withId(R.id.comment_text)).perform(replaceText("Comment 2"));
        onView(withText(android.R.string.ok)).perform(click());
        onView(withId(R.id.dream_realized)).perform(click());

        // swipe left to delete
        onView(withId(R.id.dream_entry_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(2, swipeLeft()));

        onView(withId(R.id.dream_entry_recycler_view))
                .check(matches(atPosition(1, hasDescendant(
                        withText(containsString("Comment 1"))))));
        onView(withId(R.id.dream_entry_recycler_view))
                .check(matches(atPosition(2, hasDescendant(anyOf(
                        withText(containsString("Realized")),
                        withText(containsString("realized")),
                        withText(containsString("REALIZED")))))));
    }

    @Test
    public void selectHomeIconAndDeferredFilter_CheckDeferredSelected() {
        // select home icon
        // select deferred filter
        // check if deferred filter is selected

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_deferred_dreams));
        onView(withId(R.id.dream_recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void createDreamsRADROpenNavDrawerSelectRealized_CheckRealizedDreamsDisplayed() {
        // create dreams: realized - active - deferred - realized
        // in list view, open nav drawer
        // selected realized filter
        // check that only realized dreams are displayed

        onView(withId(R.id.new_dream)).perform(click());
        onView(withId(R.id.dream_title)).perform(replaceText("Realized 1"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.dream_realized)).perform(click());
        Espresso.pressBack();

        onView(withId(R.id.new_dream)).perform(click());
        onView(withId(R.id.dream_title)).perform(replaceText("Active 1"));
        Espresso.closeSoftKeyboard();
        Espresso.pressBack();

        onView(withId(R.id.new_dream)).perform(click());
        onView(withId(R.id.dream_title)).perform(replaceText("Deferred 1"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.dream_deferred)).perform(click());
        Espresso.pressBack();

        onView(withId(R.id.new_dream)).perform(click());
        onView(withId(R.id.dream_title)).perform(replaceText("Realized 2"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.dream_realized)).perform(click());
        Espresso.pressBack();

        onView(withId(R.id.dream_recycler_view))
                .check(matches(atPosition(2, hasDescendant(withText("Deferred 1")))));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_realized_dreams));

        onView(withId(R.id.dream_recycler_view))
                .check(matches(atPosition(1, hasDescendant(withText("Realized 2")))));
    }
}