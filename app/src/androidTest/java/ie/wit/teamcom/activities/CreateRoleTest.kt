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
class CreateRoleTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun createRoleTest() {

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
                withId(R.id.txtSettingsRoles), withText("Roles"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    1
                )
            )
        )
        appCompatTextView.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val viewGroup = onView(
            allOf(
                withParent(withParent(withId(R.id.rolesRecyclerView))),
                isDisplayed()
            )
        )
        viewGroup.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val floatingActionButton = onView(
            allOf(
                withId(R.id.btnAddNewRole),
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
                withId(R.id.editRoleName),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    1
                )
            )
        )
        appCompatEditText4.perform(scrollTo(), replaceText("Test Role"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatImageButton2 = onView(
            allOf(
                withId(R.id.btnColorPicker),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        2
                    ),
                    1
                )
            )
        )
        appCompatImageButton2.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val frameLayout = onView(
            allOf(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java), isDisplayed())
        )
        frameLayout.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton2 = onView(
            allOf(
                withId(android.R.id.button1), withText("ok"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.buttonPanel),
                        0
                    ),
                    3
                )
            )
        )
        appCompatButton2.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val switchCompat = onView(
            allOf(
                withId(R.id.permViewLogs), withText("View All Logs"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    4
                )
            )
        )
        switchCompat.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val switchCompat2 = onView(
            allOf(
                withId(R.id.permManageRoles), withText("Manage Roles"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    5
                )
            )
        )
        switchCompat2.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val switchCompat3 = onView(
            allOf(
                withId(R.id.permViewTasks), withText("View Tasks"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    6
                )
            )
        )
        switchCompat3.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val switchCompat4 = onView(
            allOf(
                withId(R.id.permCreateTasks), withText("Create Tasks"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    7
                )
            )
        )
        switchCompat4.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val switchCompat5 = onView(
            allOf(
                withId(R.id.permCreateInvite), withText("Create Channel Invites"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    8
                )
            )
        )
        switchCompat5.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val switchCompat6 = onView(
            allOf(
                withId(R.id.permCreateGroupChats), withText("Create Groupchats"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    9
                )
            )
        )
        switchCompat6.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val switchCompat7 = onView(
            allOf(
                withId(R.id.permKickUser), withText("Kick Users"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    10
                )
            )
        )
        switchCompat7.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val switchCompat8 = onView(
            allOf(
                withId(R.id.permPostOnFeed), withText("Post On News Feed"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    11
                )
            )
        )
        switchCompat8.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val switchCompat9 = onView(
            allOf(
                withId(R.id.permCreateGCInvite), withText("Create Groupchat Invite"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    12
                )
            )
        )
        switchCompat9.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val switchCompat10 = onView(
            allOf(
                withId(R.id.permRequestMeetings), withText("Request Meetings"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    15
                )
            )
        )
        switchCompat10.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton3 = onView(
            allOf(
                withId(R.id.btnCreateNewRole), withText("Add Role"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    17
                )
            )
        )
        appCompatButton3.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val viewGroup2 = onView(
            allOf(
                withParent(withParent(withId(R.id.rolesRecyclerView))),
                isDisplayed()
            )
        )
        viewGroup2.check(matches(isDisplayed()))
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
