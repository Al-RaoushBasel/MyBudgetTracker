package com.example.my_budget_tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing the budget table in the Room database.
 * Ensures only one row exists for the overall budget.
 */
@Entity(tableName = "budget")
data class Budget(
    @PrimaryKey
    val id: Int = 1, // Fixed ID to enforce a single-row table
    val overallBudget: Double = 0.0, // Total budget allocated
    val monthlyIncome: Double = 0.0, // User's monthly income
    val totalExpenses: Double = 0.0, // Total expenses recorded
    val remainingBudget: Double = 0.0 // Budget left after expenses
)
