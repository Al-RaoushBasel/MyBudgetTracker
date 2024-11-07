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
import com.example.my_budget_tracker.viewModel.ExpenseViewModel
import com.example.my_budget_tracker.viewModel.ExpenseViewModelFactory
import com.example.my_budget_tracker.data.Expense
import com.example.my_budget_tracker.data.ExpenseDatabase
import com.example.my_budget_tracker.data.ExpenseRepository
import java.util.Date

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
        //inflater.inflate(R.menu.menu_back, menu) // Ensure you have a menu with a back button if needed
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
            val amount = amountEditText.text.toString().toDoubleOrNull() ?: 0.0

            if (name.isNotEmpty() && amount > 0) {
                // Get the appropriate icon based on category, or use default icon if not found
                val iconResId = categoryIconMap[name] ?: R.drawable.ic_categories

                // Create an Expense object and insert it
                val expense = Expense(
                    name = name,
                    amount = amount,
                    icon = iconResId,
                    date = Date(),
                    category = name// or System.currentTimeMillis() if using timestamp
                )
                expenseViewModel.insertExpense(expense)

                // Show success message
                successMessageLayout.visibility = View.VISIBLE
            } else {
                Toast.makeText(context, "Please enter a valid amount.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
