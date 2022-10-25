package com.example.todos_list.worker

import android.content.Context
import android.os.Build.ID
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.todos_list.utils.makeTodoNotification

class TodoReminderNotificationWorker(context: Context, params: WorkerParameters) :
    Worker(context, params) {
    override fun doWork(): Result {
        val appContext = applicationContext
        val todoId = inputData.getInt(ID, 0)
        val todoName = inputData.getString(NAME)
        val todoDescription = inputData.getString(DESCRIPTION)
        return if (todoName != null && todoId != 0) {
            makeTodoNotification(appContext, todoId, todoName, todoDescription)
            Result.success()
        } else {
            Result.failure()
        }
    }

    companion object {
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val ID = "id"
    }
}