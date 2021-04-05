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
class CreateInviteTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun createInviteTest() {

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
                withId(R.id.txtSettingsInvites), withText("Invites"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.ScrollView")),
                        0
                    ),
                    3
                )
            )
        )
        appCompatTextView.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val textView = onView(
            allOf(
                withId(R.id.txtEmpty_invs),
                withText("There do not seem to be any invitations active currently. Create Invitations and Send The Invite Code to allow others to join! :)"),
                withParent(withParent(withId(R.id.homeFrame))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("There do not seem to be any invitations active currently. Create Invitations and Send The Invite Code to allow others to join! :)")))

        onView(isRoot()).perform(waitFor(3000));
        val imageButton = onView(
            allOf(
                withId(R.id.btnAddNewInvite),
                withParent(withParent(withId(R.id.homeFrame))),
                isDisplayed()
            )
        )
        imageButton.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val floatingActionButton = onView(
            allOf(
                withId(R.id.btnAddNewInvite),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.homeFrame),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val viewGroup = onView(
            allOf(
                withParent(
                    allOf(
                        withId(android.R.id.content),
                        withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        viewGroup.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val editText = onView(
            allOf(
                withId(R.id.txtExpires), withText("Expires In (Hrs)"),
                withParent(
                    allOf(
                        withId(R.id.linearLayout),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        editText.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val editText2 = onView(
            allOf(
                withId(R.id.txtUses), withText("Uses"),
                withParent(
                    allOf(
                        withId(R.id.linearLayout),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        editText2.check(matches(withText("Uses")))

        onView(isRoot()).perform(waitFor(3000));
        val button = onView(
            allOf(
                withId(R.id.buttonCancel), withText("CANCEL"),
                withParent(withParent(withId(R.id.linearLayout))),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button2 = onView(
            allOf(
                withId(R.id.buttonSubmit), withText("CREATE"),
                withParent(withParent(withId(R.id.linearLayout))),
                isDisplayed()
            )
        )
        button2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.txtExpires),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText4.perform(replaceText("2"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.txtUses),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText5.perform(replaceText("1"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton2 = onView(
            allOf(
                withId(R.id.buttonSubmit), withText("Create"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.linearLayout),
                        3
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatButton2.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val viewGroup2 = onView(
            allOf(
                withParent(withParent(withId(R.id.invitesRecyclerView))),
                isDisplayed()
            )
        )
        viewGroup2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val textView2 = onView(
            allOf(
                withId(R.id.txtValidFrom_Card), withText("04/04/2021, 18:26:54"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        textView2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val textView3 = onView(
            allOf(
                withId(R.id.txtValidTo_Card), withText("04/04/2021, 20:26:54"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        textView3.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val textView4 = onView(
            allOf(
                withId(R.id.txtUses_Card), withText("0/1"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        textView4.check(matches(withText("0/1")))

        onView(isRoot()).perform(waitFor(3000));
        val textView5 = onView(
            allOf(
                withId(R.id.txtCode), withText("tc-tes@0T-qn0cf_IU"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        textView5.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val recyclerView2 = onView(
            allOf(
                withId(R.id.invitesRecyclerView),
                childAtPosition(
                    withId(R.id.swiperefreshInvites),
                    0
                )
            )
        )
        recyclerView2.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        onView(isRoot()).perform(waitFor(3000));
        val recyclerView3 = onView(
            allOf(
                withId(R.id.invitesRecyclerView),
                childAtPosition(
                    withId(R.id.swiperefreshInvites),
                    0
                )
            )
        )
        recyclerView3.perform(actionOnItemAtPosition<ViewHolder>(0, click()))
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
