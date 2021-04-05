package ie.wit.teamcom.activities


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
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
class RegFirebaseTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun regFirebase() {

        onView(isRoot()).perform(waitFor(4500));
        val button = onView(
            allOf(
                withId(R.id.btnShowReg), withText("CREATE AN ACCOUNT"),
                withParent(withParent(withId(R.id.initScreen_))),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button2 = onView(
            allOf(
                withId(R.id.btnLogin), withText("LOGIN WITH EMAIL"),
                withParent(withParent(withId(R.id.initScreen_))),
                isDisplayed()
            )
        )
        button2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button3 = onView(
            allOf(
                withId(R.id.btnGoogleLogin), withText("LOGIN WITH GOOGLE"),
                withParent(withParent(withId(R.id.initScreen_))),
                isDisplayed()
            )
        )
        button3.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val imageView = onView(
            allOf(
                withParent(withParent(withId(R.id.initScreen_))),
                isDisplayed()
            )
        )
        imageView.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton = onView(
            allOf(
                withId(R.id.btnShowReg), withText("Create An Account"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.initScreen_),
                        0
                    ),
                    7
                ),
                isDisplayed()
            )
        )
        appCompatButton.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val editText = onView(
            allOf(
                withId(R.id.txtFirstName), withText("First Name"),
                withParent(withParent(withId(R.id.initScreen_))),
                isDisplayed()
            )
        )
        editText.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val editText2 = onView(
            allOf(
                withId(R.id.txtSurname), withText("Surname"),
                withParent(withParent(withId(R.id.initScreen_))),
                isDisplayed()
            )
        )
        editText2.check(matches(withText("Surname")))

        onView(isRoot()).perform(waitFor(3000));
        val editText3 = onView(
            allOf(
                withId(R.id.txtEmail_logreg), withText("Email"),
                withParent(withParent(withId(R.id.initScreen_))),
                isDisplayed()
            )
        )
        editText3.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val editText4 = onView(
            allOf(
                withId(R.id.txtPassword), withText("Password"),
                withParent(withParent(withId(R.id.initScreen_))),
                isDisplayed()
            )
        )
        editText4.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val imageButton = onView(
            allOf(
                withId(R.id.btnShowLogin),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        imageButton.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val button4 = onView(
            allOf(
                withId(R.id.btnRegister), withText("REGISTER WITH EMAIL"),
                withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                isDisplayed()
            )
        )
        button4.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText = onView(
            allOf(
                withId(R.id.txtFirstName),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.initScreen_),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("Test"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.txtSurname),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.initScreen_),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("User"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText3 = onView(
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
        appCompatEditText3.perform(replaceText("test@gmail.com"), closeSoftKeyboard())


        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText4 = onView(
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
        appCompatEditText4.perform(replaceText("password"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton2 = onView(
            allOf(
                withId(R.id.btnRegister), withText("Register With Email"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("android.widget.LinearLayout")),
                        8
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatButton2.perform(click())
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
