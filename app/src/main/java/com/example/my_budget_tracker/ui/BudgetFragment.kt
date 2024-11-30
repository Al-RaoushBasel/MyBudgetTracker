package com.example.my_budget_tracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.my_budget_tracker.R
import com.example.my_budget_tracker.data.Budget
import com.example.my_budget_tracker.data.CategoryBudget
import com.example.my_budget_tracker.data.CurrencyManager
import com.example.my_budget_tracker.data.ExpenseDatabase
import com.example.my_budget_tracker.databinding.FragmentBudgetBinding
import com.example.my_budget_tracker.viewModel.BudgetViewModel
import com.example.my_budget_tracker.viewModel.BudgetViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class BudgetFragment : Fragment() {

    // --------------------------- Properties ---------------------------

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private val budgetViewModel: BudgetViewModel by viewModels {
        val budgetDao = ExpenseDatabase.getDatabase(requireContext()).budgetDao()
        val expenseDao = ExpenseDatabase.getDatabase(requireContext()).expenseDao()
        BudgetViewModelFactory(
            requireActivity().application,
            budgetDao,
            expenseDao
        )
    }

    // --------------------------- Lifecycle Methods ---------------------------

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupSaveActions()
        updateSummary() // Load the initial summary
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // --------------------------- Setup Methods ---------------------------

    private fun setupClickListeners() {
        binding.viewAnalysisButton.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_budgetFragment_to_analysisDetailFragment)
        }
    }

    private fun setupSaveActions() {
        // Handle Save Overall Budget
        binding.saveBudgetButton.setOnClickListener {
            val overallBudget = binding.overallBudgetInput.text.toString().toDoubleOrNull()
            if (overallBudget != null) {
                saveOverallBudget(overallBudget)
                binding.overallBudgetInput.text?.clear() // Clear input field
            } else {
                showSnackbar("Please enter a valid budget amount")
            }
        }

        // Handle Save Category Budgets
        binding.saveCategoryBudgetButton.setOnClickListener {
            val category = binding.categorySpinner.selectedItem.toString()
            val budgetAmount = binding.categoryBudgetInput.text.toString().toDoubleOrNull()
            if (budgetAmount != null) {
                saveCategoryBudget(category, budgetAmount)
                binding.categoryBudgetInput.text?.clear() // Clear input field
            } else {
                showSnackbar("Please enter a valid amount")
            }
        }
    }

    // --------------------------- Save Methods ---------------------------

    private fun saveOverallBudget(overallBudget: Double) {
        val budgetInEUR = CurrencyManager.convertAmount(
            overallBudget,
            CurrencyManager.selectedCurrency,
            "EUR"
        )

        val budget = Budget(overallBudget = budgetInEUR)
        budgetViewModel.insertOrUpdateBudget(budget)

        showSnackbar("Overall budget saved in ${CurrencyManager.selectedCurrency}: ${CurrencyManager.formatAmount(overallBudget)}")
        updateSummary()
    }

    private fun saveCategoryBudget(categoryName: String, budgetAmount: Double) {
        viewLifecycleOwner.lifecycleScope.launch {
            val budgetAmountInEUR = CurrencyManager.convertAmount(
                budgetAmount,
                CurrencyManager.selectedCurrency,
                "EUR"
            )

            val existingCategoryBudget = budgetViewModel.getCategoryBudgetByName(categoryName)
            if (existingCategoryBudget != null) {
                val updatedCategoryBudget = existingCategoryBudget.copy(budgetAmount = budgetAmountInEUR)
                budgetViewModel.updateCategoryBudget(updatedCategoryBudget)
                showSnackbar("$categoryName budget updated in ${CurrencyManager.selectedCurrency}: ${CurrencyManager.formatAmount(budgetAmount)}")
            } else {
                val newCategoryBudget = CategoryBudget(categoryName = categoryName, budgetAmount = budgetAmountInEUR)
                budgetViewModel.insertCategoryBudget(newCategoryBudget)
                showSnackbar("$categoryName budget saved in ${CurrencyManager.selectedCurrency}: ${CurrencyManager.formatAmount(budgetAmount)}")
            }

            updateSummary() // Refresh the summary
        }
    }

    // --------------------------- Update Methods ---------------------------

    private fun updateSummary() {
        budgetViewModel.budget.observe(viewLifecycleOwner) { budget ->
            val totalBudget = budget?.overallBudget ?: 0.0
            val totalBudgetInSelectedCurrency = CurrencyManager.convertAmount(
                totalBudget, "EUR", CurrencyManager.selectedCurrency
            )
            binding.totalBudgetSummary.text =
                "Total Budget: ${CurrencyManager.formatAmount(totalBudgetInSelectedCurrency)}"

            budgetViewModel.totalExpenses.observe(viewLifecycleOwner) { totalExpenses ->
                val expenses = totalExpenses ?: 0.0
                val expensesInSelectedCurrency = CurrencyManager.convertAmount(
                    expenses, "EUR", CurrencyManager.selectedCurrency
                )
                binding.totalExpensesSummary.text =
                    "Total Expenses: ${CurrencyManager.formatAmount(expensesInSelectedCurrency)}"

                val remainingBudget = totalBudget - expenses
                val remainingBudgetInSelectedCurrency = CurrencyManager.convertAmount(
                    remainingBudget, "EUR", CurrencyManager.selectedCurrency
                )
                binding.remainingBudgetSummary.text =
                    "Remaining Budget: ${CurrencyManager.formatAmount(remainingBudgetInSelectedCurrency)}"
            }
        }
    }

    // --------------------------- Helper Methods ---------------------------

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }
}
