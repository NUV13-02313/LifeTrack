package com.example.lifetrack

import android.content.Context
import android.graphics.Paint
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Helper function to read theme colors (like ltAccent) programmatically
fun Context.ltColor(attr: Int): Int {
    val tv = TypedValue()
    theme.resolveAttribute(attr, tv, true)
    return tv.data
}

class TodayTodoAdapter(
    private val onClick: (TodayTodoEntity) -> Unit
) : RecyclerView.Adapter<TodayTodoAdapter.VH>() {

    private var items = listOf<TodayTodoEntity>()
    fun submitList(list: List<TodayTodoEntity>) { items = list; notifyDataSetChanged() }
    fun getItem(position: Int) = items[position]

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val bar: View = view.findViewById(R.id.priorityBar)
        val title: TextView = view.findViewById(R.id.todoTitle)
        val priorityLabel: TextView = view.findViewById(R.id.priorityLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_today_todo, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val ctx = holder.itemView.context

        holder.title.text = item.title
        holder.priorityLabel.text = when (item.priority) { 3 -> "HIGH"; 1 -> "LOW"; else -> "MED" }

        // Use theme attributes (R.attr) instead of old R.color
        val colorAttr = when (item.priority) {
            3 -> R.attr.ltAccent
            1 -> R.attr.ltDivider
            else -> R.attr.ltTextSecondary
        }
        holder.bar.setBackgroundColor(ctx.ltColor(colorAttr))

        if (item.isCompleted) {
            holder.title.paintFlags = holder.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.title.alpha = 0.4f
        } else {
            holder.title.paintFlags = holder.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.title.alpha = 1f
        }

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}