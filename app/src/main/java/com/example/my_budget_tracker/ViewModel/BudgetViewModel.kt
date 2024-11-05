package com.example.my_budget_tracker.ViewModel

import androidx.lifecycle.*
import com.example.my_budget_tracker.data.Budget
import com.example.my_budget_tracker.data.BudgetDao
import com.example.my_budget_tracker.data.CategoryBudget
import kotlinx.coroutines.launch

class BudgetViewModel(private val budgetDao: BudgetDao) : ViewModel() {

    fun insertBudget(budget: Budget) = viewModelScope.launch {
        budgetDao.insertBudget(budget)
    }

    fun insertCategoryBudget(categoryBudget: CategoryBudget) = viewModelScope.launch {
        budgetDao.insertCategoryBudget(categoryBudget)
    }

    fun getOverallBudget(): LiveData<Budget?> = liveData {
        emit(budgetDao.getBudget())
    }

    fun getCategoryBudget(category: String): LiveData<CategoryBudget?> = liveData {
        emit(budgetDao.getCategoryBudget(category))
    }

    fun getAllCategoryBudgets(): LiveData<List<CategoryBudget>> = liveData {
        emit(budgetDao.getAllCategoryBudgets())
    }

    fun getTotalExpenses(): LiveData<Double> = budgetDao.getTotalExpenses()

    fun getRemainingBudget(): LiveData<Double> = budgetDao.getRemainingBudget()
}



