package com.example.my_budget_tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing a category-specific budget in the Room database.
 */
@Entity(tableName = "category_budget")
data class CategoryBudget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Unique ID for each category budget, auto-generated
    val categoryName: String, // Name of the category (e.g., Food, Entertainment)
    val budgetAmount: Double, // Total budget allocated to the category
    val remainingAmount: Double = 0.0 // Remaining budget after expenses
)
