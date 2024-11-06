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
import com.example.my_budget_tracker.data.ExpenseDatabase
import com.example.my_budget_tracker.databinding.FragmentAnalysisDetailBinding
import com.example.my_budget_tracker.viewmodel.BudgetViewModelFactory
import com.google.android.material.snackbar.Snackbar

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
        val budgetDao = ExpenseDatabase.getDatabase(requireContext()).budgetDao()
        val expenseDao = ExpenseDatabase.getDatabase(requireContext()).expenseDao()
        val factory = BudgetViewModelFactory(budgetDao, expenseDao)
        budgetViewModel = ViewModelProvider(this, factory).get(BudgetViewModel::class.java)

        // Set up the RecyclerView and adapter for category budgets
        adapter = CategoryBudgetAdapter()
        binding.categoryBudgetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.categoryBudgetRecyclerView.adapter = adapter

        // Observe and update the overall budget progress separately
        budgetViewModel.budget.observe(viewLifecycleOwner) { budget ->
            updateOverallBudgetUI(budget)
        }

        // Observe and update each category budget in the RecyclerView
        budgetViewModel.categoryBudgets().observe(viewLifecycleOwner) { categoryBudgets ->
            adapter.submitList(categoryBudgets)
            updateCategoryBudgetUI(categoryBudgets)
        }
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
                deleteCategoryBudgets()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun resetOverallBudget() {
        budgetViewModel.resetOverallBudget()
        Snackbar.make(requireView(), "Overall Budget reset to 0", Snackbar.LENGTH_SHORT).show()

        // Re-observe the budget after reset to update the UI
        budgetViewModel.budget.observe(viewLifecycleOwner) { budget ->
            updateOverallBudgetUI(budget)
        }
    }

    private fun deleteCategoryBudgets() {
        budgetViewModel.deleteAllCategoryBudgets()
        Snackbar.make(requireView(), "All category budgets deleted", Snackbar.LENGTH_SHORT).show()
    }

    private fun updateOverallBudgetUI(budget: Budget?) {
        val totalBudget = budget?.overallBudget ?: 0.0

        budgetViewModel.totalExpenses.observe(viewLifecycleOwner) { totalExpenses ->
            val expenses = totalExpenses ?: 0.0
            if (totalBudget > 0) {
                val progress = (expenses / totalBudget * 100).toInt()
                binding.overallBudgetProgress.progress = progress
                binding.overallBudgetSummaryText.text = "Used: $$expenses of $$totalBudget"
            } else {
                binding.overallBudgetProgress.progress = 0
                binding.overallBudgetSummaryText.text = "No budget set"
            }
        }
    }

    private fun updateCategoryBudgetUI(categoryBudgets: List<CategoryBudget>) {
        // Update each category's progress in the UI
        categoryBudgets.forEach { categoryBudget ->
            budgetViewModel.getCategoryExpenses(categoryBudget.categoryName).observe(viewLifecycleOwner) { totalExpenses ->
                val remainingBudget = categoryBudget.budgetAmount - (totalExpenses ?: 0.0)

                // Update each category's remaining budget in the RecyclerView
                adapter.updateCategoryProgress(categoryBudget.categoryName, remainingBudget, categoryBudget.budgetAmount, totalExpenses ?: 0.0)

                // Update remaining budget in the database, if needed
                budgetViewModel.updateCategoryRemainingBudget(categoryBudget.categoryName, remainingBudget)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
