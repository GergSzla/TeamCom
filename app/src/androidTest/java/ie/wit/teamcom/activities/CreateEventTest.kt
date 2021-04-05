package ie.wit.teamcom.activities


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import ie.wit.teamcom.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CreateEventTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun createEventTest() {

        onView(isRoot()).perform(waitFor(4500));
        val appCompatEditText = onView(
                allOf(
                        withId(R.id.txtEmail_logreg),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.initScreen_),
                                        0
                                ),
                                3
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText.perform(replaceText("test"), closeSoftKeyboard())
        onView(isRoot()).perform(waitFor(3000));

        val appCompatEditText2 = onView(
                allOf(
                        withId(R.id.txtPassword),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.initScreen_),
                                        0
                                ),
                                4
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText2.perform(replaceText("password"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText3 = onView(
                allOf(
                        withId(R.id.txtPassword), withText("password"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.initScreen_),
                                        0
                                ),
                                4
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText3.perform(pressImeActionButton())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton = onView(
                allOf(
                        withId(R.id.btnLogin), withText("Login With Email"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.initScreen_),
                                        0
                                ),
                                6
                        ),
                        isDisplayed()
                )
        )
        appCompatButton.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText4 = onView(
                allOf(
                        withId(R.id.txtEmail_logreg), withText("test"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.initScreen_),
                                        0
                                ),
                                3
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText4.perform(replaceText("test@gmail.com"))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText5 = onView(
                allOf(
                        withId(R.id.txtEmail_logreg), withText("test@gmail.com"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.initScreen_),
                                        0
                                ),
                                3
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText5.perform(closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText6 = onView(
                allOf(
                        withId(R.id.txtPassword), withText("password"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.initScreen_),
                                        0
                                ),
                                4
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText6.perform(pressImeActionButton())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton2 = onView(
                allOf(
                        withId(R.id.btnLogin), withText("Login With Email"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.initScreen_),
                                        0
                                ),
                                6
                        ),
                        isDisplayed()
                )
        )
        appCompatButton2.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val recyclerView = onView(
                allOf(
                        withId(R.id.channelsRecyclerView),
                        childAtPosition(
                                withId(R.id.swiperefresh),
                                0
                        )
                )
        )
        recyclerView.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatImageButton = onView(
                allOf(
                        withContentDescription("Open navigation drawer"),
                        childAtPosition(
                                allOf(
                                        withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(`is`("com.google.android.material.appbar.AppBarLayout")),
                                                0
                                        )
                                ),
                                1
                        ),
                        isDisplayed()
                )
        )
        appCompatImageButton.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val navigationMenuItemView = onView(
                allOf(
                        withId(R.id.nav_calendar),
                        childAtPosition(
                                allOf(
                                        withId(R.id.design_navigation_view),
                                        childAtPosition(
                                                withId(R.id.navView),
                                                0
                                        )
                                ),
                                12
                        ),
                        isDisplayed()
                )
        )
        navigationMenuItemView.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton3 = onView(
                allOf(
                        withId(R.id.btn_scroll_to_events), withText("Show Events (0)"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.LinearLayout")),
                                        0
                                ),
                                1
                        )
                )
        )
        appCompatButton3.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val textView = onView(
                allOf(
                        withId(R.id.txtDatePicked), withText("Showing Events for: \n2021-04-04"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                        isDisplayed()
                )
        )
        textView.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val imageButton = onView(
                allOf(
                        withId(R.id.btn_add_event),
                        withParent(withParent(withId(R.id.homeFrame))),
                        isDisplayed()
                )
        )
        imageButton.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val floatingActionButton = onView(
                allOf(
                        withId(R.id.btn_add_event),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.homeFrame),
                                        0
                                ),
                                1
                        ),
                        isDisplayed()
                )
        )
        floatingActionButton.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val frameLayout = onView(
                allOf(
                        withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java)),
                        isDisplayed()
                )
        )
        frameLayout.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val imageView = onView(
                allOf(
                        withId(R.id.image_stat),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()
                )
        )
        imageView.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val editText = onView(
                allOf(
                        withId(R.id.txt_event_name), withText("Event Name"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()
                )
        )
        editText.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val editText2 = onView(
                allOf(
                        withId(R.id.txt_event_desc), withText("Description"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()
                )
        )
        editText2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button = onView(
                allOf(
                        withId(R.id.btn_color_1),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                        isDisplayed()
                )
        )
        button.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button2 = onView(
                allOf(
                        withId(R.id.btn_color_2),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                        isDisplayed()
                )
        )
        button2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button3 = onView(
                allOf(
                        withId(R.id.btn_color_3),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                        isDisplayed()
                )
        )
        button3.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button4 = onView(
                allOf(
                        withId(R.id.btn_color_4),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                        isDisplayed()
                )
        )
        button4.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button5 = onView(
                allOf(
                        withId(R.id.btn_color_5),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                        isDisplayed()
                )
        )
        button5.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button6 = onView(
                allOf(
                        withId(R.id.btn_color_6),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                        isDisplayed()
                )
        )
        button6.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button7 = onView(
                allOf(
                        withId(R.id.btn_color_7),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                        isDisplayed()
                )
        )
        button7.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button8 = onView(
                allOf(
                        withId(R.id.btn_color_8),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                        isDisplayed()
                )
        )
        button8.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button9 = onView(
                allOf(
                        withId(R.id.btn_create_task), withText("DONE"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()
                )
        )
        button9.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText7 = onView(
                allOf(
                        withId(R.id.txt_event_name),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0
                                ),
                                2
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText7.perform(replaceText("Test Event"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText8 = onView(
                allOf(
                        withId(R.id.txt_event_desc),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0
                                ),
                                3
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText8.perform(replaceText("Test Desc"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton4 = onView(
                allOf(
                        withId(R.id.btn_color_6),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.LinearLayout")),
                                        7
                                ),
                                1
                        ),
                        isDisplayed()
                )
        )
        appCompatButton4.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton5 = onView(
                allOf(
                        withId(R.id.btn_create_task), withText("Done"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0
                                ),
                                8
                        ),
                        isDisplayed()
                )
        )
        appCompatButton5.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatImageButton2 = onView(
                allOf(
                        withId(R.id.btn_cancel_event),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0
                                ),
                                0
                        ),
                        isDisplayed()
                )
        )
        appCompatImageButton2.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val imageButton2 = onView(
                allOf(
                        withId(R.id.btn_add_event),
                        withParent(withParent(withId(R.id.homeFrame))),
                        isDisplayed()
                )
        )
        imageButton2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton6 = onView(
                allOf(
                        withId(R.id.btn_scroll_to_events), withText("Show Events (1)"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.LinearLayout")),
                                        0
                                ),
                                1
                        )
                )
        )
        appCompatButton6.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val viewGroup = onView(
                allOf(
                        withParent(
                                allOf(
                                        withId(R.id.event_card),
                                        withParent(withId(R.id.calendarRecyclerView))
                                )
                        ),
                        isDisplayed()
                )
        )
        viewGroup.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatImageButton3 = onView(
                allOf(
                        withContentDescription("Open navigation drawer"),
                        childAtPosition(
                                allOf(
                                        withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(`is`("com.google.android.material.appbar.AppBarLayout")),
                                                0
                                        )
                                ),
                                1
                        ),
                        isDisplayed()
                )
        )
        appCompatImageButton3.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val navigationMenuItemView2 = onView(
                allOf(
                        withId(R.id.nav_calendar),
                        childAtPosition(
                                allOf(
                                        withId(R.id.design_navigation_view),
                                        childAtPosition(
                                                withId(R.id.navView),
                                                0
                                        )
                                ),
                                12
                        ),
                        isDisplayed()
                )
        )
        navigationMenuItemView2.perform(click())
    }
    fun waitFor(millis: Long): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "Wait for $millis milliseconds."
            }

            override fun perform(uiController: UiController, view: View?) {
                uiController.loopMainThreadForAtLeast(millis)
            }
        }
    }
    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
