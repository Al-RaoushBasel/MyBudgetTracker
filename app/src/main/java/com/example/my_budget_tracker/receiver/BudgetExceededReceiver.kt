package com.example.my_budget_tracker.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.my_budget_tracker.R

class BudgetExceededReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        println("BudgetExceededReceiver: onReceive called")

        val action = intent.action
        println("BudgetExceededReceiver: Received intent with action: $action")

        if (action == "com.example.my_budget_tracker.BUDGET_EXCEEDED") {
            println("BudgetExceededReceiver: Handling BUDGET_EXCEEDED action")

            val notification = NotificationCompat.Builder(context, "budget_channel")
                .setSmallIcon(R.drawable.ic_budget)
                .setContentTitle("Budget Exceeded")
                .setContentText("Your expenses have exceeded the overall budget!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                println("BudgetExceededReceiver: Notification permission not granted")
                return
            }

            NotificationManagerCompat.from(context).notify(1, notification)
            println("BudgetExceededReceiver: Notification displayed")
        } else {
            println("BudgetExceededReceiver: Unhandled intent action: $action")
        }
    }
}

