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
class NewsFeedTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun newsFeedTest() {

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
        val viewGroup = onView(
                allOf(
                        withParent(withParent(withId(R.id.profile_card))),
                        isDisplayed()
                )
        )
        viewGroup.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val imageView = onView(
                allOf(
                        withId(R.id.profImage),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        imageView.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val viewGroup2 = onView(
                allOf(
                        withParent(withParent(withId(R.id.channelsRecyclerView))),
                        isDisplayed()
                )
        )
        viewGroup2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val imageView2 = onView(
                allOf(
                        withId(R.id.imgChannelCard),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        imageView2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val textView = onView(
                allOf(
                        withId(R.id.txtChannelNameCard), withText("Test Channel"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        textView.check(matches(withText("Test Channel")))

        onView(isRoot()).perform(waitFor(3000));
        val textView2 = onView(
                allOf(
                        withId(R.id.txtChannelDescCard), withText("Channel Deacription"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        textView2.check(matches(withText("Channel Deacription")))

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
        val editText = onView(
                allOf(
                        withId(R.id.editTextPost), withText("What is on your mind?"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        editText.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val imageButton2 = onView(
                allOf(
                        withId(R.id.imgBtnSend),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        imageButton2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText4 = onView(
                allOf(
                        withId(R.id.editTextPost),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("androidx.cardview.widget.CardView")),
                                        0
                                ),
                                0
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText4.perform(replaceText("Test Post"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatImageButton = onView(
                allOf(
                        withId(R.id.imgBtnSend),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("androidx.cardview.widget.CardView")),
                                        0
                                ),
                                1
                        ),
                        isDisplayed()
                )
        )
        appCompatImageButton.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val textView3 = onView(
                allOf(
                        withId(R.id.txtPostUser), withText("Test User"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        textView3.check(matches(withText("Test User")))

        onView(isRoot()).perform(waitFor(3000));
        val imageView3 = onView(
                allOf(
                        withId(R.id.post_img),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        imageView3.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val textView4 = onView(
                allOf(
                        withId(R.id.txtPostTimeAndDate), withText("04/04/2021 @ 18:06"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        textView4.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val textView5 = onView(
                allOf(
                        withId(R.id.txtPostContent), withText("Test Post"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        textView5.check(matches(withText("Test Post")))

        onView(isRoot()).perform(waitFor(3000));
        val button = onView(
                allOf(
                        withId(R.id.btnLike), withText("LIKE (0)"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))),
                        isDisplayed()
                )
        )
        button.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button2 = onView(
                allOf(
                        withId(R.id.btnComment), withText("COMMENTS (0)"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))),
                        isDisplayed()
                )
        )
        button2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton2 = onView(
                allOf(
                        withId(R.id.btnLike), withText("Like (0)"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                                        5
                                ),
                                0
                        ),
                        isDisplayed()
                )
        )
        appCompatButton2.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val button3 = onView(
                allOf(
                        withId(R.id.btnLike), withText("LIKE (1)"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))),
                        isDisplayed()
                )
        )
        button3.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button4 = onView(
                allOf(
                        withId(R.id.btnLike), withText("LIKE (1)"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))),
                        isDisplayed()
                )
        )
        button4.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton3 = onView(
                allOf(
                        withId(R.id.btnComment), withText("Comments (0)"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                                        5
                                ),
                                1
                        ),
                        isDisplayed()
                )
        )
        appCompatButton3.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val editText2 = onView(
                allOf(
                        withId(R.id.editComment), withText("Comment"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        editText2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val imageButton3 = onView(
                allOf(
                        withId(R.id.imgBtnPostComment),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        imageButton3.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText5 = onView(
                allOf(
                        withId(R.id.editComment),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("androidx.cardview.widget.CardView")),
                                        0
                                ),
                                0
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText5.perform(replaceText("Test Comment"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatImageButton2 = onView(
                allOf(
                        withId(R.id.imgBtnPostComment),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("androidx.cardview.widget.CardView")),
                                        0
                                ),
                                1
                        ),
                        isDisplayed()
                )
        )
        appCompatImageButton2.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val textView6 = onView(
                allOf(
                        withId(R.id.txtCommentUser), withText("Test User"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        textView6.check(matches(withText("Test User")))

        onView(isRoot()).perform(waitFor(3000));
        val textView7 = onView(
                allOf(
                        withId(R.id.txtCommentContent), withText("Test Comment"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        textView7.check(matches(withText("Test Comment")))

        onView(isRoot()).perform(waitFor(3000));
        val textView8 = onView(
                allOf(
                        withId(R.id.txtCommentTimeAndDate), withText("04/04/2021 @ 18:10"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        textView8.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatImageButton3 = onView(
                allOf(
                        withId(R.id.btn_comment_edit),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("androidx.cardview.widget.CardView")),
                                        0
                                ),
                                0
                        ),
                        isDisplayed()
                )
        )
        appCompatImageButton3.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText6 = onView(
                allOf(
                        withId(R.id.editTxtCommentContent), withText("Test Comment"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("androidx.cardview.widget.CardView")),
                                        0
                                ),
                                5
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText6.perform(replaceText("Test Comment Edited "))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText7 = onView(
                allOf(
                        withId(R.id.editTxtCommentContent), withText("Test Comment Edited "),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("androidx.cardview.widget.CardView")),
                                        0
                                ),
                                5
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText7.perform(closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatImageButton4 = onView(
                allOf(
                        withId(R.id.btn_save_comment),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("androidx.cardview.widget.CardView")),
                                        0
                                ),
                                6
                        ),
                        isDisplayed()
                )
        )
        appCompatImageButton4.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val textView9 = onView(
                allOf(
                        withId(R.id.txtCommentContent), withText("Test Comment Edited "),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        textView9.check(matches(withText("Test Comment Edited ")))
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
