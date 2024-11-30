package com.example.my_budget_tracker.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.my_budget_tracker.data.ExpenseRepository

/**
 * Factory class for creating an instance of [ExpenseViewModel].
 * It provides the [ExpenseRepository] as a dependency to the ViewModel.
 */
class ExpenseViewModelFactory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            return ExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
