package com.example.my_budget_tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity class representing an expense in the Room database.
 */
@Entity(tableName = "expense")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Auto-generated unique ID for each expense

    val name: String, // Name of the expense (e.g., "Bills", "Food")

    val amount: Double, // Amount spent

    val icon: Int, // Icon identifier for representing the expense visually

    val date: Date, // Date the expense was made

    val category: String, // Category of the expense (e.g., "Food", "Health")

    val currency: String // Currency in which the expense was made (e.g., "USD", "EUR")
)
