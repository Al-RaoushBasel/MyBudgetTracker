package com.example.my_budget_tracker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.my_budget_tracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // --------------------------- Lifecycle Methods ---------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleNotificationPermission()
        setupNavigation()
    }

    /**
     * Handles the result of permission requests.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
                createNotificationChannel()
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --------------------------- Permission Handling ---------------------------

    /**
     * Handles notification permissions for Android 13+ and creates notification channels.
     */
    private fun handleNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            } else {
                createNotificationChannel()
            }
        } else {
            createNotificationChannel()
        }
    }

    // --------------------------- Navigation Setup ---------------------------

    /**
     * Sets up navigation components, including the BottomNavigationView and FAB navigation.
     */
    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_records,
                R.id.navigation_analysis,
                R.id.navigation_budget,
                R.id.navigation_categories
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        // Set up navigation for the floating action button
        binding.addExpenseFab.setOnClickListener {
            navController.navigate(R.id.addExpenseFragment)
        }

        // Control FAB visibility based on navigation destinations
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.addExpenseFab.visibility = when (destination.id) {
                R.id.navigation_records, R.id.navigation_analysis -> View.VISIBLE
                else -> View.GONE
            }
        }
    }

    // --------------------------- Notification Setup ---------------------------

    /**
     * Creates a notification channel for budget notifications.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Budget Notifications"
            val descriptionText = "Notifications for budget exceed events"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("budget_channel", channelName, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
