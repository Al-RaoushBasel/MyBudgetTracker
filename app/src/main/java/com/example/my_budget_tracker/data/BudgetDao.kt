package com.example.my_budget_tracker.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Data Access Object (DAO) for managing budget-related database operations.
 */
@Dao
interface BudgetDao {

    // --------------------------- Budget Operations ---------------------------

    /**
     * Insert or update the overall budget.
     * If a budget already exists, it will be replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudget(budget: Budget)

    /**
     * Retrieve the overall budget as LiveData.
     * Limits to one row since only one budget record exists.
     */
    @Query("SELECT * FROM budget LIMIT 1")
    fun getBudget(): LiveData<Budget?>

    /**
     * Update the remaining budget for the overall budget.
     */
    @Query("UPDATE budget SET remainingBudget = :remainingBudget WHERE id = 1")
    suspend fun updateRemainingBudget(remainingBudget: Double)

    /**
     * Retrieve the budget directly without LiveData.
     */
    @Query("SELECT * FROM budget LIMIT 1")
    suspend fun getBudgetDirectly(): Budget?

    /**
     * Reset the overall budget to zero.
     */
    @Query("UPDATE budget SET overallBudget = 0.0 WHERE id = (SELECT id FROM budget LIMIT 1)")
    suspend fun resetOverallBudget()

    /**
     * Delete all budget records.
     */
    @Query("DELETE FROM budget")
    suspend fun deleteAllBudgets()

    // --------------------------- Category-Specific Budget Operations ---------------------------

    /**
     * Insert or update a category-specific budget.
     * If a category budget already exists, it will be replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCategoryBudget(categoryBudget: CategoryBudget)

    /**
     * Retrieve a specific category budget as LiveData.
     */
    @Query("SELECT * FROM category_budget WHERE categoryName = :category")
    fun getCategoryBudget(category: String): LiveData<CategoryBudget?>

    /**
     * Retrieve all category budgets as LiveData.
     * Useful for observing changes in RecyclerView.
     */
    @Query("SELECT * FROM category_budget")
    fun getAllCategoryBudgets(): LiveData<List<CategoryBudget>>

    /**
     * Update the remaining budget for a specific category.
     */
    @Query("UPDATE category_budget SET remainingAmount = :remainingBudget WHERE categoryName = :categoryName")
    suspend fun updateCategoryRemainingBudget(categoryName: String, remainingBudget: Double)

    /**
     * Retrieve a specific category budget directly.
     */
    @Query("SELECT * FROM category_budget WHERE categoryName = :categoryName LIMIT 1")
    suspend fun getCategoryBudgetByName(categoryName: String): CategoryBudget?

    /**
     * Update an existing category budget.
     */
    @Update
    suspend fun updateCategoryBudget(categoryBudget: CategoryBudget)

    /**
     * Insert a new category budget.
     */
    @Insert
    suspend fun insertCategoryBudget(categoryBudget: CategoryBudget)

    /**
     * Delete all category budgets.
     */
    @Query("DELETE FROM category_budget")
    suspend fun deleteAllCategoryBudgets()

    // --------------------------- Expense Operations ---------------------------

    /**
     * Calculate the total expenses as LiveData.
     */
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expense")
    fun getTotalExpenses(): LiveData<Double>

    /**
     * Calculate the remaining budget as LiveData.
     * Formula: overall budget - total expenses.
     */
    @Query("SELECT (SELECT COALESCE(overallBudget, 0.0) FROM budget LIMIT 1) - (SELECT COALESCE(SUM(amount), 0.0) FROM expense)")
    fun getRemainingBudget(): LiveData<Double>
}
