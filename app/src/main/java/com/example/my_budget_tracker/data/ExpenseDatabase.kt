package com.example.my_budget_tracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Singleton class representing the Room database for the application.
 * Contains DAOs for accessing expense, budget, and category budget data.
 */
@Database(
    entities = [Expense::class, Budget::class, CategoryBudget::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class ExpenseDatabase : RoomDatabase() {

    /**
     * Provides access to Expense-related database operations.
     */
    abstract fun expenseDao(): ExpenseDao

    /**
     * Provides access to Budget-related database operations.
     */
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: ExpenseDatabase? = null

        /**
         * Returns the singleton instance of the ExpenseDatabase.
         * Ensures only one instance of the database exists in the application.
         *
         * @param context The application context.
         * @return The ExpenseDatabase instance.
         */
        fun getDatabase(context: Context): ExpenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseDatabase::class.java,
                    "expense_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
