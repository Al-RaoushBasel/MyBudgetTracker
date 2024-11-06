package com.example.my_budget_tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget")
data class Budget(
    @PrimaryKey val id: Int = 1, // Ensures only one row exists
    val overallBudget: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val remainingBudget: Double = 0.0
)

