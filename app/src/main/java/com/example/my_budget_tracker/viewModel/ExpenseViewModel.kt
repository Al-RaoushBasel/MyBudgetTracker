package com.example.my_budget_tracker.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_budget_tracker.data.CurrencyManager
import com.example.my_budget_tracker.data.Expense
import com.example.my_budget_tracker.data.ExpenseRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for managing expense data and business logic.
 */
class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    // --------------------------- Properties ---------------------------

    // LiveData for all expenses, exposed to the UI
    private val _allExpenses = MutableLiveData<List<Expense>>()
    val allExpenses: LiveData<List<Expense>> = repository.getAllExpenses()

    init {
        fetchAllExpenses() // Load expenses on initialization
    }

    // --------------------------- Data Fetching ---------------------------

    /**
     * Fetches all expenses from the repository, converts them to the selected currency,
     * and updates LiveData.
     */
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

    // --------------------------- Insert Operations ---------------------------

    /**
     * Inserts a new expense and refreshes the expense list.
     */
    fun insertExpense(expense: Expense) = viewModelScope.launch {
        repository.insertExpense(expense)
        fetchAllExpenses() // Refresh the list after insertion
    }

    // --------------------------- Delete Operations ---------------------------

    /**
     * Deletes all expenses and clears the LiveData.
     */
    fun deleteAllExpenses() = viewModelScope.launch {
        repository.deleteAllExpenses()
        _allExpenses.postValue(emptyList()) // Clear LiveData after deletion
    }

    // --------------------------- Refresh Operations ---------------------------

    /**
     * Refreshes the expense list, ensuring values are converted to the selected currency.
     */
    @Suppress("unused")
    fun refreshExpenses() {
        fetchAllExpenses()
    }
}
