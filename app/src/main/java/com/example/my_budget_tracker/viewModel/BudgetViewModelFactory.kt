package com.example.my_budget_tracker.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.my_budget_tracker.data.BudgetDao
import com.example.my_budget_tracker.data.ExpenseDao

/**
 * Factory for creating a [BudgetViewModel] instance with the required dependencies.
 */
class BudgetViewModelFactory(
    private val application: Application, // Application context required for ViewModel
    private val budgetDao: BudgetDao,
    private val expenseDao: ExpenseDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            return BudgetViewModel(application, budgetDao, expenseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
