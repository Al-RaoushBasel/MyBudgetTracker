package com.example.my_budget_tracker.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_budget_tracker.data.CurrencyManager
import kotlinx.coroutines.launch
import com.example.my_budget_tracker.data.Expense
import com.example.my_budget_tracker.data.ExpenseRepository

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    // Expose all expenses as LiveData to be observed in the UI
    private val _allExpenses = MutableLiveData<List<Expense>>()
    val allExpenses: LiveData<List<Expense>> = repository.getAllExpenses()

    init {
        fetchAllExpenses()
    }

    // Fetch all expenses and convert them to the selected currency
    private fun fetchAllExpenses() {
        viewModelScope.launch {
            val expenses = repository.getAllExpensesValue()
            val convertedExpenses = expenses.map { expense ->
                expense.copy(
                    amount = CurrencyManager.convertAmount(
                        expense.amount,
                        expense.currency,
                        CurrencyManager.selectedCurrency
                    ),
                    currency = CurrencyManager.selectedCurrency
                )
            }
            _allExpenses.postValue(convertedExpenses)
        }
    }

    // Function to insert an expense
    fun insertExpense(expense: Expense) = viewModelScope.launch {
        repository.insertExpense(expense)
        fetchAllExpenses() // Refresh the expenses after insertion
    }

    // Function to delete all expenses
    fun deleteAllExpenses() = viewModelScope.launch {
        repository.deleteAllExpenses()
        _allExpenses.postValue(emptyList()) // Clear the expenses in LiveData
    }

    // Refresh expenses when the currency changes
    fun refreshExpenses() {
        fetchAllExpenses()
    }
}
