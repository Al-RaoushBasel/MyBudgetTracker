package com.example.my_budget_tracker.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BudgetDao {

    // Insert or update the overall budget
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudget(budget: Budget)

    @Query("SELECT * FROM budget LIMIT 1")
    fun getBudget(): LiveData<Budget?>

    @Query("UPDATE budget SET remainingBudget = :remainingBudget WHERE id = 1")
    suspend fun updateRemainingBudget(remainingBudget: Double)

    // Insert or update a category-specific budget
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCategoryBudget(categoryBudget: CategoryBudget)

    @Query("SELECT * FROM category_budget WHERE categoryName = :category")
    fun getCategoryBudget(category: String): LiveData<CategoryBudget?>

    // Get all category budgets as LiveData for observing changes in the RecyclerView
    @Query("SELECT * FROM category_budget")
    fun getAllCategoryBudgets(): LiveData<List<CategoryBudget>>

    // Get total expenses as LiveData
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expense")
    fun getTotalExpenses(): LiveData<Double>

    // Calculate remaining budget as LiveData
    @Query("SELECT (SELECT COALESCE(overallBudget, 0.0) FROM budget LIMIT 1) - (SELECT COALESCE(SUM(amount), 0.0) FROM expense)")
    fun getRemainingBudget(): LiveData<Double>

    // Reset the overall budget to zero
    @Query("UPDATE budget SET overallBudget = 0.0 WHERE id = (SELECT id FROM budget LIMIT 1)")
    suspend fun resetOverallBudget()

    @Query("DELETE FROM budget")
    suspend fun deleteAllBudgets()

    // Delete all category-specific budgets
    @Query("DELETE FROM category_budget")
    suspend fun deleteAllCategoryBudgets()

    @Query("UPDATE category_budget SET remainingAmount = :remainingBudget WHERE categoryName = :categoryName")
    suspend fun updateCategoryRemainingBudget(categoryName: String, remainingBudget: Double)


    @Query("SELECT * FROM category_budget WHERE categoryName = :categoryName LIMIT 1")
    suspend fun getCategoryBudgetByName(categoryName: String): CategoryBudget?

    @Update
    suspend fun updateCategoryBudget(categoryBudget: CategoryBudget)

    @Insert
    suspend fun insertCategoryBudget(categoryBudget: CategoryBudget)

}
