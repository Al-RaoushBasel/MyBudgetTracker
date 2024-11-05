package com.example.my_budget_tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_budget")
data class CategoryBudget(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryName: String,
    val budgetAmount: Double,
    val remainingAmount: Double = 0.0
)
