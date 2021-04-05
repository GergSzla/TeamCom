package ie.wit.teamcom.activities


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
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
class CreateReminderTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun createReminderTest() {

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
        appCompatEditText.perform(replaceText("test@gmail.com"), closeSoftKeyboard())

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
                withId(R.id.nav_reminders),
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(
                            withId(R.id.navView),
                            0
                        )
                    ),
                    13
                ),
                isDisplayed()
            )
        )
        navigationMenuItemView.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val textView = onView(
            allOf(
                withId(R.id.txtEmpty_reminders),
                withText("No reminders set yet. Create one so you don't forget! :)"),
                withParent(withParent(withId(R.id.homeFrame))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("No reminders set yet. Create one so you don't forget! :)")))

        onView(isRoot()).perform(waitFor(3000));
        val imageButton = onView(
            allOf(
                withId(R.id.btnAddNewReminder),
                withParent(withParent(withId(R.id.homeFrame))),
                isDisplayed()
            )
        )
        imageButton.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val floatingActionButton = onView(
            allOf(
                withId(R.id.btnAddNewReminder),
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
        val button = onView(
            allOf(
                withId(R.id.selectDate), withText("SELECT"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val editText = onView(
            allOf(
                withId(R.id.message), withText("Message"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        editText.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button2 = onView(
            allOf(
                withId(R.id.addButton), withText("ADD"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        button2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton2 = onView(
            allOf(
                withId(R.id.selectDate), withText("SELECT"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatButton2.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton3 = onView(
            allOf(
                withId(android.R.id.button1), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        appCompatButton3.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton4 = onView(
            allOf(
                withId(android.R.id.button1), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        appCompatButton4.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton5 = onView(
            allOf(
                withId(R.id.selectDate), withText("SELECT"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatButton5.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton6 = onView(
            allOf(
                withId(android.R.id.button1), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        appCompatButton6.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton7 = onView(
            allOf(
                withId(android.R.id.button1), withText("OK"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        appCompatButton7.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.message),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText4.perform(replaceText("test message"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton8 = onView(
            allOf(
                withId(R.id.addButton), withText("Add"),
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
        appCompatButton8.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val viewGroup = onView(
            allOf(
                withParent(withParent(withId(R.id.remindersRecyclerView))),
                isDisplayed()
            )
        )
        viewGroup.check(matches(isDisplayed()))
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
