package com.example.my_budget_tracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * Splash screen activity that navigates to the MainActivity when the "Get Started" button is clicked.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        setupGetStartedButton()
    }

    /**
     * Sets up the "Get Started" button to navigate to MainActivity and finish the current activity.
     */
    private fun setupGetStartedButton() {
        val getStartedButton = findViewById<Button>(R.id.getStartedButton)
        getStartedButton.setOnClickListener {
            navigateToMainActivity()
        }
    }

    /**
     * Navigates to MainActivity and finishes SplashActivity.
     */
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Ensures the user cannot return to this activity by pressing the back button
    }
}
