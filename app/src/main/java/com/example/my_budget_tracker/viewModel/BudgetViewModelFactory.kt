package com.example.my_budget_tracker.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.my_budget_tracker.data.BudgetDao
import com.example.my_budget_tracker.data.ExpenseDao
import com.example.my_budget_tracker.viewmodel.BudgetViewModel

class BudgetViewModelFactory(
    private val application: Application, // Add the application parameter
    private val budgetDao: BudgetDao,
    private val expenseDao: ExpenseDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            return BudgetViewModel(application, budgetDao, expenseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
