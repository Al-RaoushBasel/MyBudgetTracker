package com.example.my_budget_tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.my_budget_tracker.data.BudgetDao
import com.example.my_budget_tracker.data.ExpenseDao

class BudgetViewModelFactory(
    private val budgetDao: BudgetDao,
    private val expenseDao: ExpenseDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(budgetDao, expenseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
