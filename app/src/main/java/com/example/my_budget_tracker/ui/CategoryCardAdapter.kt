package com.example.my_budget_tracker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.my_budget_tracker.R
import com.example.my_budget_tracker.data.CategoryBudget
import com.example.my_budget_tracker.data.CurrencyManager
import com.example.my_budget_tracker.databinding.ItemCategoryCardBinding

class CategoryCardAdapter : ListAdapter<CategoryBudget, CategoryCardAdapter.CategoryCardViewHolder>(DIFF_CALLBACK) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryCardViewHolder {
        val binding = ItemCategoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryCardViewHolder, position: Int) {
        val categoryBudget = getItem(position)
        holder.bind(categoryBudget)
    }

    inner class CategoryCardViewHolder(private val binding: ItemCategoryCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryBudget: CategoryBudget) {
            // Set category name
            binding.categoryName.text = categoryBudget.categoryName

            // Set category icon
            binding.categoryIcon.setImageResource(getCategoryIcon(categoryBudget.categoryName))

            // Calculate spent amount and percentage
            val spentAmount = categoryBudget.budgetAmount - categoryBudget.remainingAmount
            val percentage = if (categoryBudget.budgetAmount > 0) {
                (spentAmount / categoryBudget.budgetAmount * 100).toInt()
            } else {
                0
            }

            // Update UI
            binding.categoryAmountSpent.text = CurrencyManager.formatAmount(spentAmount)
            binding.categoryProgress.progress = percentage
            binding.categoryPercentage.text = "$percentage%"
        }




        private fun getCategoryIcon(categoryName: String): Int {
            return when (categoryName) {
                "Bills" -> R.drawable.ic_bills
                "Food" -> R.drawable.ic_food
                "Health" -> R.drawable.ic_health
                "Education" -> R.drawable.ic_education
                "Insurance" -> R.drawable.ic_insurance
                "Shopping" -> R.drawable.ic_shopping
                "Other" -> R.drawable.ic_categories
                else -> R.drawable.ic_categories
            }
        }
    }


}
