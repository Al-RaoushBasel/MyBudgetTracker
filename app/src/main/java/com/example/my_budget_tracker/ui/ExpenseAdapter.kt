package com.example.my_budget_tracker.ui

import com.example.my_budget_tracker.data.CurrencyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.my_budget_tracker.R
import com.example.my_budget_tracker.data.Expense

class ExpenseAdapter(private var expenses: List<Expense>) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.expense_name)
        val amountTextView: TextView = view.findViewById(R.id.expense_amount)
        val iconImageView: ImageView = view.findViewById(R.id.expense_icon)
        val dateTextView: TextView = view.findViewById(R.id.expense_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        val convertedAmount = CurrencyManager.convertAmount(
            expense.amount, // Stored in EUR
            "EUR", // Base currency
            CurrencyManager.selectedCurrency // Convert to selected currency
        )
        holder.amountTextView.text = CurrencyManager.formatAmount(convertedAmount)
        holder.nameTextView.text = expense.name
        holder.iconImageView.setImageResource(expense.icon)
        holder.dateTextView.text = java.text.SimpleDateFormat("dd/MM/yyyy").format(expense.date)
    }



    override fun getItemCount() = expenses.size

    // Sort by date
    fun sortByDate() {
        expenses = expenses.sortedBy { it.date }
        notifyDataSetChanged()
    }

    // Sort by amount
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

    // Sort by category (you might want to sort alphabetically by category name)
    fun sortByCategory() {
        expenses = expenses.sortedBy { it.category } // Adjust based on your model field
        notifyDataSetChanged()
    }

    // Update expenses and handle currency conversions
    fun updateExpenses(newExpenses: List<Expense>) {
        this.expenses = newExpenses
        notifyDataSetChanged()
    }
}
