package com.example.todos_list.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.todos_list.R
import com.example.todos_list.TODO_NOTIFICATION_CHANNEL_ID
import com.example.todos_list.TODO_NOTIFICATION_CHANNEL_NAME
import com.example.todos_list.ui.MainActivity

fun makeTodoNotification(
    appContext: Context,
    todoId: Int,
    todoName: String,
    todoDescription: String?
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create the NotificationChannel
        val name = TODO_NOTIFICATION_CHANNEL_NAME
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(TODO_NOTIFICATION_CHANNEL_ID, name, importance)
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        // Create a PendingIntent to lead user to the TodoDetailFragment if he clicks on the notification
        val bundle = Bundle()
        bundle.putInt("todo_id", todoId)
        val pendingIntent = NavDeepLinkBuilder(appContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.todoDetailFragment)
            .setArguments(bundle)
            .createPendingIntent()

        // Create the notification
        val builder = NotificationCompat.Builder(appContext, TODO_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(todoName)
            .setContentText(todoDescription)
            .setSmallIcon(R.drawable.ic_reminder)
            .setContentIntent(pendingIntent)
            .setVibrate(LongArray(0))
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Show the notification
        val notificationId = System.currentTimeMillis().toInt()
        NotificationManagerCompat.from(appContext).notify(notificationId, builder.build())
    }
}