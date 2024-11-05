package com.example.my_budget_tracker.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.my_budget_tracker.data.BudgetDao
import com.example.my_budget_tracker.ViewModel.BudgetViewModel

class BudgetViewModelFactory(private val budgetDao: BudgetDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(budgetDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
