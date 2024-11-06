package com.example.my_budget_tracker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.my_budget_tracker.data.CategoryBudget
import com.example.my_budget_tracker.databinding.ItemCategoryBudgetBinding

class CategoryBudgetAdapter : ListAdapter<CategoryBudget, CategoryBudgetAdapter.CategoryBudgetViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CategoryBudget>() {
            override fun areItemsTheSame(oldItem: CategoryBudget, newItem: CategoryBudget): Boolean {
                return oldItem.categoryName == newItem.categoryName // or any unique identifier
            }

            override fun areContentsTheSame(oldItem: CategoryBudget, newItem: CategoryBudget): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryBudgetViewHolder {
        val binding = ItemCategoryBudgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryBudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryBudgetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateCategoryProgress(categoryName: String, remainingBudget: Double, totalBudget: Double, totalExpenses: Double) {
        // Locate the correct item in the RecyclerView by categoryName
        // Update the progress bar and budget summary for the specific category
        val index = currentList.indexOfFirst { it.categoryName == categoryName }
        if (index != -1) {
            val categoryBudget = currentList[index].copy(remainingAmount = remainingBudget)
            val updatedList = currentList.toMutableList()
            updatedList[index] = categoryBudget
            submitList(updatedList)
        }
    }


    private lateinit var adapter: CategoryBudgetAdapter


    class CategoryBudgetViewHolder(private val binding: ItemCategoryBudgetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryBudget: CategoryBudget) {
            binding.categoryName.text = categoryBudget.categoryName
            binding.budgetAmount.text = "$${categoryBudget.budgetAmount}"
        }
    }
}
