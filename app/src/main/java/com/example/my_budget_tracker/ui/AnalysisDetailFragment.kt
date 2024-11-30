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
import com.example.my_budget_tracker.data.CategoryBudget
import com.example.my_budget_tracker.data.CurrencyManager
import com.example.my_budget_tracker.data.ExpenseDatabase
import com.example.my_budget_tracker.databinding.FragmentAnalysisDetailBinding
import com.example.my_budget_tracker.viewModel.BudgetViewModel
import com.example.my_budget_tracker.viewModel.BudgetViewModelFactory
import com.google.android.material.snackbar.Snackbar

class AnalysisDetailFragment : Fragment() {

    // --------------------------- Properties ---------------------------

    private var _binding: FragmentAnalysisDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CategoryBudgetAdapter
    private lateinit var budgetViewModel: BudgetViewModel

    // --------------------------- Lifecycle Methods ---------------------------

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

        setupToolbar()
        handleBackButton()
        setupViewModel()
        setupRecyclerView()
        observeOverallBudget()
        observeAndUpdateCategoryBudgets()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_analysis_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // --------------------------- Setup Methods ---------------------------

    private fun setupToolbar() {
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.title = "Analysis Detail"
    }

    private fun handleBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
    }

    private fun setupViewModel() {
        val budgetDao = ExpenseDatabase.getDatabase(requireContext()).budgetDao()
        val expenseDao = ExpenseDatabase.getDatabase(requireContext()).expenseDao()
        val factory = BudgetViewModelFactory(requireActivity().application, budgetDao, expenseDao)
        budgetViewModel = ViewModelProvider(this, factory).get(BudgetViewModel::class.java)
    }

    private fun setupRecyclerView() {
        adapter = CategoryBudgetAdapter()
        binding.categoryBudgetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryBudgetRecyclerView.adapter = adapter
    }

    // --------------------------- Action Methods ---------------------------

    private fun resetOverallBudget() {
        budgetViewModel.resetOverallBudget()
        Snackbar.make(requireView(), "Overall Budget reset to 0", Snackbar.LENGTH_SHORT).show()
    }

    private fun deleteAllCategoryBudgets() {
        budgetViewModel.deleteAllCategoryBudgets()
        Snackbar.make(requireView(), "All category budgets deleted", Snackbar.LENGTH_SHORT).show()
    }

    // --------------------------- Observation Methods ---------------------------

    private fun observeOverallBudget() {
        budgetViewModel.budget.observe(viewLifecycleOwner) { budget ->
            val totalBudgetInEUR = budget?.overallBudget ?: 0.0
            val totalBudgetInSelectedCurrency = CurrencyManager.convertAmount(
                totalBudgetInEUR, "EUR", CurrencyManager.selectedCurrency
            )

            budgetViewModel.totalExpenses.observe(viewLifecycleOwner) { totalExpensesInEUR ->
                val expensesInSelectedCurrency = CurrencyManager.convertAmount(
                    totalExpensesInEUR ?: 0.0, "EUR", CurrencyManager.selectedCurrency
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
                        totalExpensesInEUR ?: 0.0, "EUR", CurrencyManager.selectedCurrency
                    )
                    val budgetAmountInSelectedCurrency = CurrencyManager.convertAmount(
                        categoryBudget.budgetAmount, "EUR", CurrencyManager.selectedCurrency
                    )
                    val remainingBudgetInSelectedCurrency = budgetAmountInSelectedCurrency - expensesInSelectedCurrency

                    updatedCategoryBudgets.add(
                        categoryBudget.copy(
                            budgetAmount = budgetAmountInSelectedCurrency,
                            remainingAmount = remainingBudgetInSelectedCurrency
                        )
                    )

                    if (updatedCategoryBudgets.size == categoryBudgets.size) {
                        adapter.submitList(updatedCategoryBudgets)
                    }
                }
            }
        }
    }
}
