package com.example.my_budget_tracker.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.my_budget_tracker.data.Expense
import com.example.my_budget_tracker.data.ExpenseRepository

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    // Expose all expenses as LiveData to be observed in the UI
    val allExpenses: LiveData<List<Expense>> = repository.getAllExpenses()

    // Function to insert an expense
    fun insertExpense(expense: Expense) = viewModelScope.launch {
        repository.insertExpense(expense)
    }

    // Function to delete all expenses
    fun deleteAllExpenses() = viewModelScope.launch {
        repository.deleteAllExpenses()
    }
}



