package ie.wit.teamcom.activities


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import ie.wit.teamcom.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginGoogleTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun loginGoogle() {

        onView(isRoot()).perform(waitFor(4500));
        val button = onView(
            allOf(
                withId(R.id.btnGoogleLogin), withText("LOGIN WITH GOOGLE"),
                withParent(withParent(withId(R.id.initScreen))),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton = onView(
            allOf(
                withId(R.id.btnGoogleLogin), withText("Login With Google"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.initScreen),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val textView = onView(
            allOf(
                withId(R.id.card_full_name), withText("Gergő Szilágyi"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Gergő Szilágyi")))

        onView(isRoot()).perform(waitFor(3000));
        val textView2 = onView(
            allOf(
                withId(R.id.card_email), withText("gergo.szla@gmail.com"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("gergo.szla@gmail.com")))
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
