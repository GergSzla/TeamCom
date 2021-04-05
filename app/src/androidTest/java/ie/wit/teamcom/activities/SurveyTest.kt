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
class SurveyTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun surveyTest() {

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
                withId(R.id.nav_channel_settings),
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(
                            withId(R.id.navView),
                            0
                        )
                    ),
                    14
                ),
                isDisplayed()
            )
        )
        navigationMenuItemView.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatTextView = onView(
            allOf(
                withId(R.id.txtSettingsQuestionnaireRem), withText("Survey Settings"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    5
                )
            )
        )
        appCompatTextView.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val imageButton = onView(
            allOf(
                withId(R.id.btn_info_dialog),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        imageButton.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val switch_ = onView(
            allOf(
                withId(R.id.toggle_survey), withText("Enable Recurring Survey ON"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        switch_.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val switch_2 = onView(
            allOf(
                withId(R.id.toggle_public), withText("Enable Visibility for Admin OFF"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        switch_2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val radioGroup = onView(
            allOf(
                withId(R.id.radio_group),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        radioGroup.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button = onView(
            allOf(
                withId(R.id.btn_save_survey_pref), withText("SAVE PREFERENCES"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val imageButton2 = onView(
            allOf(
                withId(R.id.fab_expand_menu_button),
                withParent(
                    allOf(
                        withId(R.id.survey_btn),
                        withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        imageButton2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val switchCompat = onView(
            allOf(
                withId(R.id.toggle_public), withText("Enable Visibility for Admin"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    2
                )
            )
        )
        switchCompat.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatRadioButton = onView(
            allOf(
                withId(R.id.radioButton3), withText("Daily"),
                childAtPosition(
                    allOf(
                        withId(R.id.radio_group),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            4
                        )
                    ),
                    0
                )
            )
        )
        appCompatRadioButton.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton2 = onView(
            allOf(
                withId(R.id.btn_save_survey_pref), withText("Save Preferences"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    5
                )
            )
        )
        appCompatButton2.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val frameLayout = onView(
            allOf(
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        frameLayout.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton3 = onView(
            allOf(
                withId(android.R.id.button2), withText("Cancel"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.buttonPanel),
                        0
                    ),
                    2
                )
            )
        )
        appCompatButton3.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val viewInteraction = onView(
            allOf(
                withId(R.id.fab_expand_menu_button),
                childAtPosition(
                    allOf(
                        withId(R.id.survey_btn),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            1
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        viewInteraction.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val imageButton3 = onView(
            allOf(
                withId(R.id.btnAddManual),
                withParent(
                    allOf(
                        withId(R.id.survey_btn),
                        withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        imageButton3.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val floatingActionButton = onView(
            allOf(
                withId(R.id.btnAddManual),
                childAtPosition(
                    allOf(
                        withId(R.id.survey_btn),
                        childAtPosition(
                            withClassName(`is`("android.widget.FrameLayout")),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatRadioButton2 = onView(
            allOf(
                withId(R.id._1opt4), withText("Quite Overwhelmed"),
                childAtPosition(
                    allOf(
                        withId(R.id.radio_group1),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            2
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatRadioButton2.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatRadioButton3 = onView(
            allOf(
                withId(R.id._2opt4), withText("Quite Valued"),
                childAtPosition(
                    allOf(
                        withId(R.id.radio_group2),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            4
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatRadioButton3.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatRadioButton4 = onView(
            allOf(
                withId(R.id._3opt1), withText("Not At All Valued"),
                childAtPosition(
                    allOf(
                        withId(R.id.radio_group3),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            6
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatRadioButton4.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatRadioButton5 = onView(
            allOf(
                withId(R.id._4opt5), withText("Not At All"),
                childAtPosition(
                    allOf(
                        withId(R.id.radio_group4),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            8
                        )
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        appCompatRadioButton5.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatRadioButton6 = onView(
            allOf(
                withId(R.id._4opt4), withText("No"),
                childAtPosition(
                    allOf(
                        withId(R.id.radio_group4),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            8
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatRadioButton6.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatRadioButton7 = onView(
            allOf(
                withId(R.id._5opt3), withText("More Than Half The Days"),
                childAtPosition(
                    allOf(
                        withId(R.id.radio_group5),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            11
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatRadioButton7.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatRadioButton8 = onView(
            allOf(
                withId(R.id._6opt1), withText("Not At All"),
                childAtPosition(
                    allOf(
                        withId(R.id.radio_group6),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            13
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatRadioButton8.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatRadioButton9 = onView(
            allOf(
                withId(R.id._7opt2), withText("Several Days"),
                childAtPosition(
                    allOf(
                        withId(R.id.radio_group7),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            15
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatRadioButton9.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatRadioButton10 = onView(
            allOf(
                withId(R.id._8opt2), withText("Several Days"),
                childAtPosition(
                    allOf(
                        withId(R.id.radio_group8),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            17
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatRadioButton10.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatRadioButton11 = onView(
            allOf(
                withId(R.id._9opt2), withText("Several Days"),
                childAtPosition(
                    allOf(
                        withId(R.id.radio_group9),
                        childAtPosition(
                            withClassName(`is`("android.widget.LinearLayout")),
                            19
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatRadioButton11.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton8 = onView(
            allOf(
                withId(R.id.btn_save_survey), withText("Save Survey"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("com.amar.library.ui.StickyScrollView")),
                        0
                    ),
                    20
                ),
                isDisplayed()
            )
        )
        appCompatButton8.perform(click())
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
