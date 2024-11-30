package com.example.my_budget_tracker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.my_budget_tracker.data.CategoryBudget
import com.example.my_budget_tracker.data.CurrencyManager
import com.example.my_budget_tracker.databinding.ItemCategoryBudgetBinding

class CategoryBudgetAdapter : ListAdapter<CategoryBudget, CategoryBudgetAdapter.CategoryBudgetViewHolder>(DIFF_CALLBACK) {

    // --------------------------- Diff Callback ---------------------------
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

    // --------------------------- Adapter Methods ---------------------------

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryBudgetViewHolder {
        val binding = ItemCategoryBudgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryBudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryBudgetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // --------------------------- ViewHolder ---------------------------

    inner class CategoryBudgetViewHolder(private val binding: ItemCategoryBudgetBinding) : RecyclerView.ViewHolder(binding.root) {

        // Bind category budget details to the UI
        fun bind(categoryBudget: CategoryBudget) {
            binding.categoryName.text = categoryBudget.categoryName

            // Format and display budget amount
            binding.budgetAmount.text = "Budget Amount: ${CurrencyManager.formatAmount(categoryBudget.budgetAmount)}"

            val remainingBudget = categoryBudget.remainingAmount
            val expenses = categoryBudget.budgetAmount - remainingBudget

            // Format and display category budget details
            binding.categoryBudgetDetails.text = "Used ${CurrencyManager.formatAmount(expenses)} out of ${CurrencyManager.formatAmount(categoryBudget.budgetAmount)}"

            // Calculate and display progress
            val progress = if (categoryBudget.budgetAmount > 0) {
                (expenses / categoryBudget.budgetAmount * 100).toInt()
            } else {
                0
            }
            binding.categoryBudgetProgress.progress = progress
        }

        // Update progress dynamically (unused but kept for future use)
        @Suppress("UNUSED")
        fun updateProgress(remainingBudget: Double, totalBudget: Double, expenses: Double) {
            val progress = if (totalBudget > 0) {
                (expenses / totalBudget * 100).toInt()
            } else {
                0
            }
            binding.categoryBudgetProgress.progress = progress
            binding.categoryBudgetDetails.text = "Used ${CurrencyManager.formatAmount(expenses)} out of ${CurrencyManager.formatAmount(totalBudget)}"
        }
    }

    // --------------------------- Additional Methods ---------------------------

    /**
     * Updates the progress for a specific category and refreshes the UI.
     * This method is unused but kept for future use.
     */
    @Suppress("UNUSED")
    fun updateCategoryProgress(categoryName: String, remainingBudget: Double, totalBudget: Double, expenses: Double, progress: Int) {
        val position = currentList.indexOfFirst { it.categoryName == categoryName }
        if (position != -1) {
            // Update the item in the list and notify the ViewHolder
            val updatedCategory = currentList[position].copy(
                remainingAmount = remainingBudget,
                budgetAmount = totalBudget
            )
            submitList(currentList.toMutableList().apply { set(position, updatedCategory) })
            notifyItemChanged(position)
        }
    }
}
