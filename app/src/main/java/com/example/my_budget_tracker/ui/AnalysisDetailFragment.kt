package com.example.my_budget_tracker.ui

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_budget_tracker.R
import com.example.my_budget_tracker.viewmodel.BudgetViewModel
import com.example.my_budget_tracker.data.Budget
import com.example.my_budget_tracker.data.CategoryBudget
import com.example.my_budget_tracker.data.CurrencyManager
import com.example.my_budget_tracker.data.ExpenseDatabase
import com.example.my_budget_tracker.databinding.FragmentAnalysisDetailBinding
import com.google.android.material.snackbar.Snackbar
import com.example.my_budget_tracker.viewmodel.BudgetViewModelFactory


class AnalysisDetailFragment : Fragment() {

    private var _binding: FragmentAnalysisDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CategoryBudgetAdapter
    private lateinit var budgetViewModel: BudgetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the toolbar with a back button and title
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.title = "Analysis Detail"

        // Handle physical back button press
        activity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        // Initialize the ViewModel with the factory
// Initialize the ViewModel with the factory
        val budgetDao = ExpenseDatabase.getDatabase(requireContext()).budgetDao()
        val expenseDao = ExpenseDatabase.getDatabase(requireContext()).expenseDao()
        val factory = BudgetViewModelFactory(
            requireActivity().application, // Pass the Application instance
            budgetDao,
            expenseDao
        )
        budgetViewModel = ViewModelProvider(this, factory).get(BudgetViewModel::class.java)


        // Set up the RecyclerView and adapter for category budgets
        adapter = CategoryBudgetAdapter()
        binding.categoryBudgetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryBudgetRecyclerView.adapter = adapter

        // Observe and update the overall budget progress
        observeOverallBudget()

        // Fetch and update category budgets with currency handling
        observeAndUpdateCategoryBudgets()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_analysis_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { // Handle toolbar back button click
                findNavController().navigateUp()
                true
            }
            R.id.action_reset_overall_budget -> {
                resetOverallBudget()
                true
            }
            R.id.action_delete_category_budgets -> {
                deleteAllCategoryBudgets()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun resetOverallBudget() {
        budgetViewModel.resetOverallBudget()
        Snackbar.make(requireView(), "Overall Budget reset to 0", Snackbar.LENGTH_SHORT).show()
    }

    private fun deleteAllCategoryBudgets() {
        budgetViewModel.deleteAllCategoryBudgets()
        Snackbar.make(requireView(), "All category budgets deleted", Snackbar.LENGTH_SHORT).show()
    }

    private fun observeOverallBudget() {
        budgetViewModel.budget.observe(viewLifecycleOwner) { budget ->
            val totalBudgetInEUR = budget?.overallBudget ?: 0.0
            val totalBudgetInSelectedCurrency = CurrencyManager.convertAmount(
                totalBudgetInEUR,
                "EUR", // From EUR
                CurrencyManager.selectedCurrency // To selected currency
            )

            budgetViewModel.totalExpenses.observe(viewLifecycleOwner) { totalExpensesInEUR ->
                val expensesInSelectedCurrency = CurrencyManager.convertAmount(
                    totalExpensesInEUR ?: 0.0,
                    "EUR", // From EUR
                    CurrencyManager.selectedCurrency // To selected currency
                )

                if (totalBudgetInSelectedCurrency > 0) {
                    val progress = (expensesInSelectedCurrency / totalBudgetInSelectedCurrency * 100).toInt()
                    binding.overallBudgetProgress.progress = progress
                    binding.overallBudgetSummaryText.text =
                        "Used: ${CurrencyManager.formatAmount(expensesInSelectedCurrency)} of ${CurrencyManager.formatAmount(totalBudgetInSelectedCurrency)}"
                } else {
                    binding.overallBudgetProgress.progress = 0
                    binding.overallBudgetSummaryText.text = "No budget set"
                }
            }
        }
    }

    private fun observeAndUpdateCategoryBudgets() {
        budgetViewModel.categoryBudgets().observe(viewLifecycleOwner) { categoryBudgets ->
            val updatedCategoryBudgets = mutableListOf<CategoryBudget>()

            categoryBudgets.forEach { categoryBudget ->
                budgetViewModel.getCategoryExpenses(categoryBudget.categoryName).observe(viewLifecycleOwner) { totalExpensesInEUR ->
                    val expensesInSelectedCurrency = CurrencyManager.convertAmount(
                        totalExpensesInEUR ?: 0.0,
                        "EUR", // From EUR
                        CurrencyManager.selectedCurrency // To selected currency
                    )
                    val budgetAmountInSelectedCurrency = CurrencyManager.convertAmount(
                        categoryBudget.budgetAmount,
                        "EUR", // From EUR
                        CurrencyManager.selectedCurrency // To selected currency
                    )
                    val remainingBudgetInSelectedCurrency = budgetAmountInSelectedCurrency - expensesInSelectedCurrency
                    if (budgetAmountInSelectedCurrency > 0) {
                        (expensesInSelectedCurrency / budgetAmountInSelectedCurrency * 100).toInt()
                    } else {
                        0
                    }

                    updatedCategoryBudgets.add(
                        categoryBudget.copy(
                            budgetAmount = budgetAmountInSelectedCurrency,
                            remainingAmount = remainingBudgetInSelectedCurrency
                        )
                    )

                    // Update adapter after processing all categories
                    if (updatedCategoryBudgets.size == categoryBudgets.size) {
                        adapter.submitList(updatedCategoryBudgets)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
