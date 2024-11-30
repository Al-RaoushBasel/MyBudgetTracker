package com.example.my_budget_tracker.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.my_budget_tracker.R
import com.example.my_budget_tracker.data.CurrencyManager
import com.example.my_budget_tracker.data.Expense
import com.example.my_budget_tracker.data.ExpenseDatabase
import com.example.my_budget_tracker.data.ExpenseRepository
import com.example.my_budget_tracker.databinding.FragmentAnalysisBinding
import com.example.my_budget_tracker.viewModel.ExpenseViewModel
import com.example.my_budget_tracker.viewModel.ExpenseViewModelFactory
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class AnalysisFragment : Fragment() {

    // --------------------------- Properties ---------------------------
    private val expenseViewModel: ExpenseViewModel by viewModels {
        val repository = ExpenseRepository(
            expenseDao = ExpenseDatabase.getDatabase(requireContext()).expenseDao()
        )
        ExpenseViewModelFactory(repository)
    }

    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!
    private var isHighlighted = false // Tracks if the highest expense is currently highlighted

    // --------------------------- Lifecycle Methods ---------------------------

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

        // Observe expenses and update UI
        expenseViewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->
            updateChartWithExpenses(expenses, pieChart)
            updateStatistics(expenses)
        }
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

    // --------------------------- Chart Methods ---------------------------

    private fun updateChartWithExpenses(expenses: List<Expense>, pieChart: PieChart) {
        val categoryTotals = expenses.groupBy { it.name }
            .mapValues { entry ->
                entry.value.sumOf { expense ->
                    CurrencyManager.convertAmount(expense.amount, "EUR", CurrencyManager.selectedCurrency)
                }
            }

        val entries = categoryTotals.map { (category, total) ->
            PieEntry(total.toFloat(), category)
        }

        val colors = listOf(
            Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW,
            Color.CYAN, Color.parseColor("#FFA500"), Color.LTGRAY, Color.DKGRAY,
            Color.parseColor("#FF5733")
        )

        val dataSet = PieDataSet(entries, "Categories").apply {
            this.colors = colors
            valueTextColor = Color.WHITE
            valueTextSize = 14f
            sliceSpace = 2f
            selectionShift = 5f
        }

        pieChart.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            centerText = "Expense Analysis"
            setCenterTextSize(20f)
            setCenterTextTypeface(Typeface.DEFAULT_BOLD)
            setCenterTextColor(Color.DKGRAY)
            legend.apply {
                isEnabled = true
                textColor = Color.DKGRAY
                textSize = 12f
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            }
            rotationAngle = 45f
            animateY(1400, Easing.EaseInOutQuad)
            invalidate()
        }

        pieChart.setOnChartValueSelectedListener(object : com.github.mikephil.charting.listener.OnChartValueSelectedListener {
            override fun onValueSelected(e: com.github.mikephil.charting.data.Entry?, h: com.github.mikephil.charting.highlight.Highlight?) {
                if (e is PieEntry) {
                    Toast.makeText(requireContext(), "${e.label}: ${CurrencyManager.formatAmount(e.value.toDouble())}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected() {}
        })
    }

    private fun highlightHighestExpense() {
        val expenses = expenseViewModel.allExpenses.value ?: return
        val categoryTotals = expenses.groupBy { it.name }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        if (isHighlighted) {
            resetChartColors(categoryTotals)
        } else {
            val highestCategory = categoryTotals.maxByOrNull { it.value }?.key
            val highlightedColor = Color.parseColor("#FFA500")

            val entries = categoryTotals.map { (category, total) ->
                PieEntry(total.toFloat(), "$category (${CurrencyManager.formatAmount(total)})")
            }

            val colors = categoryTotals.map { (category, _) ->
                if (category == highestCategory) highlightedColor else Color.LTGRAY
            }

            val dataSet = PieDataSet(entries, "Categories").apply {
                this.colors = colors
                valueTextColor = Color.WHITE
                valueTextSize = 14f
            }

            binding.pieChart.data = PieData(dataSet)
            binding.pieChart.invalidate()
        }

        isHighlighted = !isHighlighted
    }

    private fun resetChartColors(categoryTotals: Map<String, Double>) {
        val entries = categoryTotals.map { (category, total) ->
            PieEntry(total.toFloat(), category)
        }

        val defaultColors = listOf(
            Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW,
            Color.CYAN, Color.parseColor("#FFA500"), Color.LTGRAY, Color.DKGRAY,
            Color.parseColor("#FF5733")
        )

        val dataSet = PieDataSet(entries, "Categories").apply {
            this.colors = defaultColors
            valueTextColor = Color.WHITE
            valueTextSize = 14f
        }

        binding.pieChart.data = PieData(dataSet)
        binding.pieChart.invalidate()
    }

    // --------------------------- Statistics Methods ---------------------------

    private fun updateStatistics(expenses: List<Expense>) {
        if (expenses.isEmpty()) {
            binding.totalSpending.text = "Total Spending: ${CurrencyManager.formatAmount(0.0)}"
            binding.highestExpenseCategory.text = "Highest Expense Category: N/A"
            binding.averageDailySpending.text = "Average Daily Spending: ${CurrencyManager.formatAmount(0.0)}"
            binding.expenseComparison.text = "Comparison (Food vs Shopping): N/A"
            return
        }

        val convertedExpenses = expenses.map { expense ->
            expense.copy(amount = CurrencyManager.convertAmount(expense.amount, "EUR", CurrencyManager.selectedCurrency))
        }

        val totalSpending = convertedExpenses.sumOf { it.amount }
        val highestCategory = convertedExpenses.groupBy { it.name }
            .maxByOrNull { it.value.sumOf { expense -> expense.amount } }?.key ?: "N/A"

        val daysInMonth = java.util.Calendar.getInstance().getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
        val avgDailySpending = totalSpending / daysInMonth

        binding.totalSpending.text = "Total Spending: ${CurrencyManager.formatAmount(totalSpending)}"
        binding.highestExpenseCategory.text = "Highest Expense Category: $highestCategory"
        binding.averageDailySpending.text = "Average Daily Spending: ${CurrencyManager.formatAmount(avgDailySpending)}"

        val comparison = compareSpending(convertedExpenses, "Food", "Shopping")
        binding.expenseComparison.text = "Comparison (Food vs Shopping): $comparison"
    }

    private fun compareSpending(expenses: List<Expense>, category1: String, category2: String): String {
        val category1Total = expenses.filter { it.name == category1 }.sumOf { it.amount }
        val category2Total = expenses.filter { it.name == category2 }.sumOf { it.amount }
        return when {
            category1Total > category2Total -> "$category1 higher (${CurrencyManager.formatAmount(category1Total)} vs ${CurrencyManager.formatAmount(category2Total)})"
            category1Total < category2Total -> "$category2 higher (${CurrencyManager.formatAmount(category2Total)} vs ${CurrencyManager.formatAmount(category1Total)})"
            else -> "Equal spending (${CurrencyManager.formatAmount(category1Total)})"
        }
    }
}
