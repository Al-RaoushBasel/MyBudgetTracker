package com.example.my_budget_tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget")
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monthlyIncome: Double = 0.0,    // User's total monthly income
    val overallBudget: Double = 0.0,
    val amount: Double = 0.0,    // Total budget amount set by the user
    val remainingBudget: Double = overallBudget - amount  // Optional: can dynamically adjust based on usage
)
