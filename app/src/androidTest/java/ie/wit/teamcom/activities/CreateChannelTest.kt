package ie.wit.teamcom.activities


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
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
class CreateChannelTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test

    fun createChannelTest() {
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

//        pressBack()
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
        val imageButton = onView(
            allOf(
                withId(R.id.btnAddNew),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        imageButton.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val textView = onView(
            allOf(
                withId(R.id.txtEmpty),
                withText("You are not yet part of any channels. Create or Join some! :)"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        textView.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val floatingActionButton = onView(
            allOf(
                withId(R.id.btnAddNew),
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
        floatingActionButton.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val frameLayout = onView(
            allOf(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java), isDisplayed())
        )
        frameLayout.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val textView2 = onView(
            allOf(
                IsInstanceOf.instanceOf(android.widget.TextView::class.java),
                withText("Add New Channel"),
                withParent(
                    allOf(
                        IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java),
                        withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("Add New Channel")))

        onView(isRoot()).perform(waitFor(3000));
        val textView3 = onView(
            allOf(
                withId(android.R.id.text1), withText("Create"),
                withParent(
                    allOf(
                        IsInstanceOf.instanceOf(android.widget.ListView::class.java),
                        withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView3.check(matches(withText("Create")))

        onView(isRoot()).perform(waitFor(3000));
        val textView4 = onView(
            allOf(
                withId(android.R.id.text1), withText("Join"),
                withParent(
                    allOf(
                        IsInstanceOf.instanceOf(android.widget.ListView::class.java),
                        withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView4.check(matches(withText("Join")))

        onView(isRoot()).perform(waitFor(3000));
        val textView5 = onView(
            allOf(
                withId(android.R.id.text1), withText("Join"),
                withParent(
                    allOf(
                        IsInstanceOf.instanceOf(android.widget.ListView::class.java),
                        withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        textView5.check(matches(withText("Join")))

//        val appCompatTextView = onData(anything())
//            .inAdapterView(
//                allOf(
//                    withClassName(`is`("com.android.internal.app.AlertController$RecycleListView")),
//                    childAtPosition(
//                        withClassName(`is`("android.widget.FrameLayout")),
//                        0
//                    )
//                )
//            )
//            .atPosition(0)
//        appCompatTextView.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val viewGroup = onView(
            allOf(
                withParent(
                    allOf(
                        withId(android.R.id.content),
                        withParent(withId(R.id.action_bar_root))
                    )
                ),
                isDisplayed()
            )
        )
        viewGroup.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val imageView = onView(
            allOf(
                withId(R.id.imageView2),
                withParent(withParent(withId(R.id.linearLayout6))),
                isDisplayed()
            )
        )
        imageView.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button = onView(
            allOf(
                withId(R.id.btnChannelImage), withText("ADD IMAGE"),
                withParent(withParent(withId(R.id.linearLayout6))),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val editText = onView(
            allOf(
                withId(R.id.txtChannelName), withText("Channel Name"),
                withParent(
                    allOf(
                        withId(R.id.linearLayout6),
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
                withId(R.id.txtChannelDesc), withText("Channel Description"),
                withParent(
                    allOf(
                        withId(R.id.linearLayout6),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        editText2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button2 = onView(
            allOf(
                withId(R.id.btnCreateNew), withText("CREATE CHANNEL"),
                withParent(
                    allOf(
                        withId(R.id.linearLayout6),
                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                    )
                ),
                isDisplayed()
            )
        )
        button2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.txtChannelName),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout6),
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
        appCompatEditText3.perform(replaceText("Test Channel"), closeSoftKeyboard())


        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.txtChannelDesc),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout6),
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
        appCompatEditText4.perform(replaceText("Channel Deacription"), closeSoftKeyboard())


        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton2 = onView(
            allOf(
                withId(R.id.btnCreateNew), withText("Create Channel"),
                childAtPosition(
                    allOf(
                        withId(R.id.linearLayout6),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatButton2.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val frameLayout2 = onView(
            allOf(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java), isDisplayed())
        )
        frameLayout2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val viewGroup2 = onView(
            allOf(
                withParent(withParent(withId(R.id.channelsRecyclerView))),
                isDisplayed()
            )
        )
        viewGroup2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val textView6 = onView(
            allOf(
                withId(R.id.txtChannelNameCard), withText("Test Channel"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        textView6.check(matches(withText("Test Channel")))

        onView(isRoot()).perform(waitFor(3000));
        val textView7 = onView(
            allOf(
                withId(R.id.txtChannelDescCard), withText("Channel Deacription"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        textView7.check(matches(withText("Channel Deacription")))
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
