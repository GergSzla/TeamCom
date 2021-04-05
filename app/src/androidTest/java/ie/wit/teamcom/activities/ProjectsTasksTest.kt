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
class ProjectsTasksTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SplashActivity::class.java)

    @Test
    fun projectsTasksTest() {

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
                        withId(R.id.nav_tasks),
                        childAtPosition(
                                allOf(
                                        withId(R.id.design_navigation_view),
                                        childAtPosition(
                                                withId(R.id.navView),
                                                0
                                        )
                                ),
                                9
                        ),
                        isDisplayed()
                )
        )
        navigationMenuItemView.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val textView = onView(
                allOf(
                        withId(R.id.txtEmpty_projects),
                        withText("No projects yet. Why not create one? :)"),
                        withParent(withParent(withId(R.id.homeFrame))),
                        isDisplayed()
                )
        )
        textView.check(matches(withText("No projects yet. Why not create one? :)")))

        onView(isRoot()).perform(waitFor(3000));
        val imageButton = onView(
                allOf(
                        withId(R.id.btn_add_project),
                        withParent(withParent(withId(R.id.homeFrame))),
                        isDisplayed()
                )
        )
        imageButton.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val floatingActionButton = onView(
                allOf(
                        withId(R.id.btn_add_project),
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
                        withId(R.id.txt_project_name),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                2
                        )
                )
        )
        appCompatEditText4.perform(scrollTo(), replaceText("Test Project"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText5 = onView(
                allOf(
                        withId(R.id.txt_project_desc),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0
                                ),
                                4
                        )
                )
        )
        appCompatEditText5.perform(scrollTo(), replaceText("test desc"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatImageButton2 = onView(
                allOf(
                        withId(R.id.btnProjectDateSelect),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.LinearLayout")),
                                        5
                                ),
                                1
                        )
                )
        )
        appCompatImageButton2.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val datePicker = onView(
                allOf(
                        IsInstanceOf.instanceOf(android.widget.DatePicker::class.java),
                        withParent(
                                allOf(
                                        withId(android.R.id.custom),
                                        withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))
                                )
                        ),
                        isDisplayed()
                )
        )
        datePicker.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton2 = onView(
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
        appCompatButton2.perform(scrollTo(), click())

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
        val appCompatCheckBox = onView(
                allOf(
                        withId(R.id.checkboxStage3),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.group3),
                                        0
                                ),
                                1
                        )
                )
        )
        appCompatCheckBox.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton4 = onView(
                allOf(
                        withId(R.id.btnSaveStages), withText("Done"),
                        childAtPosition(
                                allOf(
                                        withId(R.id.group6),
                                        childAtPosition(
                                                withClassName(`is`("android.widget.LinearLayout")),
                                                12
                                        )
                                ),
                                3
                        )
                )
        )
        appCompatButton4.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val textView2 = onView(
                allOf(
                        withId(R.id.txt_completed_tasks), withText("0"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        textView2.check(matches(withText("0")))

        onView(isRoot()).perform(waitFor(3000));
        val textView3 = onView(
                allOf(
                        withId(R.id.txt_pending_tasks), withText("0"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        textView3.check(matches(withText("0")))

        onView(isRoot()).perform(waitFor(3000));
        val textView4 = onView(
                allOf(
                        withId(R.id.txt_proj_name), withText("Test Project"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        textView4.check(matches(withText("Test Project")))

        onView(isRoot()).perform(waitFor(3000));
        val recyclerView2 = onView(
                allOf(
                        withId(R.id.projectsRecyclerView),
                        childAtPosition(
                                withId(R.id.swiperefreshProjects),
                                0
                        )
                )
        )
        recyclerView2.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        onView(isRoot()).perform(waitFor(3000));
        val imageButton2 = onView(
                allOf(
                        withId(R.id.fab_expand_menu_button),
                        withParent(
                                allOf(
                                        withId(R.id.proj),
                                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                                )
                        ),
                        isDisplayed()
                )
        )
        imageButton2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val viewInteraction = onView(
                allOf(
                        withId(R.id.fab_expand_menu_button),
                        childAtPosition(
                                allOf(
                                        withId(R.id.proj),
                                        childAtPosition(
                                                withClassName(`is`("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                                                1
                                        )
                                ),
                                2
                        ),
                        isDisplayed()
                )
        )
        viewInteraction.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val imageButton3 = onView(
                allOf(
                        withId(R.id.btnEditProject),
                        withParent(
                                allOf(
                                        withId(R.id.proj),
                                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                                )
                        ),
                        isDisplayed()
                )
        )
        imageButton3.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val imageButton4 = onView(
                allOf(
                        withId(R.id.btnCreateTask),
                        withParent(
                                allOf(
                                        withId(R.id.proj),
                                        withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))
                                )
                        ),
                        isDisplayed()
                )
        )
        imageButton4.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val floatingActionButton2 = onView(
                allOf(
                        withId(R.id.btnCreateTask),
                        childAtPosition(
                                allOf(
                                        withId(R.id.proj),
                                        childAtPosition(
                                                withClassName(`is`("androidx.coordinatorlayout.widget.CoordinatorLayout")),
                                                1
                                        )
                                ),
                                1
                        ),
                        isDisplayed()
                )
        )
        floatingActionButton2.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText6 = onView(
                allOf(
                        withId(R.id.txtTaskName),
                        childAtPosition(
                                allOf(
                                        withId(R.id.group1),
                                        childAtPosition(
                                                withClassName(`is`("android.widget.LinearLayout")),
                                                1
                                        )
                                ),
                                1
                        )
                )
        )
        appCompatEditText6.perform(scrollTo(), replaceText("test task 1"), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText7 = onView(
                allOf(
                        withId(R.id.txtTaskDesc),
                        childAtPosition(
                                allOf(
                                        withId(R.id.group1),
                                        childAtPosition(
                                                withClassName(`is`("android.widget.LinearLayout")),
                                                1
                                        )
                                ),
                                3
                        )
                )
        )
        appCompatEditText7.perform(scrollTo(), replaceText("test Desc "), closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatSpinner = onView(
                allOf(
                        withId(R.id.spinnerStage),
                        childAtPosition(
                                allOf(
                                        withId(R.id.group1),
                                        childAtPosition(
                                                withClassName(`is`("android.widget.LinearLayout")),
                                                1
                                        )
                                ),
                                5
                        )
                )
        )
        appCompatSpinner.perform(scrollTo(), click())

//        onView(isRoot()).perform(waitFor(3000));
//        val appCompatTextView = onData(anything())
//            .inAdapterView(
//                    childAtPosition(
//                            withClassName(`is`("android.widget.PopupWindow$PopupBackgroundView")),
//                            0
//                    )
//            )
//            .atPosition(2)
//        appCompatTextView.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText8 = onView(
                allOf(
                        withId(R.id.txtTaskDesc), withText("test Desc "),
                        childAtPosition(
                                allOf(
                                        withId(R.id.group1),
                                        childAtPosition(
                                                withClassName(`is`("android.widget.LinearLayout")),
                                                1
                                        )
                                ),
                                3
                        )
                )
        )
        appCompatEditText8.perform(scrollTo(), replaceText("test Desc "))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatEditText9 = onView(
                allOf(
                        withId(R.id.txtTaskDesc), withText("test Desc "),
                        childAtPosition(
                                allOf(
                                        withId(R.id.group1),
                                        childAtPosition(
                                                withClassName(`is`("android.widget.LinearLayout")),
                                                1
                                        )
                                ),
                                3
                        ),
                        isDisplayed()
                )
        )
        appCompatEditText9.perform(closeSoftKeyboard())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatSpinner2 = onView(
                allOf(
                        withId(R.id.spinnerAssignee),
                        childAtPosition(
                                allOf(
                                        withId(R.id.group1),
                                        childAtPosition(
                                                withClassName(`is`("android.widget.LinearLayout")),
                                                1
                                        )
                                ),
                                7
                        )
                )
        )
        appCompatSpinner2.perform(scrollTo(), click())

//        onView(isRoot()).perform(waitFor(3000));
//        val appCompatTextView2 = onData(anything())
//            .inAdapterView(
//                    childAtPosition(
//                            withClassName(`is`("android.widget.PopupWindow$PopupBackgroundView")),
//                            0
//                    )
//            )
//            .atPosition(1)
//        appCompatTextView2.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatButton5 = onView(
                allOf(
                        withId(R.id.btnAddTask), withText("Add Task"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.group1),
                                        13
                                ),
                                0
                        )
                )
        )
        appCompatButton5.perform(scrollTo(), click())

        onView(isRoot()).perform(waitFor(3000));
        val appCompatImageButton3 = onView(
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
        appCompatImageButton3.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val navigationMenuItemView2 = onView(
                allOf(
                        withId(R.id.nav_tasks),
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
        navigationMenuItemView2.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val textView5 = onView(
                allOf(
                        withId(R.id.txt_pending_tasks), withText("1"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        textView5.check(matches(withText("1")))

        onView(isRoot()).perform(waitFor(3000));
        val recyclerView3 = onView(
                allOf(
                        withId(R.id.projectsRecyclerView),
                        childAtPosition(
                                withId(R.id.swiperefreshProjects),
                                0
                        )
                )
        )
        recyclerView3.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        onView(isRoot()).perform(waitFor(3000));
        val textView6 = onView(
                allOf(
                        withId(R.id.txtStage3Title), withText("To Do"),
                        withParent(withParent(withId(R.id.swiperefreshTasks))),
                        isDisplayed()
                )
        )
        textView6.check(matches(withText("To Do")))

        onView(isRoot()).perform(waitFor(3000));
        val recyclerView4 = onView(
                allOf(
                        withId(R.id.tasks3RecyclerView),
                        childAtPosition(
                                withClassName(`is`("android.widget.LinearLayout")),
                                1
                        )
                )
        )
        recyclerView4.perform(actionOnItemAtPosition<ViewHolder>(0, click()))

        onView(isRoot()).perform(waitFor(3000));
        val frameLayout = onView(
                allOf(
                        withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java)),
                        isDisplayed()
                )
        )
        frameLayout.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val textView7 = onView(
                allOf(
                        withId(R.id.txtTaskName), withText("test task 1"),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()
                )
        )
        textView7.check(matches(withText("test task 1")))

        onView(isRoot()).perform(waitFor(3000));
        val textView8 = onView(
                allOf(
                        withId(R.id.txtTaskDesc), withText("test Desc "),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()
                )
        )
        textView8.check(matches(withText("test Desc ")))

        onView(isRoot()).perform(waitFor(3000));
        val textView9 = onView(
                allOf(
                        withId(R.id.txtTaskProgress), withText("1/5"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.LinearLayout::class.java))),
                        isDisplayed()
                )
        )
        textView9.check(matches(withText("1/5")))

        onView(isRoot()).perform(waitFor(3000));
        val appCompatTextView3 = onView(
                allOf(
                        withId(R.id.txtTaskStage), withText("To Do"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.LinearLayout")),
                                        9
                                ),
                                0
                        ),
                        isDisplayed()
                )
        )
        appCompatTextView3.perform(click())

        onView(isRoot()).perform(waitFor(3000));
        val frameLayout2 = onView(
                allOf(
                        withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java)),
                        isDisplayed()
                )
        )
        frameLayout2.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val textView10 = onView(
                allOf(
                        withId(R.id.txtTaskName), withText("Completed"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        textView10.check(matches(withText("Completed")))

        onView(isRoot()).perform(waitFor(3000));
        val textView11 = onView(
                allOf(
                        withId(R.id.txtTaskName), withText("In Progress"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        textView11.check(matches(withText("In Progress")))

        onView(isRoot()).perform(waitFor(3000));
        val textView12 = onView(
                allOf(
                        withId(R.id.txtTaskName), withText("To Do"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java))),
                        isDisplayed()
                )
        )
        textView12.check(matches(withText("To Do")))

        onView(isRoot()).perform(waitFor(3000));
        val imageButton5 = onView(
                allOf(
                        withId(R.id.btn_cancel_stage),
                        withParent(withParent(withId(android.R.id.content))),
                        isDisplayed()
                )
        )
        imageButton5.check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(3000));
        val recyclerView5 = onView(
                allOf(
                        withId(R.id.stage_rec),
                        childAtPosition(
                                withClassName(`is`("android.widget.LinearLayout")),
                                1
                        )
                )
        )
        recyclerView5.perform(actionOnItemAtPosition<ViewHolder>(1, click()))
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
