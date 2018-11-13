package com.austindroids.austinfeedsme.events;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.rule.ActivityTestRule;
import androidx.appcompat.widget.SearchView;
import android.view.KeyEvent;

import com.austindroids.austinfeedsme.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by darrankelinske on 5/4/16.
 */
public class EventsActivityTest {

    @Rule
    public ActivityTestRule<EventsActivity> mActivityRule
            = new ActivityTestRule<>(EventsActivity.class);

    @Test
    public void performSearch() {

        onView(withId(R.id.menu_search)).perform(click());
        onView(isAssignableFrom(SearchView.SearchAutoComplete.class)).perform(typeText("pizza"),
                pressKey(KeyEvent.KEYCODE_ENTER));
    }

    @Test
    public void openDrawerAndClickPizza() {

        onView(withId(R.id.drawer_layout_events)).perform(DrawerActions.open());
        // Doesn't work with NavigationView yet
//        onView(withId(R.id.events_map)).perform(scrollTo()).check(matches(isDisplayed()));
//        onView(withId(R.id.events_map)).perform(click());

        onView(withId(R.id.drawer_layout_events)).perform(DrawerActions.close());

    }

    @Test
    public void openOptionsMenu() {
        // Open the overflow menu OR open the options menu,
        // depending on if the device has a hardware or software overflow menu button.
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

//        // Click the item. Logic needs to be fixed - the text says Logout if the user is logged in
//        onView(withText("Login"))
//                .perform(click());
    }

}
