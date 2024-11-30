package com.example.my_budget_tracker.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_budget_tracker.data.Budget
import com.example.my_budget_tracker.data.BudgetDao
import com.example.my_budget_tracker.data.CategoryBudget
import com.example.my_budget_tracker.data.ExpenseDao
import com.example.my_budget_tracker.receiver.BudgetExceededReceiver
import kotlinx.coroutines.launch

class BudgetViewModel(
    application: Application, // Add this parameter
    private val budgetDao: BudgetDao,
    private val expenseDao: ExpenseDao
) : AndroidViewModel(application) { // Extend AndroidViewModel

    // LiveData to observe budget data
    val budget: LiveData<Budget?> = budgetDao.getBudget()

    // LiveData for observing total expenses directly
    val totalExpenses: LiveData<Double> = expenseDao.getTotalExpenses()

    init {
        observeTotalExpenses()
    }

    // Observe total expenses and update remaining budget when they change
    private fun observeTotalExpenses() {
        totalExpenses.observeForever { total ->
            viewModelScope.launch {
                // Directly call a function to update remaining budget whenever expenses change
                updateRemainingBudget(total)
            }
        }
    }

    // Insert or update overall budget without resetting the database
    fun insertOrUpdateBudget(budget: Budget) = viewModelScope.launch {
        budgetDao.insertOrUpdateBudget(budget)
        calculateAndUpdateRemainingBudget()
    }

    private suspend fun updateRemainingBudget(totalExpenses: Double) {
        val budget = budgetDao.getBudget().value
        if (budget != null) {
            val remainingBudget = budget.overallBudget - totalExpenses
            budgetDao.updateRemainingBudget(remainingBudget)
        }
    }

    fun updateCategoryRemainingBudget(categoryName: String, remainingBudget: Double) = viewModelScope.launch {
        budgetDao.updateCategoryRemainingBudget(categoryName, remainingBudget)
    }


    fun resetOverallBudget() = viewModelScope.launch {
        budgetDao.deleteAllBudgets()
        budgetDao.updateRemainingBudget(0.0)
    }

    fun getCategoryExpenses(categoryName: String): LiveData<Double> {
        return expenseDao.getTotalExpensesForCategory(categoryName)
    }


    // Insert or update a category-specific budget
    fun insertOrUpdateCategoryBudget(categoryBudget: CategoryBudget) = viewModelScope.launch {
        budgetDao.insertOrUpdateCategoryBudget(categoryBudget)
    }

    // Fetch category budgets as LiveData
    fun categoryBudgets(): LiveData<List<CategoryBudget>> = budgetDao.getAllCategoryBudgets()

    // Delete all category budgets
    fun deleteAllCategoryBudgets() = viewModelScope.launch {
        budgetDao.deleteAllCategoryBudgets()
    }

    // Calculate and update remaining budget based on current budget values
    private suspend fun calculateAndUpdateRemainingBudget() {
        val budget = budgetDao.getBudget().value
        val totalExpenses = expenseDao.getTotalExpenses().value ?: 0.0
        if (budget != null) {
            val remainingBudget = budget.overallBudget - totalExpenses
            budgetDao.updateRemainingBudget(remainingBudget)
        }
    }


    suspend fun getCategoryBudgetByName(categoryName: String): CategoryBudget? {
        return budgetDao.getCategoryBudgetByName(categoryName)
    }

    fun updateCategoryBudget(categoryBudget: CategoryBudget) {
        viewModelScope.launch {
            budgetDao.updateCategoryBudget(categoryBudget)
        }
    }

    fun insertCategoryBudget(categoryBudget: CategoryBudget) {
        viewModelScope.launch {
            budgetDao.insertCategoryBudget(categoryBudget)
        }
    }


    fun checkBudgetExceeded() = viewModelScope.launch {
        println("checkBudgetExceeded() called")

        val budget = budgetDao.getBudgetDirectly()
        val totalExpenses = expenseDao.getTotalExpensesDirectly() ?: 0.0

        if (budget == null) {
            println("No budget found. Cannot check budget exceeded.")
            return@launch
        }

        println("Total Expenses: $totalExpenses, Overall Budget: ${budget.overallBudget}")

        if (totalExpenses > budget.overallBudget) {
            println("Budget exceeded! Sending broadcast.")

            val context = getApplication<Application>().applicationContext
            val intent = Intent(context, BudgetExceededReceiver::class.java) // Explicit intent
            intent.action = "com.example.my_budget_tracker.BUDGET_EXCEEDED"
            context.sendBroadcast(intent)
        } else {
            println("Budget not exceeded.")
        }
    }






}
