package com.example.my_budget_tracker.data

import androidx.lifecycle.LiveData

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    // Function to insert an expense
    suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    // Get all expenses as LiveData
    fun getAllExpenses(): LiveData<List<Expense>> = expenseDao.getAllExpenses()

    // Get all expenses as a synchronous list for conversions
    suspend fun getAllExpensesValue(): List<Expense> {
        return expenseDao.getAllExpensesValue() // This assumes you add the corresponding method in the DAO
    }

    // Function to delete all expenses
    suspend fun deleteAllExpenses() {
        expenseDao.deleteAllExpenses()
    }
}
