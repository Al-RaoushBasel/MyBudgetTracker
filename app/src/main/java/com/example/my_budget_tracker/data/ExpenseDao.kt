package com.example.my_budget_tracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.my_budget_tracker.data.Expense


@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expense ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expense")
    fun getTotalExpenses(): LiveData<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expense WHERE category = :categoryName")
    fun getTotalExpensesForCategory(categoryName: String): LiveData<Double>



    @Query("DELETE FROM expense")
    suspend fun deleteAllExpenses()

    companion object
}
