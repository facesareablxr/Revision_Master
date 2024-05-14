package uk.ac.aber.dcs.cs31620.revisionmaster.model.util

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import uk.ac.aber.dcs.cs31620.revisionmaster.R
import uk.ac.aber.dcs.cs31620.revisionmaster.model.database.viewmodel.UserViewModel

class ScheduleNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { ctx ->
            // Ensure we have the ViewModel instance
            val viewModel =
                ViewModelProvider.AndroidViewModelFactory.getInstance(ctx.applicationContext as Application)
                    .create(UserViewModel::class.java)

            // Fetch schedules
            viewModel.schedules.observeForever { schedules ->
                // Clear previous alarms if any
                val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(
                    PendingIntent.getBroadcast(
                        ctx,
                        0,
                        Intent(ctx, NotificationReceiver::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )

                schedules.forEachIndexed { index, schedule ->
                    schedule.startTime?.let { startTime ->
                        schedule.description?.let { title ->
                            // Schedule notification
                            scheduleNotification(ctx, title, startTime * 1000, index)
                        }
                    }
                }
            }
        }
    }

    // Change scheduleNotification function signature to include index parameter
    private fun scheduleNotification(context: Context, title: String, startTime: Long, index: Int) {
        // Use index as the request code for PendingIntent
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            index,
            Intent(context, NotificationReceiver::class.java).apply {
                putExtra("title", title)
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, startTime, pendingIntent)
    }
}

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val title = intent?.getStringExtra("title")
            showNotification(it, title ?: "Scheduled Session", "Your session is about to start!")
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(context, "default_channel")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(hashCode(), notificationBuilder.build())
    }
}