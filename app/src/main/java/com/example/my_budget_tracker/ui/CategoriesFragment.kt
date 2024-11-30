package com.example.my_budget_tracker.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_budget_tracker.R
import com.example.my_budget_tracker.data.CategoryBudget
import com.example.my_budget_tracker.data.CurrencyManager
import com.example.my_budget_tracker.data.ExpenseDatabase
import com.example.my_budget_tracker.databinding.FragmentCategoriesBinding
import com.example.my_budget_tracker.viewmodel.BudgetViewModel
import com.example.my_budget_tracker.viewmodel.BudgetViewModelFactory
import com.google.android.material.snackbar.Snackbar

class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CategoryCardAdapter
    private lateinit var budgetViewModel: BudgetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        val budgetDao = ExpenseDatabase.getDatabase(requireContext()).budgetDao()
        val expenseDao = ExpenseDatabase.getDatabase(requireContext()).expenseDao()
        val factory = BudgetViewModelFactory(
            requireActivity().application, // Pass the Application context
            budgetDao,
            expenseDao
        )
        budgetViewModel = ViewModelProvider(this, factory).get(BudgetViewModel::class.java)


        // Set up RecyclerView
        adapter = CategoryCardAdapter()
        binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.categoriesRecyclerView.adapter = adapter

        // Observe category budgets and update UI
        budgetViewModel.categoryBudgets().observe(viewLifecycleOwner) { categoryBudgets ->
            updateCategoryBudgetUI(categoryBudgets)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_categories, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reset_all_categories -> {
                resetAllCategoryBudgets()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun resetAllCategoryBudgets() {
        budgetViewModel.deleteAllCategoryBudgets()
        Snackbar.make(requireView(), "All category budgets deleted", Snackbar.LENGTH_SHORT).show()
    }

    /**
     * Updates the category budgets displayed in the RecyclerView, converting them to the selected currency
     * and calculating the percentage used.
     */
    private fun updateCategoryBudgetUI(categoryBudgets: List<CategoryBudget>) {
        val updatedCategoryBudgets = mutableListOf<CategoryBudget>()

        // For each category budget, calculate expenses and remaining amounts
        categoryBudgets.forEach { categoryBudget ->
            budgetViewModel.getCategoryExpenses(categoryBudget.categoryName).observe(viewLifecycleOwner) { totalExpensesInEUR ->
                val expensesInSelectedCurrency = CurrencyManager.convertAmount(
                    totalExpensesInEUR ?: 0.0,
                    "EUR", // From EUR
                    CurrencyManager.selectedCurrency // To selected currency
                )

                val budgetAmountInSelectedCurrency = CurrencyManager.convertAmount(
                    categoryBudget.budgetAmount,
                    "EUR", // Base currency
                    CurrencyManager.selectedCurrency // Convert to selected currency
                )

                val remainingAmountInSelectedCurrency = budgetAmountInSelectedCurrency - expensesInSelectedCurrency

                updatedCategoryBudgets.add(
                    categoryBudget.copy(
                        budgetAmount = budgetAmountInSelectedCurrency,
                        remainingAmount = remainingAmountInSelectedCurrency
                    )
                )

                // Update adapter only after all categories are processed
                if (updatedCategoryBudgets.size == categoryBudgets.size) {
                    adapter.submitList(updatedCategoryBudgets)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
