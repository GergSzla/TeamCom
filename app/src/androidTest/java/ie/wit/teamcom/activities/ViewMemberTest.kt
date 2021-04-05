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
class ViewMemberTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun viewMemberTest() {

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
                withId(R.id.nav_members),
                childAtPosition(
                    allOf(
                        withId(R.id.design_navigation_view),
                        childAtPosition(
                            withId(R.id.navView),
                            0
                        )
                    ),
                    16
                ),
                isDisplayed()
            )
        )
        navigationMenuItemView.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val recyclerView2 = onView(
            allOf(
                withId(R.id.membersRecyclerView),
                childAtPosition(
                    withId(R.id.swiperefreshMembers),
                    0
                )
            )
        )
        recyclerView2.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        onView(isRoot()).perform(waitFor(3000));
        val textView = onView(
            allOf(
                withId(R.id.txt_mem_name), withText("Test User"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Test User")))

        onView(isRoot()).perform(waitFor(3000));
        val textView2 = onView(
            allOf(
                withId(R.id.txtViewName), withText("Name"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("Name")))

        onView(isRoot()).perform(waitFor(3000));
        val textView3 = onView(
            allOf(
                withId(R.id.txtViewEmail), withText("test@gmail.com"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        textView3.check(matches(withText("test@gmail.com")))

        onView(isRoot()).perform(waitFor(3000));
        val textView4 = onView(
            allOf(
                withId(R.id.txtViewRole), withText("Admin"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        textView4.check(matches(withText("Admin")))

        onView(isRoot()).perform(waitFor(3000));
        val imageButton = onView(
            allOf(
                withId(R.id.btn_change_role),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        imageButton.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button = onView(
            allOf(
                withId(R.id.btn_kick_user), withText("KICK USER"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button2 = onView(
            allOf(
                withId(R.id.btn_kick_user), withText("KICK USER"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.ScrollView::class.java))),
                isDisplayed()
            )
        )
        button2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val textView5 = onView(
            allOf(
                withId(R.id.txt_stats), withText("Statistics"),
                withParent(
                    allOf(
                        withId(R.id.mem_stats),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView5.check(matches(withText("Statistics")))

        onView(isRoot()).perform(waitFor(3000));
        val viewGroup = onView(
            allOf(
                withId(R.id.pie_tasks),
                withParent(
                    allOf(
                        withId(R.id.mem_stats),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        viewGroup.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val textView6 = onView(
            allOf(
                withId(R.id.txtongoing), withText("Ongoing Tasks"),
                withParent(
                    allOf(
                        withId(R.id.mem_stats),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView6.check(matches(withText("Ongoing Tasks")))

        onView(isRoot()).perform(waitFor(3000));
        val textView7 = onView(
            allOf(
                withId(R.id.txtComplete), withText("Completed Tasks"),
                withParent(
                    allOf(
                        withId(R.id.mem_stats),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView7.check(matches(withText("Completed Tasks")))

        onView(isRoot()).perform(waitFor(3000));
        val textView8 = onView(
            allOf(
                withId(R.id.txtOverdue), withText("Overdue Tasks"),
                withParent(
                    allOf(
                        withId(R.id.mem_stats),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView8.check(matches(withText("Overdue Tasks")))

        onView(isRoot()).perform(waitFor(3000));
        val textView9 = onView(
            allOf(
                withId(R.id.txtCompletedOverdue), withText("Completed Overdue Tasks "),
                withParent(
                    allOf(
                        withId(R.id.mem_stats),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView9.check(matches(withText("Completed Overdue Tasks ")))

        onView(isRoot()).perform(waitFor(3000));
        val textView10 = onView(
            allOf(
                withId(R.id.txtDue24Hrs), withText("Tasks Due Within 24hrs"),
                withParent(
                    allOf(
                        withId(R.id.mem_stats),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView10.check(matches(withText("Tasks Due Within 24hrs")))

        onView(isRoot()).perform(waitFor(3000));
        val textView11 = onView(
            allOf(
                withId(R.id.txt_mental_health), withText("Mental Health"),
                withParent(
                    allOf(
                        withId(R.id.mem_mental_health),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView11.check(matches(withText("Mental Health")))

        onView(isRoot()).perform(waitFor(3000));
        val progressBar = onView(
            allOf(
                withId(R.id.progressBar_mh),
                withParent(withParent(withId(R.id.mem_mental_health))),
                isDisplayed()
            )
        )
        progressBar.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val textView12 = onView(
            allOf(
                withId(R.id.txt_percentage), withText("38.3"),
                withParent(withParent(withId(R.id.mem_mental_health))),
                isDisplayed()
            )
        )
        textView12.check(matches(withText("38.3")))

        onView(isRoot()).perform(waitFor(3000));
        val textView13 = onView(
            allOf(
                withId(R.id.txt_overall_standing), withText("Could Use Some Friendly Words"),
                withParent(
                    allOf(
                        withId(R.id.mem_mental_health),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView13.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val textView14 = onView(
            allOf(
                withId(R.id.txt_mh_desc),
                withText("-Perhaps does not feel valued or feels their work is not valued.\nand feels overwhelmed and unsupported.\n-Seems like this user needs some support. Please approach with care and support! \n-Please refer to the following website!\nhttps://www.aware.ie/support/support-line/"),
                withParent(
                    allOf(
                        withId(R.id.mem_mental_health),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView14.check(matches(isDisplayed()))
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
