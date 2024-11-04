package com.example.my_budget_tracker

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.my_budget_tracker.databinding.ActivityMainBinding
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.View

import androidx.navigation.NavController

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_records, R.id.navigation_analysis,
                R.id.navigation_budget, R.id.navigation_categories
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        val navView: BottomNavigationView = binding.navView
        navView.setupWithNavController(navController)

        // Set up navigation for the floating action button
        binding.addExpenseFab.setOnClickListener {
            navController.navigate(R.id.addExpenseFragment)
        }

        // Observe navigation changes to control FAB visibility
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Show FAB only on specific fragments
            binding.addExpenseFab.visibility = when (destination.id) {
                R.id.navigation_records, R.id.navigation_analysis -> View.VISIBLE
                else -> View.GONE
            }
        }
    }
}
