package com.example.my_budget_tracker.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.my_budget_tracker.R
import com.example.my_budget_tracker.viewModel.ExpenseViewModel
import com.example.my_budget_tracker.viewModel.ExpenseViewModelFactory
import com.example.my_budget_tracker.data.Expense
import com.example.my_budget_tracker.data.ExpenseDatabase
import com.example.my_budget_tracker.data.ExpenseRepository
import com.example.my_budget_tracker.databinding.FragmentAnalysisBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class AnalysisFragment : Fragment() {

    private val expenseViewModel: ExpenseViewModel by viewModels {
        val repository = ExpenseRepository(
            expenseDao = ExpenseDatabase.getDatabase(requireContext()).expenseDao()
        )
        ExpenseViewModelFactory(repository)
    }

    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true) // Enable options menu
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pieChart = binding.pieChart

        // Observe expenses from ViewModel and update the chart when data changes
        expenseViewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->
            updateChartWithExpenses(expenses, pieChart)
            updateStatistics(expenses)
        }
    }

    private fun updateChartWithExpenses(expenses: List<Expense>, pieChart: PieChart) {
        val categoryTotals = expenses.groupBy { it.name }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val entries = categoryTotals.map { (category, total) ->
            PieEntry(total.toFloat(), category)
        }

        val colors = listOf(
            Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW,
            Color.CYAN, Color.parseColor("#FFA500"), Color.LTGRAY, Color.DKGRAY,
            Color.parseColor("#FF5733")
        )

        val dataSet = PieDataSet(entries, "Categories")
        dataSet.colors = colors
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14f

        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.invalidate()

        pieChart.description.isEnabled = false
        pieChart.centerText = "Expense Analysis"
        pieChart.setCenterTextSize(20f)
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)
        pieChart.setCenterTextColor(Color.DKGRAY)
    }

    private fun updateStatistics(expenses: List<Expense>) {
        val totalSpending = expenses.sumOf { it.amount }
        val highestCategory = expenses.groupBy { it.name }
            .maxByOrNull { it.value.sumOf { expense -> expense.amount } }?.key ?: "N/A"
        val avgDailySpending = totalSpending / 30  // Example for monthly average

        binding.totalSpending.text = "Total Spending: $%.2f".format(totalSpending)
        binding.highestExpenseCategory.text = "Highest Expense Category: $highestCategory"
        binding.averageDailySpending.text = "Average Daily Spending: $%.2f".format(avgDailySpending)

        val comparison = compareSpending(expenses, "Food", "Shopping")
        binding.expenseComparison.text = "Comparison (Food vs Shopping): $comparison"
    }

    private fun compareSpending(expenses: List<Expense>, category1: String, category2: String): String {
        val category1Total = expenses.filter { it.name == category1 }.sumOf { it.amount }
        val category2Total = expenses.filter { it.name == category2 }.sumOf { it.amount }
        return when {
            category1Total > category2Total -> "$category1 higher"
            category1Total < category2Total -> "$category2 higher"
            else -> "Equal spending"
        }
    }

    private var isHighlighted = false // Tracks if the highest expense is currently highlighted


    private fun highlightHighestExpense() {
        val expenses = expenseViewModel.allExpenses.value ?: return
        val categoryTotals = expenses.groupBy { it.name }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        // Toggle highlight state
        if (isHighlighted) {
            // Unhighlight: reset to normal colors
            resetChartColors(categoryTotals)
        } else {
            // Highlight the highest category
            val highestCategory = categoryTotals.maxByOrNull { it.value }?.key
            val highlightedColor = Color.parseColor("#FFA500") // Orange highlight color

            val entries = categoryTotals.map { (category, total) ->
                PieEntry(total.toFloat(), category)
            }

            val colors = categoryTotals.map { (category, _) ->
                if (category == highestCategory) highlightedColor else Color.LTGRAY
            }

            val dataSet = PieDataSet(entries, "Categories")
            dataSet.colors = colors
            dataSet.valueTextColor = Color.WHITE
            dataSet.valueTextSize = 14f

            val pieData = PieData(dataSet)
            binding.pieChart.data = pieData
            binding.pieChart.invalidate()
        }

        // Toggle the highlight state
        isHighlighted = !isHighlighted
    }

    private fun resetChartColors(categoryTotals: Map<String, Double>) {
        val entries = categoryTotals.map { (category, total) ->
            PieEntry(total.toFloat(), category)
        }

        // Default colors for each category
        val defaultColors = listOf(
            Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW,
            Color.CYAN, Color.parseColor("#FFA500"), Color.LTGRAY, Color.DKGRAY,
            Color.parseColor("#FF5733")
        )

        val dataSet = PieDataSet(entries, "Categories")
        dataSet.colors = defaultColors
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14f

        val pieData = PieData(dataSet)
        binding.pieChart.data = pieData
        binding.pieChart.invalidate()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_analysis, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_highlight_highest -> {
                highlightHighestExpense()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
