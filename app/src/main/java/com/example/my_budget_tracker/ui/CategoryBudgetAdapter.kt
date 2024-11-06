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
                return oldItem.categoryName == newItem.categoryName
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

    // ViewHolder for each category budget item
    inner class CategoryBudgetViewHolder(private val binding: ItemCategoryBudgetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryBudget: CategoryBudget) {
            binding.categoryName.text = categoryBudget.categoryName
            binding.budgetAmount.text = "Budget Amount: $${categoryBudget.budgetAmount}"

            // Initialize progress and remaining budget details
            val remainingBudget = categoryBudget.remainingAmount
            val expenses = categoryBudget.budgetAmount - remainingBudget
            val progress = if (categoryBudget.budgetAmount > 0) {
                (expenses / categoryBudget.budgetAmount * 100).toInt()
            } else {
                0
            }
            binding.categoryBudgetProgress.progress = progress
            binding.categoryBudgetDetails.text = "Used $$expenses out of $${categoryBudget.budgetAmount}"
        }

        // Additional method to update progress dynamically
        fun updateProgress(remainingBudget: Double, totalBudget: Double, expenses: Double) {
            val progress = if (totalBudget > 0) {
                (expenses / totalBudget * 100).toInt()
            } else {
                0
            }
            binding.categoryBudgetProgress.progress = progress
            binding.categoryBudgetDetails.text = "Used $$expenses out of $$totalBudget"
        }
    }

    // Function to update the category's progress and notify UI
    fun updateCategoryProgress(categoryName: String, remainingBudget: Double, totalBudget: Double, expenses: Double, progress: Int) {
        val position = currentList.indexOfFirst { it.categoryName == categoryName }
        if (position != -1) {
            // Update the item in the list and notify the ViewHolder to refresh
            val updatedCategory = currentList[position].copy(
                remainingAmount = remainingBudget,
                budgetAmount = totalBudget
            )
            submitList(currentList.toMutableList().apply { set(position, updatedCategory) })
            notifyItemChanged(position)
        }
    }
}
