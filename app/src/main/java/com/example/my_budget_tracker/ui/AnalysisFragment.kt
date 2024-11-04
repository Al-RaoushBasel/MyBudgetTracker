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
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.example.my_budget_tracker.data.Expense


class AnalysisFragment : Fragment() {

    // Initialize ExpenseViewModel with a factory to access the repository
    private val expenseViewModel: ExpenseViewModel by viewModels {
        val repository = ExpenseRepository(
            expenseDao = ExpenseDatabase.getDatabase(requireContext()).expenseDao()
        )
        ExpenseViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analysis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Reference the PieChart from the XML layout
        val pieChart = view.findViewById<PieChart>(R.id.pie_chart)

        // Observe expenses from ViewModel and update the chart when data changes
        expenseViewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->
            updateChartWithExpenses(expenses, pieChart)
        }
    }

    private fun updateChartWithExpenses(expenses: List<Expense>, pieChart: PieChart) {
        // Group expenses by category and sum up the amounts
        val categoryTotals = expenses.groupBy { it.name }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        // Convert the data into PieEntry format
        val entries = categoryTotals.map { (category, total) ->
            PieEntry(total.toFloat(), category)
        }

// Define the colors list with custom colors using parseColor() where needed
        val colors = listOf(
            Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.MAGENTA,
            Color.YELLOW,
            Color.CYAN,
            Color.parseColor("#FFA500"), // Orange
            Color.LTGRAY,
            Color.DKGRAY,
            Color.parseColor("#FF5733") // Another custom color
        )

// Set up the data set for the PieChart with predefined colors
        val dataSet = PieDataSet(entries, "Categories")
        dataSet.colors = colors // Apply the color list here
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14f

        // Set up the data for the PieChart and apply it
        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.invalidate() // Refresh the chart with data

        // Customize the PieChart appearance
        pieChart.description.isEnabled = false
        pieChart.centerText = "Expense Analysis"
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AnalysisFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
