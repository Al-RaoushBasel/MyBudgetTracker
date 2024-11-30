package com.example.my_budget_tracker.viewModel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.my_budget_tracker.data.Budget
import com.example.my_budget_tracker.data.BudgetDao
import com.example.my_budget_tracker.data.CategoryBudget
import com.example.my_budget_tracker.data.ExpenseDao
import com.example.my_budget_tracker.receiver.BudgetExceededReceiver
import kotlinx.coroutines.launch

class BudgetViewModel(
    application: Application,
    private val budgetDao: BudgetDao,
    private val expenseDao: ExpenseDao
) : AndroidViewModel(application) {

    // --------------------------- LiveData Properties ---------------------------

    val budget: LiveData<Budget?> = budgetDao.getBudget()
    val totalExpenses: LiveData<Double> = expenseDao.getTotalExpenses()

    init {
        observeTotalExpenses()
    }

    // --------------------------- Budget Operations ---------------------------

    /**
     * Inserts or updates the overall budget without resetting the database.
     */
    fun insertOrUpdateBudget(budget: Budget) = viewModelScope.launch {
        budgetDao.insertOrUpdateBudget(budget)
        calculateAndUpdateRemainingBudget()
    }

    /**
     * Resets the overall budget to zero and deletes all budget data.
     */
    fun resetOverallBudget() = viewModelScope.launch {
        budgetDao.deleteAllBudgets()
        budgetDao.updateRemainingBudget(0.0)
    }

    /**
     * Checks if the budget has been exceeded and sends a broadcast if so.
     */
    fun checkBudgetExceeded() = viewModelScope.launch {
        val budget = budgetDao.getBudgetDirectly()
        val totalExpenses = expenseDao.getTotalExpensesDirectly() ?: 0.0

        if (budget != null && totalExpenses > budget.overallBudget) {
            val context = getApplication<Application>().applicationContext
            val intent = Intent(context, BudgetExceededReceiver::class.java).apply {
                action = "com.example.my_budget_tracker.BUDGET_EXCEEDED"
            }
            context.sendBroadcast(intent)
        }
    }

    /**
     * Calculates and updates the remaining budget based on total expenses.
     */
    private suspend fun calculateAndUpdateRemainingBudget() {
        val budget = budgetDao.getBudget().value
        val totalExpenses = expenseDao.getTotalExpenses().value ?: 0.0
        if (budget != null) {
            val remainingBudget = budget.overallBudget - totalExpenses
            budgetDao.updateRemainingBudget(remainingBudget)
        }
    }

    /**
     * Updates the remaining budget directly.
     */
    private suspend fun updateRemainingBudget(totalExpenses: Double) {
        val budget = budgetDao.getBudget().value
        if (budget != null) {
            val remainingBudget = budget.overallBudget - totalExpenses
            budgetDao.updateRemainingBudget(remainingBudget)
        }
    }

    // --------------------------- Category Budget Operations ---------------------------

    /**
     * Fetches all category budgets as LiveData.
     */
    fun categoryBudgets(): LiveData<List<CategoryBudget>> = budgetDao.getAllCategoryBudgets()

    /**
     * Deletes all category budgets.
     */
    fun deleteAllCategoryBudgets() = viewModelScope.launch {
        budgetDao.deleteAllCategoryBudgets()
    }

    /**
     * Updates the remaining budget for a specific category.
     */
    @Suppress("unused")
    fun updateCategoryRemainingBudget(categoryName: String, remainingBudget: Double) = viewModelScope.launch {
        budgetDao.updateCategoryRemainingBudget(categoryName, remainingBudget)
    }

    /**
     * Inserts or updates a category-specific budget.
     */
    @Suppress("unused")
    fun insertOrUpdateCategoryBudget(categoryBudget: CategoryBudget) = viewModelScope.launch {
        budgetDao.insertOrUpdateCategoryBudget(categoryBudget)
    }

    /**
     * Updates the specified category budget.
     */
    fun updateCategoryBudget(categoryBudget: CategoryBudget) = viewModelScope.launch {
        budgetDao.updateCategoryBudget(categoryBudget)
    }

    /**
     * Inserts a new category budget.
     */
    fun insertCategoryBudget(categoryBudget: CategoryBudget) = viewModelScope.launch {
        budgetDao.insertCategoryBudget(categoryBudget)
    }

    /**
     * Fetches a specific category budget by name.
     */
    suspend fun getCategoryBudgetByName(categoryName: String): CategoryBudget? {
        return budgetDao.getCategoryBudgetByName(categoryName)
    }

    /**
     * Fetches the total expenses for a specific category.
     */
    fun getCategoryExpenses(categoryName: String): LiveData<Double> {
        return expenseDao.getTotalExpensesForCategory(categoryName)
    }

    // --------------------------- Helper Methods ---------------------------

    /**
     * Observes total expenses and updates the remaining budget whenever expenses change.
     */
    private fun observeTotalExpenses() {
        totalExpenses.observeForever { total ->
            viewModelScope.launch {
                updateRemainingBudget(total)
            }
        }
    }
}
