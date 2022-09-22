package com.example.todos_list

import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.todos_list.ui.*
import com.example.todos_list.ui.AddOrEditTodoFragment
import com.example.todos_list.ui.TodoDetailFragment
import com.example.todos_list.ui.TodosListFragment
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTests {

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var navController: TestNavHostController

    // Use Generic type with fragment as upper bound to pass any type of FragmentScenario
    private fun <T : Fragment> init(scenario: FragmentScenario<T>) {

        navController = TestNavHostController(appContext)

        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    /**
     * Test to determine if the user is taken to [AddOrEditTodoFragment] after having clicked on
     * floating action button when on [TodosListFragment]
     */
    @Test
    fun navigateToAddOrEditTodoFragmentFromTodosListFragmentTest() {
        val scenario =
            launchFragmentInContainer<TodosListFragment>(themeResId = R.style.Theme_Todoslist)
        init(scenario)
        scenario.onFragment {
            navController.setCurrentDestination(R.id.todosListFragment)
        }
        onView(withId(R.id.todo_add_fab))
            .perform(click())
        assertEquals(
            "Navigation to AddOrEditTodoFragment failed",
            navController.currentDestination?.id,
            R.id.addOrEditTodoFragment
        )
    }

    /**
     * Test to determine if,  when a [RecyclerView] item is clicked, user is taken to [TodoDetailFragment]
     */
    @Test
    fun navigateToDetailFragmentTest() {
        val scenario = launchFragmentInContainer<TodosListFragment>(null, R.style.Theme_Todoslist)
        init(scenario)
        onView(withId(R.id.todos_list_recycler_view))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            .perform(click())
        assertEquals(
            "Navigation to TodoDetailFragment failed",
            navController.currentDestination?.id,
            R.id.todoDetailFragment
        )
    }

    /**
     * Test to determine if user is taken back to [TodosListFragment] after having clicked on
     * Toolbar arrow button when on [TodoDetailFragment]
     */
    @Test
    fun navigateToTodosListFragmentOnNavigateUpFromTodoDetailFragmentTest() {
        val args = TodoDetailFragmentArgs(2)
        val bundle = args.toBundle()
        val scenario = launchFragmentInContainer<TodoDetailFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Todoslist
        )
        init(scenario)
        scenario.onFragment {
            navController.setCurrentDestination(R.id.todoDetailFragment, bundle)
            navController.navigateUp()
        }
        assertEquals(navController.currentDestination?.id, R.id.todosListFragment)
    }

    /**
     * Test to determine if user is taken back to [TodosListFragment] after having clicked on
     * Toolbar arrow button when on [AddOrEditTodoFragment]
     */
    @Test
    fun navigateToTodosListFragmentOnNavigateUpFromAddOrEditFragmentTest() {
        val args = AddOrEditTodoFragmentArgs(
            appContext.getString(R.string.add_fragment_title),
            2
        )
        val bundle = args.toBundle()
        val scenario = launchFragmentInContainer<AddOrEditTodoFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Theme_Todoslist
        )
        init(scenario)
        scenario.onFragment {
            navController.setCurrentDestination(R.id.addOrEditTodoFragment, bundle)
            navController.navigateUp()
        }
        assertEquals(navController.currentDestination?.id, R.id.todosListFragment)
    }

    /**
     * Test to determine if user is taken to [AddOrEditTodoFragment] after he'd clicked on
     * Edit button when on [TodoDetailFragment]
     */
    @Test
    fun navigateToAddOrEditFragmentOnEditButtonClickTest() {
        val args = TodoDetailFragmentArgs(2)
        val bundle = args.toBundle()
        val scenario = launchFragmentInContainer<TodoDetailFragment>(bundle, R.style.Theme_Todoslist)
        init(scenario)
        scenario.onFragment {
            navController.setCurrentDestination(R.id.todoDetailFragment, bundle)
        }
        onView(withId(R.id.todo_edit_button))
            .perform(click())
        assertEquals(navController.currentDestination?.id, R.id.addOrEditTodoFragment)
    }
}