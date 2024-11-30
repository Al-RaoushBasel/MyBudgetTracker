package com.example.my_budget_tracker.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.my_budget_tracker.R

/**
 * BroadcastReceiver to handle budget exceed notifications.
 * Listens for the "BUDGET_EXCEEDED" action and triggers a notification.
 */
class BudgetExceededReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Check if the received action matches "BUDGET_EXCEEDED"
        if (intent.action == "com.example.my_budget_tracker.BUDGET_EXCEEDED") {
            // Build the notification
            val notification = NotificationCompat.Builder(context, "budget_channel")
                .setSmallIcon(R.drawable.ic_budget) // Small icon for the notification
                .setContentTitle("Budget Exceeded") // Notification title
                .setContentText("Your expenses have exceeded the overall budget!") // Notification message
                .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for importance
                .setAutoCancel(true) // Dismiss notification when tapped
                .build()

            // Check for notification permission
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Display the notification
                NotificationManagerCompat.from(context).notify(1, notification)
            }
        }
    }
}
