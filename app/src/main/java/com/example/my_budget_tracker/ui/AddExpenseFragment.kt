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
import com.example.my_budget_tracker.viewModel.ExpenseViewModel
import com.example.my_budget_tracker.viewModel.ExpenseViewModelFactory
import com.example.my_budget_tracker.data.Expense
import com.example.my_budget_tracker.data.ExpenseDatabase
import com.example.my_budget_tracker.data.ExpenseRepository
import java.util.Date
import com.example.my_budget_tracker.viewmodel.BudgetViewModel
import com.example.my_budget_tracker.viewmodel.BudgetViewModelFactory


@Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
class AddExpenseFragment : Fragment() {

    // Category icon map to link categories to drawable resources
    private val categoryIconMap = mapOf(
        "Bills" to R.drawable.ic_bills,
        "Food" to R.drawable.ic_food,
        "Health" to R.drawable.ic_health,
        "Insurance" to R.drawable.ic_insurance,
        "Shopping" to R.drawable.ic_shopping,
        "Education" to R.drawable.ic_education
    )

    // Initialize expenseViewModel using a factory
    private val expenseViewModel: ExpenseViewModel by viewModels {
        val repository = ExpenseRepository(ExpenseDatabase.getDatabase(requireContext()).expenseDao())
        ExpenseViewModelFactory(repository)
    }

    // Initialize budgetViewModel using a factory
    private val budgetViewModel: BudgetViewModel by viewModels {
        val application = requireActivity().application // Get the application context
        val budgetDao = ExpenseDatabase.getDatabase(requireContext()).budgetDao()
        val expenseDao = ExpenseDatabase.getDatabase(requireContext()).expenseDao()
        BudgetViewModelFactory(application, budgetDao, expenseDao)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Enable options menu to handle the back button in toolbar
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enable the back button in the toolbar
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.title = "Add Expense"

        // Handle back button press
        activity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp() // Navigate up in the navigation stack
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp() // Handle toolbar back button
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_expense, container, false)

        val categorySpinner = view.findViewById<Spinner>(R.id.category_spinner)
        val amountEditText = view.findViewById<EditText>(R.id.amount_edit_text)
        val addButton = view.findViewById<Button>(R.id.add_button)
        val successMessageLayout = view.findViewById<View>(R.id.success_message_layout) // For success message

        addButton.setOnClickListener {
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
                    amount = amountInEUR, // Store in EUR
                    icon = iconResId,
                    date = Date(),
                    category = name,
                    currency = "EUR" // Indicate that the amount is in EUR
                )
                expenseViewModel.insertExpense(expense)
                amountEditText.text.clear()
                successMessageLayout.visibility = View.VISIBLE
                successMessageLayout.postDelayed({
                    successMessageLayout.visibility = View.GONE
                }, 2000)

                // Check if the budget has been exceeded
                budgetViewModel.checkBudgetExceeded()
                // log checkBudgetExceeded() call



                // Show a toast message
                Toast.makeText(context, "Expense added successfully!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(context, "Please enter a valid amount.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
