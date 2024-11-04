package com.example.my_budget_tracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "expense")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Primary key with auto-generation
    val name: String, // Name of the expense (e.g., "Groceries", "Utilities")
    val amount: Double, // Amount spent
    val icon: Int, // Icon identifier (e.g., resource name or URL)
    val date: Date, // Date of the expense
    val category: String // Category of the expense (e.g., "Food", "Health")
)
