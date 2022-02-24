package idv.zijunliao.note

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import idv.zijunliao.note.di.AppModule
import idv.zijunliao.note.presentation.activities.MainActivity
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@UninstallModules(AppModule::class)
@HiltAndroidTest
class MainActivityTest {
    lateinit var mainActivity: MainActivity

    @get:Rule
    val hilt = HiltAndroidRule(this)

    @Before
    fun launch() {
        hilt.inject()
        ActivityScenario.launch(MainActivity::class.java).onActivity {
            mainActivity = it
        }
    }

    @Test
    fun testNoteInsert() {
        val time = System.currentTimeMillis()
        val title = "test title $time"
        val content = "test content $time"
        onView(withId(R.id.addNote)).perform(click())
        onView(withId(R.id.amber)).perform(click())
        onView(withHint(R.string.hint_title)).perform(typeText(title)).check(matches(isDisplayed()))
        onView(withHint(R.string.hint_content)).perform(typeText(content)).check(matches(isDisplayed()))
        onView(withId(R.id.menu_save)).perform(click())
        onView(withText(title)).check(matches(isDisplayed()))
        onView(withText(content)).check(matches(isDisplayed()))
    }


    @Test
    fun testNoteEdit() {
        mainActivity.dummy()
        val time = System.currentTimeMillis().toString()
        val title = "test title"
        val content = "test content"
        onView(withText(title)).perform(click())
        onView(withText(title)).check(matches(isDisplayed()))
        onView(withText(content)).check(matches(isDisplayed()))
        onView(withId(R.id.menu_edit)).perform(click())
        onView(withHint(R.string.hint_title)).perform(typeText(time)).check(matches(isDisplayed()))
        onView(withHint(R.string.hint_content)).perform(typeText(time)).check(matches(isDisplayed()))
        onView(withId(R.id.menu_save)).perform(click())
        onView(withText("$title$time")).check(matches(isDisplayed()))
        onView(withText("$content$time")).check(matches(isDisplayed()))
    }


    @Test
    fun testNoteDelete() {
        mainActivity.dummy()
        val title = "test title"
        onView(withText(title)).perform(swipeLeft())
        onView(withText(R.string.snack_message)).check(matches(isDisplayed()))
    }

    @Test
    fun testNoteRestore() {
        mainActivity.dummy()
        val title = "test title"
        onView(withText(title)).perform(swipeLeft())
        onView(withText(R.string.snack_message)).check(matches(isDisplayed()))
        onView(withText(R.string.snack_action)).perform(click())
        onView(withText(title)).check(matches(isDisplayed()))
    }

    @Test
    fun testNoteTitleBlank() {
        val content = "test content"
        onView(withId(R.id.addNote)).perform(click())
        onView(withHint(R.string.hint_content)).perform(typeText(content)).check(matches(isDisplayed()))
        onView(withId(R.id.menu_save)).perform(click())
        onView(withText("Note title cannot be empty.")).inRoot(withDecorView(not(mainActivity.window.decorView))).check(matches(isDisplayed()))
    }

    @Test
    fun testNoteContentBlank() {
        val title = "test title"
        onView(withId(R.id.addNote)).perform(click())
        onView(withHint(R.string.hint_title)).perform(typeText(title)).check(matches(isDisplayed()))
        onView(withId(R.id.menu_save)).perform(click())
        onView(withText("Note content cannot be empty.")).inRoot(withDecorView(not(mainActivity.window.decorView))).check(matches(isDisplayed()))
    }
}