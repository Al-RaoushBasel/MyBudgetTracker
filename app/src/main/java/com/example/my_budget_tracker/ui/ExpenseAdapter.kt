package com.example.my_budget_tracker.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.my_budget_tracker.R
import com.example.my_budget_tracker.data.CurrencyManager
import com.example.my_budget_tracker.data.Expense
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter(private var expenses: List<Expense>) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    // --------------------------- ViewHolder ---------------------------
    /**
     * Represents a single expense item in the RecyclerView.
     */
    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.expense_name)
        val amountTextView: TextView = view.findViewById(R.id.expense_amount)
        val iconImageView: ImageView = view.findViewById(R.id.expense_icon)
        val dateTextView: TextView = view.findViewById(R.id.expense_date)
    }

    // --------------------------- Adapter Methods ---------------------------

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        // Convert the amount to the selected currency
        val convertedAmount = CurrencyManager.convertAmount(
            expense.amount, // Stored in EUR
            "EUR", // Base currency
            CurrencyManager.selectedCurrency // Convert to selected currency
        )

        // Bind data to the views
        holder.amountTextView.text = CurrencyManager.formatAmount(convertedAmount)
        holder.nameTextView.text = expense.name
        holder.iconImageView.setImageResource(expense.icon)
        holder.dateTextView.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(expense.date)
    }

    override fun getItemCount() = expenses.size

    // --------------------------- Sorting Methods ---------------------------

    /**
     * Sort expenses by date.
     */
    fun sortByDate() {
        expenses = expenses.sortedBy { it.date }
        notifyDataSetChanged()
    }

    /**
     * Sort expenses by amount, considering the selected currency.
     */
    fun sortByAmount() {
        expenses = expenses.sortedBy { expense ->
            CurrencyManager.convertAmount(
                expense.amount,
                expense.currency,
                CurrencyManager.selectedCurrency
            )
        }
        notifyDataSetChanged()
    }

    /**
     * Sort expenses by category name.
     */
    fun sortByCategory() {
        expenses = expenses.sortedBy { it.category }
        notifyDataSetChanged()
    }

    // --------------------------- Update Methods ---------------------------

    /**
     * Update the list of expenses and notify the adapter.
     */
    fun updateExpenses(newExpenses: List<Expense>) {
        this.expenses = newExpenses
        notifyDataSetChanged()
    }
}
