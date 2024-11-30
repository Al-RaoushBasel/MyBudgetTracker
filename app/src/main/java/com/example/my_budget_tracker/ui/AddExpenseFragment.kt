package com.example.my_budget_tracker.ui

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.my_budget_tracker.R
import com.example.my_budget_tracker.data.CurrencyManager
import com.example.my_budget_tracker.data.Expense
import com.example.my_budget_tracker.data.ExpenseDatabase
import com.example.my_budget_tracker.data.ExpenseRepository
import com.example.my_budget_tracker.viewModel.BudgetViewModel
import com.example.my_budget_tracker.viewModel.BudgetViewModelFactory
import com.example.my_budget_tracker.viewModel.ExpenseViewModel
import com.example.my_budget_tracker.viewModel.ExpenseViewModelFactory
import java.util.Date

@Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
class AddExpenseFragment : Fragment() {

    // --------------------------- Properties ---------------------------

    // Category icon map to link categories to drawable resources
    private val categoryIconMap = mapOf(
        "Bills" to R.drawable.ic_bills,
        "Food" to R.drawable.ic_food,
        "Health" to R.drawable.ic_health,
        "Insurance" to R.drawable.ic_insurance,
        "Shopping" to R.drawable.ic_shopping,
        "Education" to R.drawable.ic_education
    )

    // ViewModel for managing expenses
    private val expenseViewModel: ExpenseViewModel by viewModels {
        val repository = ExpenseRepository(ExpenseDatabase.getDatabase(requireContext()).expenseDao())
        ExpenseViewModelFactory(repository)
    }

    // ViewModel for managing budgets
    private val budgetViewModel: BudgetViewModel by viewModels {
        val application = requireActivity().application
        val budgetDao = ExpenseDatabase.getDatabase(requireContext()).budgetDao()
        val expenseDao = ExpenseDatabase.getDatabase(requireContext()).expenseDao()
        BudgetViewModelFactory(application, budgetDao, expenseDao)
    }

    // --------------------------- Lifecycle Methods ---------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Enable options menu
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_expense, container, false)

        setupUI(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        handleBackButton()
    }

    // --------------------------- Menu Handling ---------------------------

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // --------------------------- UI Setup Methods ---------------------------

    private fun setupUI(view: View) {
        val categorySpinner = view.findViewById<Spinner>(R.id.category_spinner)
        val amountEditText = view.findViewById<EditText>(R.id.amount_edit_text)
        val addButton = view.findViewById<Button>(R.id.add_button)
        val successMessageLayout = view.findViewById<View>(R.id.success_message_layout)

        addButton.setOnClickListener {
            handleAddExpense(categorySpinner, amountEditText, successMessageLayout)
        }
    }

    private fun setupToolbar() {
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.title = "Add Expense"
    }

    private fun handleBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })
    }

    // --------------------------- Expense Handling ---------------------------

    private fun handleAddExpense(
        categorySpinner: Spinner,
        amountEditText: EditText,
        successMessageLayout: View
    ) {
        val name = categorySpinner.selectedItem.toString()
        val amountInput = amountEditText.text.toString().toDoubleOrNull()

        if (name.isNotEmpty() && amountInput != null && amountInput > 0) {
            val iconResId = categoryIconMap[name] ?: R.drawable.ic_categories

            // Convert entered amount to EUR before storing
            val amountInEUR = CurrencyManager.convertAmount(
                amountInput,
                CurrencyManager.selectedCurrency, // Current selected currency
                "EUR" // Convert to base currency
            )

            val expense = Expense(
                name = name,
                amount = amountInEUR,
                icon = iconResId,
                date = Date(),
                category = name,
                currency = "EUR"
            )
            expenseViewModel.insertExpense(expense)

            amountEditText.text.clear()
            successMessageLayout.visibility = View.VISIBLE
            successMessageLayout.postDelayed({
                successMessageLayout.visibility = View.GONE
            }, 2000)

            budgetViewModel.checkBudgetExceeded()

            Toast.makeText(context, "Expense added successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Please enter a valid amount.", Toast.LENGTH_SHORT).show()
        }
    }
}
