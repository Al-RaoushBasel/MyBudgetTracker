package com.example.my_budget_tracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Data Access Object (DAO) for managing expense-related database operations.
 */
@Dao
interface ExpenseDao {

    /**
     * Inserts a new expense into the database.
     * @param expense The expense object to insert.
     */
    @Insert
    suspend fun insertExpense(expense: Expense)

    /**
     * Retrieves all expenses, ordered by date in descending order.
     * @return A LiveData list of all expenses.
     */
    @Query("SELECT * FROM expense ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    /**
     * Retrieves the total sum of all expenses.
     * @return A LiveData object containing the total expense amount.
     */
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expense")
    fun getTotalExpenses(): LiveData<Double>

    /**
     * Retrieves the total sum of expenses for a specific category.
     * @param categoryName The name of the category to filter expenses by.
     * @return A LiveData object containing the total expense amount for the category.
     */
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expense WHERE category = :categoryName")
    fun getTotalExpensesForCategory(categoryName: String): LiveData<Double>

    /**
     * Retrieves all expenses as a regular list (not LiveData).
     * @return A list of all expenses in the database.
     */
    @Query("SELECT * FROM expense")
    suspend fun getAllExpensesValue(): List<Expense>

    /**
     * Deletes all expense records from the database.
     */
    @Query("DELETE FROM expense")
    suspend fun deleteAllExpenses()

    /**
     * Retrieves the total sum of all expenses directly (not LiveData).
     * @return The total expense amount or null if no expenses exist.
     */
    @Query("SELECT SUM(amount) FROM expense")
    suspend fun getTotalExpensesDirectly(): Double?
}
