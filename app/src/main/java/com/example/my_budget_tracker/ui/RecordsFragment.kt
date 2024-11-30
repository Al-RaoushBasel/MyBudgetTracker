package com.example.my_budget_tracker.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_budget_tracker.R
import com.example.my_budget_tracker.data.CurrencyManager
import com.example.my_budget_tracker.data.ExpenseDatabase
import com.example.my_budget_tracker.data.ExpenseRepository
import com.example.my_budget_tracker.viewModel.ExpenseViewModel
import com.example.my_budget_tracker.viewModel.ExpenseViewModelFactory
import kotlinx.coroutines.launch

class RecordsFragment : Fragment() {

    // --------------------------- Properties ---------------------------
    private val expenseViewModel: ExpenseViewModel by viewModels {
        val repository = ExpenseRepository(
            expenseDao = ExpenseDatabase.getDatabase(requireContext()).expenseDao()
        )
        ExpenseViewModelFactory(repository)
    }
    private lateinit var expenseRecyclerView: RecyclerView
    private lateinit var expenseAdapter: ExpenseAdapter

    // --------------------------- Lifecycle Methods ---------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Enable the options menu
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_records, container, false)
        setupRecyclerView(view)
        initializeCurrencyManager()
        observeExpenses()
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.records_menu, menu) // Inflate the menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear_all -> {
                expenseViewModel.deleteAllExpenses()
                true
            }
            R.id.action_sort_by_date -> {
                expenseAdapter.sortByDate()
                true
            }
            R.id.action_sort_by_amount -> {
                expenseAdapter.sortByAmount()
                true
            }
            R.id.action_sort_by_category -> {
                expenseAdapter.sortByCategory()
                true
            }
            R.id.action_switch_currency -> {
                showCurrencySwitchDialog()
                true
            }
            R.id.action_refresh_rates -> {
                refreshExchangeRates()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // --------------------------- Setup Methods ---------------------------

    /**
     * Sets up the RecyclerView with the ExpenseAdapter.
     */
    private fun setupRecyclerView(view: View) {
        expenseRecyclerView = view.findViewById(R.id.expense_recycler_view)
        expenseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        expenseAdapter = ExpenseAdapter(emptyList())
        expenseRecyclerView.adapter = expenseAdapter
    }

    /**
     * Initializes the CurrencyManager and fetches exchange rates if necessary.
     */
    private fun initializeCurrencyManager() {
        CurrencyManager.initialize(requireContext())
        lifecycleScope.launch {
            try {
                CurrencyManager.fetchExchangeRates()
            } catch (e: Exception) {
                Toast.makeText(context, "Error fetching rates: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --------------------------- Observation Methods ---------------------------

    /**
     * Observes expenses from the ViewModel and updates the RecyclerView with converted values.
     */
    private fun observeExpenses() {
        expenseViewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->
            val convertedExpenses = expenses.map { expense ->
                val convertedAmount = CurrencyManager.convertAmount(
                    expense.amount,
                    "EUR", // Base currency
                    CurrencyManager.selectedCurrency // Convert to selected currency
                )
                expense.copy(
                    amount = convertedAmount,
                    currency = CurrencyManager.selectedCurrency // Update displayed currency
                )
            }
            expenseAdapter.updateExpenses(convertedExpenses)
        }
    }

    // --------------------------- Action Methods ---------------------------

    /**
     * Shows a dialog for switching currencies.
     */
    private fun showCurrencySwitchDialog() {
        val currencies = arrayOf("EUR", "USD")
        var selectedCurrency = CurrencyManager.selectedCurrency

        AlertDialog.Builder(requireContext())
            .setTitle("Select Currency")
            .setSingleChoiceItems(currencies, currencies.indexOf(selectedCurrency)) { _, which ->
                selectedCurrency = currencies[which]
            }
            .setPositiveButton("OK") { _, _ ->
                if (selectedCurrency != CurrencyManager.selectedCurrency) {
                    CurrencyManager.setCurrency(selectedCurrency)
                    Toast.makeText(context, "Currency switched to $selectedCurrency", Toast.LENGTH_SHORT).show()
                    updateRecyclerView()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Refreshes exchange rates and updates the RecyclerView.
     */
    private fun refreshExchangeRates() {
        lifecycleScope.launch {
            try {
                CurrencyManager.fetchExchangeRates(true)
                Toast.makeText(context, "Exchange rates refreshed", Toast.LENGTH_SHORT).show()
                updateRecyclerView()
            } catch (e: Exception) {
                Toast.makeText(context, "Error refreshing rates: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Updates the RecyclerView with the latest expenses and converted values.
     */
    private fun updateRecyclerView() {
        val expenses = expenseViewModel.allExpenses.value ?: emptyList()
        val convertedExpenses = expenses.map { expense ->
            val convertedAmount = CurrencyManager.convertAmount(
                expense.amount,
                "EUR", // Base currency
                CurrencyManager.selectedCurrency // Convert to selected currency
            )
            expense.copy(
                amount = convertedAmount,
                currency = CurrencyManager.selectedCurrency // Update displayed currency
            )
        }
        expenseAdapter.updateExpenses(convertedExpenses)
    }
}
