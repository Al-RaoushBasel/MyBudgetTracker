package com.example.my_budget_tracker.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.example.my_budget_tracker.R
import com.example.my_budget_tracker.data.Budget
import com.example.my_budget_tracker.data.CategoryBudget
import com.example.my_budget_tracker.data.ExpenseDatabase
import com.example.my_budget_tracker.databinding.FragmentBudgetBinding
import com.example.my_budget_tracker.viewmodel.BudgetViewModel
import com.example.my_budget_tracker.viewmodel.BudgetViewModelFactory
import com.google.android.material.snackbar.Snackbar

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private val budgetViewModel: BudgetViewModel by viewModels {
        val budgetDao = ExpenseDatabase.getDatabase(requireContext()).budgetDao()
        val expenseDao = ExpenseDatabase.getDatabase(requireContext()).expenseDao()
        BudgetViewModelFactory(budgetDao, expenseDao)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle Save Overall Budget
        binding.saveBudgetButton.setOnClickListener {
            val overallBudget = binding.overallBudgetInput.text.toString().toDoubleOrNull()
            if (overallBudget != null) {
                saveOverallBudget(overallBudget)
                Snackbar.make(view, "Overall Budget saved successfully!", Snackbar.LENGTH_SHORT).show()

                // Clear the input field after saving
                binding.overallBudgetInput.text?.clear()

                updateSummary()
            } else {
                Snackbar.make(view, "Please enter a valid budget amount", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Handle Save Category Budgets
        binding.saveCategoryBudgetButton.setOnClickListener {
            val category = binding.categorySpinner.selectedItem.toString()
            val budgetAmount = binding.categoryBudgetInput.text.toString().toDoubleOrNull()
            if (budgetAmount != null) {
                saveCategoryBudget(category, budgetAmount)
                Snackbar.make(view, "$category budget saved successfully!", Snackbar.LENGTH_SHORT).show()

                // Clear the input field after saving
                binding.categoryBudgetInput.text?.clear()

                updateSummary()
            } else {
                Snackbar.make(view, "Please enter a valid amount", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Load initial summary
        updateSummary()

        setupClickListeners(view)
    }

    private fun setupClickListeners(view: View) {
        binding.saveBudgetButton.setOnClickListener {
            val overallBudget = binding.overallBudgetInput.text.toString().toDoubleOrNull()
            if (overallBudget != null) {
                saveOverallBudget(overallBudget)
                Snackbar.make(view, "Overall Budget saved successfully!", Snackbar.LENGTH_SHORT).show()

                // Clear the input field after saving
                binding.overallBudgetInput.text?.clear()
            } else {
                Snackbar.make(view, "Please enter a valid budget amount", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.saveCategoryBudgetButton.setOnClickListener {
            val category = binding.categorySpinner.selectedItem.toString()
            val budgetAmount = binding.categoryBudgetInput.text.toString().toDoubleOrNull()
            if (budgetAmount != null) {
                saveCategoryBudget(category, budgetAmount)
                Snackbar.make(view, "$category budget saved successfully!", Snackbar.LENGTH_SHORT).show()

                // Clear the input field after saving
                binding.categoryBudgetInput.text?.clear()
            } else {
                Snackbar.make(view, "Please enter a valid amount", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.viewAnalysisButton.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_budgetFragment_to_analysisDetailFragment)
        }
    }

    private fun saveOverallBudget(overallBudget: Double) {
        val budget = Budget(overallBudget = overallBudget)
        budgetViewModel.insertOrUpdateBudget(budget)
    }

    private fun saveCategoryBudget(categoryName: String, budgetAmount: Double) {
        val categoryBudget = CategoryBudget(categoryName = categoryName, budgetAmount = budgetAmount)
        budgetViewModel.insertOrUpdateCategoryBudget(categoryBudget)
    }

    private fun updateSummary() {
        // Revert to observing LiveData directly within the fragment as in the original code
        budgetViewModel.budget.observe(viewLifecycleOwner) { budget ->
            val totalBudget = budget?.overallBudget ?: 0.0
            binding.totalBudgetSummary.text = "Total Budget: $$totalBudget"

            budgetViewModel.totalExpenses.observe(viewLifecycleOwner) { totalExpenses ->
                val expenses = totalExpenses ?: 0.0
                binding.totalExpensesSummary.text = "Total Expenses: $$expenses"

                // Calculate the remaining budget
                val remainingBudget = totalBudget - expenses
                binding.remainingBudgetSummary.text = "Remaining Budget: $$remainingBudget"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
