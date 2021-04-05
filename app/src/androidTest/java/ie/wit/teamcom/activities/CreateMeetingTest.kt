package ie.wit.teamcom.activities


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import ie.wit.teamcom.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CreateMeetingTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun createMeetingTest() {
        onView(isRoot()).perform(waitFor(4500));
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
                withId(R.id.nav_meetings),
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(
                            withId(R.id.navView),
                            0
                        )
                    ),
                    8
                ),
                isDisplayed()
            )
        )
        navigationMenuItemView.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val floatingActionButton = onView(
            allOf(
                withId(R.id.btnAddNewMeeting),
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
        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.editTxtTitle),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    2
                )
            )
        )
        appCompatEditText4.perform(scrollTo(), replaceText("Test .eeting"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.editTxtTitle), withText("Test .eeting"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    2
                )
            )
        )
        appCompatEditText5.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText6 = onView(
            allOf(
                withId(R.id.editTxtTitle), withText("Test .eeting"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    2
                )
            )
        )
        appCompatEditText6.perform(scrollTo(), replaceText("Test meeting"))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText7 = onView(
            allOf(
                withId(R.id.editTxtTitle), withText("Test meeting"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText7.perform(closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText8 = onView(
            allOf(
                withId(R.id.editTxtDesc),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    4
                )
            )
        )
        appCompatEditText8.perform(scrollTo(), replaceText("test desc"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatImageButton2 = onView(
            allOf(
                withId(R.id.btnSelectDate),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        6
                    ),
                    1
                )
            )
        )
        appCompatImageButton2.perform(scrollTo(), click())

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
        val appCompatEditText9 = onView(
            allOf(
                withId(R.id.editTxtEnd),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    8
                )
            )
        )
        appCompatEditText9.perform(scrollTo(), replaceText("1"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText10 = onView(
            allOf(
                withId(R.id.editTxtEnd), withText("1"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    8
                )
            )
        )
        appCompatEditText10.perform(pressImeActionButton())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatSpinner = onView(
            allOf(
                withId(R.id.spinnerPlatform),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    18
                )
            )
        )
        appCompatSpinner.perform(scrollTo(), click())

//        val appCompatTextView = onData(anything())
//            .inAdapterView(
//                childAtPosition(
//                    withClassName(`is`("android.widget.PopupWindow$PopupBackgroundView")),
//                    0
//                )
//            )
//            .atPosition(2)
//        appCompatTextView.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText11 = onView(
            allOf(
                withId(R.id.editTxtID),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    23
                )
            )
        )
        appCompatEditText11.perform(scrollTo(), replaceText("1234567"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText12 = onView(
            allOf(
                withId(R.id.editTxtPasscode),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    25
                )
            )
        )
        appCompatEditText12.perform(scrollTo(), replaceText("123456781234"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton5 = onView(
            allOf(
                withId(R.id.btnCreateNewMeeting), withText("Create Meeting"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        26
                    ),
                    0
                )
            )
        )
        appCompatButton5.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton6 = onView(
            allOf(
                withId(android.R.id.button1), withText("Proceed"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.buttonPanel),
                        0
                    ),
                    3
                )
            )
        )
        appCompatButton6.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val viewGroup = onView(
            allOf(
                withParent(withParent(withId(R.id.meetingsRecyclerView))),
                isDisplayed()
            )
        )
        viewGroup.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val textView = onView(
            allOf(
                withId(R.id.txtMTitle), withText("Test meeting"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Test meeting")))

        onView(isRoot()).perform(waitFor(3000));
        val textView2 = onView(
            allOf(
                withId(R.id.txtMDesc), withText("test desc"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("test desc")))

        onView(isRoot()).perform(waitFor(3000));
        val textView3 = onView(
            allOf(
                withId(R.id.txtMLocationElsePlatform), withText("Online via: Zoom"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        textView3.check(matches(withText("Online via: Zoom")))

        onView(isRoot()).perform(waitFor(3000));
        val recyclerView2 = onView(
            allOf(
                withId(R.id.meetingsRecyclerView),
                childAtPosition(
                    withId(R.id.swiperefreshMeetings),
                    0
                )
            )
        )
        recyclerView2.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        onView(isRoot()).perform(waitFor(3000));
        val textView4 = onView(
            allOf(
                withId(R.id.textViewTitle), withText("Test meeting"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView4.check(matches(withText("Test meeting")))

        onView(isRoot()).perform(waitFor(3000));
        val textView5 = onView(
            allOf(
                withId(R.id.textViewDesc), withText("test desc"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView5.check(matches(withText("test desc")))

        onView(isRoot()).perform(waitFor(3000));
        val viewGroup2 = onView(
            allOf(
                withParent(withParent(withId(R.id.meetingMembersRecyclerView))),
                isDisplayed()
            )
        )
        viewGroup2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val textView6 = onView(
            allOf(
                withId(R.id.textViewPlatform), withText("Zoom"),
                withParent(
                    allOf(
                        withId(R.id.linearOnline),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView6.check(matches(withText("Zoom")))
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
