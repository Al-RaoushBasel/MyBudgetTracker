package com.example.my_budget_tracker.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_budget_tracker.R
import com.example.my_budget_tracker.viewModel.ExpenseViewModel
import com.example.my_budget_tracker.viewModel.ExpenseViewModelFactory
import com.example.my_budget_tracker.data.ExpenseDatabase
import com.example.my_budget_tracker.data.ExpenseRepository

class RecordsFragment : Fragment() {

    private val expenseViewModel: ExpenseViewModel by viewModels {
        val repository = ExpenseRepository(
            expenseDao = ExpenseDatabase.getDatabase(requireContext()).expenseDao()
        )
        ExpenseViewModelFactory(repository)
    }
    private lateinit var expenseRecyclerView: RecyclerView
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Enable the options menu
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_records, container, false)
        expenseRecyclerView = view.findViewById(R.id.expense_recycler_view)
        expenseRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Observe expenses from ViewModel
        expenseViewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->
            expenseAdapter = ExpenseAdapter(expenses)
            expenseRecyclerView.adapter = expenseAdapter
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.records_menu, menu) // Inflate the menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear_all -> {
                // Call ViewModel's method to clear all expenses
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
            else -> super.onOptionsItemSelected(item)
        }
    }

}
