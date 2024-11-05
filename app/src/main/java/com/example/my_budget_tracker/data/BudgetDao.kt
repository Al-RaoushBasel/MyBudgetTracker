package com.example.my_budget_tracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import androidx.room.Update


@Dao
interface BudgetDao {
    // For the overall budget
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget)

    @Query("SELECT * FROM budget LIMIT 1")
    suspend fun getBudget(): Budget?

    @Update
    suspend fun updateBudget(budget: Budget)

    // For category-specific budgets
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryBudget(categoryBudget: CategoryBudget)

    @Query("SELECT * FROM category_budget WHERE categoryName = :category")
    suspend fun getCategoryBudget(category: String): CategoryBudget?

    @Query("SELECT * FROM category_budget")
    suspend fun getAllCategoryBudgets(): List<CategoryBudget>

    @Update
    suspend fun updateCategoryBudget(categoryBudget: CategoryBudget)

    @Query("SELECT SUM(amount) FROM expense")
    fun getTotalExpenses(): LiveData<Double>

    @Query("SELECT (SELECT amount FROM budget LIMIT 1) - (SELECT SUM(amount) FROM expense)")
    fun getRemainingBudget(): LiveData<Double>

}

