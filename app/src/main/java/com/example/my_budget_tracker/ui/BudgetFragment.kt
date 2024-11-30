package com.example.my_budget_tracker.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.my_budget_tracker.R
import com.example.my_budget_tracker.data.Budget
import com.example.my_budget_tracker.data.CategoryBudget
import com.example.my_budget_tracker.data.CurrencyManager
import com.example.my_budget_tracker.data.ExpenseDatabase
import com.example.my_budget_tracker.databinding.FragmentBudgetBinding
import com.example.my_budget_tracker.viewmodel.BudgetViewModel
import com.example.my_budget_tracker.viewmodel.BudgetViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private val budgetViewModel: BudgetViewModel by viewModels {
        val budgetDao = ExpenseDatabase.getDatabase(requireContext()).budgetDao()
        val expenseDao = ExpenseDatabase.getDatabase(requireContext()).expenseDao()
        BudgetViewModelFactory(
            requireActivity().application, // Pass the application instance
            budgetDao,
            expenseDao
        )
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
        binding.viewAnalysisButton.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_budgetFragment_to_analysisDetailFragment)
        }
    }

    private fun saveOverallBudget(overallBudget: Double) {
        // Convert the budget from the selected currency to EUR before saving
        val budgetInEUR = CurrencyManager.convertAmount(
            overallBudget,
            CurrencyManager.selectedCurrency, // From selected currency
            "EUR" // To EUR
        )

        val budget = Budget(overallBudget = budgetInEUR)
        budgetViewModel.insertOrUpdateBudget(budget)

        // Show a confirmation message
        Snackbar.make(
            requireView(),
            "Overall budget saved in ${CurrencyManager.selectedCurrency}: ${CurrencyManager.formatAmount(overallBudget)}",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun saveCategoryBudget(categoryName: String, budgetAmount: Double) {
        viewLifecycleOwner.lifecycleScope.launch {
            // Convert the category budget from the selected currency to EUR before saving
            val budgetAmountInEUR = CurrencyManager.convertAmount(
                budgetAmount,
                CurrencyManager.selectedCurrency, // From selected currency
                "EUR" // To EUR
            )

            val existingCategoryBudget = budgetViewModel.getCategoryBudgetByName(categoryName)
            if (existingCategoryBudget != null) {
                val updatedCategoryBudget = existingCategoryBudget.copy(budgetAmount = budgetAmountInEUR)
                budgetViewModel.updateCategoryBudget(updatedCategoryBudget)
                Snackbar.make(
                    requireView(),
                    "$categoryName budget updated in ${CurrencyManager.selectedCurrency}: ${CurrencyManager.formatAmount(budgetAmount)}",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                val newCategoryBudget = CategoryBudget(
                    categoryName = categoryName,
                    budgetAmount = budgetAmountInEUR
                )
                budgetViewModel.insertCategoryBudget(newCategoryBudget)
                Snackbar.make(
                    requireView(),
                    "$categoryName budget saved in ${CurrencyManager.selectedCurrency}: ${CurrencyManager.formatAmount(budgetAmount)}",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

            // Refresh the summary after saving the budget
            updateSummary()
        }
    }


    private fun updateSummary() {
        budgetViewModel.budget.observe(viewLifecycleOwner) { budget ->
            val totalBudget = budget?.overallBudget ?: 0.0
            val totalBudgetInSelectedCurrency = CurrencyManager.convertAmount(
                totalBudget, // Stored in EUR
                "EUR", // From EUR
                CurrencyManager.selectedCurrency // To selected currency
            )
            binding.totalBudgetSummary.text =
                "Total Budget: ${CurrencyManager.formatAmount(totalBudgetInSelectedCurrency)}"

            budgetViewModel.totalExpenses.observe(viewLifecycleOwner) { totalExpenses ->
                val expenses = totalExpenses ?: 0.0
                val expensesInSelectedCurrency = CurrencyManager.convertAmount(
                    expenses, // Stored in EUR
                    "EUR", // From EUR
                    CurrencyManager.selectedCurrency // To selected currency
                )
                binding.totalExpensesSummary.text =
                    "Total Expenses: ${CurrencyManager.formatAmount(expensesInSelectedCurrency)}"

                // Calculate and convert the remaining budget
                val remainingBudget = totalBudget - expenses
                val remainingBudgetInSelectedCurrency = CurrencyManager.convertAmount(
                    remainingBudget, // Stored in EUR
                    "EUR", // From EUR
                    CurrencyManager.selectedCurrency // To selected currency
                )
                binding.remainingBudgetSummary.text =
                    "Remaining Budget: ${CurrencyManager.formatAmount(remainingBudgetInSelectedCurrency)}"
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
