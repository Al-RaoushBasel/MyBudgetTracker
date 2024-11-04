package com.example.my_budget_tracker.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    // Function to insert an expense
    suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    fun getAllExpenses(): LiveData<List<Expense>> = expenseDao.getAllExpenses()




    // Function to delete all expenses
    suspend fun deleteAllExpenses() {
        expenseDao.deleteAllExpenses()
    }
}

