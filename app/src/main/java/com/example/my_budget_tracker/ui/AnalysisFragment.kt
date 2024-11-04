package com.example.my_budget_tracker.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.my_budget_tracker.R
import com.example.my_budget_tracker.ViewModel.ExpenseViewModel
import com.example.my_budget_tracker.ViewModel.ExpenseViewModelFactory
import com.example.my_budget_tracker.data.ExpenseDatabase
import com.example.my_budget_tracker.data.ExpenseRepository
import com.example.my_budget_tracker.data.Expense
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
        // Inflate the layout using binding
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Reference the PieChart using binding
        val pieChart = binding.pieChart

        // Observe expenses from ViewModel and update the chart when data changes
        expenseViewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->
            updateChartWithExpenses(expenses, pieChart)
            updateStatistics(expenses) // Update statistics with the expenses data
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
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)
    }

    private fun updateStatistics(expenses: List<Expense>) {
        val totalSpending = expenses.sumOf { it.amount }
        val highestCategory = expenses.groupBy { it.name }
            .maxByOrNull { it.value.sumOf { expense -> expense.amount } }?.key ?: "N/A"
        val avgDailySpending = totalSpending / 30  // Example for monthly average

        // Update TextViews
        binding.totalSpending.text = "Total Spending: $%.2f".format(totalSpending)
        binding.highestExpenseCategory.text = "Highest Expense Category: $highestCategory"
        binding.averageDailySpending.text = "Average Daily Spending: $%.2f".format(avgDailySpending)

        // Expense Comparison between two example categories
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
