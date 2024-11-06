package com.example.my_budget_tracker.data


class BudgetRepository(private val budgetDao: BudgetDao) {
    suspend fun insertBudget(budget: Budget) = budgetDao.insertOrUpdateBudget(budget)
    suspend fun insertCategoryBudget(categoryBudget: CategoryBudget) = budgetDao.insertOrUpdateCategoryBudget(categoryBudget)
    suspend fun getBudget() = budgetDao.getBudget()
    suspend fun getCategoryBudget(category: String) = budgetDao.getCategoryBudget(category)
    suspend fun getAllCategoryBudgets() = budgetDao.getAllCategoryBudgets()
}
