package com.example.my_budget_tracker.data

import androidx.lifecycle.LiveData

/**
 * Repository class for managing Expense data.
 * Acts as a bridge between the DAO and the ViewModel, ensuring separation of concerns.
 */
class ExpenseRepository(private val expenseDao: ExpenseDao) {

    /**
     * Inserts a new expense into the database.
     * @param expense The expense object to insert.
     */
    suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    /**
     * Retrieves all expenses as LiveData for observation.
     * @return A LiveData list of all expenses.
     */
    fun getAllExpenses(): LiveData<List<Expense>> = expenseDao.getAllExpenses()

    /**
     * Retrieves all expenses as a regular list for synchronous operations.
     * @return A list of all expenses in the database.
     */
    suspend fun getAllExpensesValue(): List<Expense> {
        return expenseDao.getAllExpensesValue()
    }

    /**
     * Deletes all expense records from the database.
     */
    suspend fun deleteAllExpenses() {
        expenseDao.deleteAllExpenses()
    }
}
