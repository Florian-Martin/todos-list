package com.example.todos_list.worker

import android.util.Log
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import androidx.work.*
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.todos_list.ui.MainActivity
import org.hamcrest.Matchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class TodoReminderNotificationWorkerTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    private val todoName = "name"
    private val todoDescription = "description"
    private var timeout: Long = 10
    private lateinit var uiDevice: UiDevice
    private var workInfo: WorkInfo? = null

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        setupWorkRequest()

        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    private fun setupWorkRequest() {
        // Define input data
        val input = workDataOf("id" to 1, "name" to todoName, "description" to todoDescription)

        // Create request
        val request = OneTimeWorkRequestBuilder<TodoReminderNotificationWorker>()
            .setInputData(input)
            .setInitialDelay(timeout, TimeUnit.SECONDS)
            .build()

        val workManager = WorkManager.getInstance(getApplicationContext())
        // Get the TestDriver
        val testDriver = WorkManagerTestInitHelper.getTestDriver(getApplicationContext())
        workManager.enqueue(request).result.get()
        // Tells the WorkManager test framework that initial delays are now met.
        testDriver?.setInitialDelayMet(request.id)
        workInfo = workManager.getWorkInfoById(request.id).get()
    }

    @Test
    @Throws(Exception::class)
    fun workerResultWithInitialDelayTest() {
        assertThat(workInfo?.state, `is`(WorkInfo.State.SUCCEEDED))
    }

    @Test
    fun notificationDisplayedTest() {
        uiDevice.openNotification()
        uiDevice.wait(Until.hasObject(By.textContains(todoName)), timeout)
        val notification = uiDevice.findObject(UiSelector().textContains(todoName)).exists()
        assertTrue("Could not find text $todoName", notification)
        uiDevice.pressHome()
    }
}