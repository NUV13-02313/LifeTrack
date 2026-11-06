package com.example.lifetrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyReportAdapter : RecyclerView.Adapter<DailyReportAdapter.VH>() {
    private var items = listOf<DailyReportEntity>()
    fun submitList(list: List<DailyReportEntity>) { items = list; notifyDataSetChanged() }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.reportDate)
        val focus: TextView = view.findViewById(R.id.reportFocus)
        val activity: TextView = view.findViewById(R.id.reportActivity)
        val phone: TextView = view.findViewById(R.id.reportPhone)
        val tasks: TextView = view.findViewById(R.id.reportTasks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_daily_report, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val dateObj = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(item.date)
        holder.date.text = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(dateObj ?: Date())

        holder.focus.text = "${item.focusMins / 60}h ${item.focusMins % 60}m"
        holder.activity.text = "${item.activityMins / 60}h ${item.activityMins % 60}m"
        holder.phone.text = "${item.phoneMins / 60}h ${item.phoneMins % 60}m"
        holder.tasks.text = "Tasks: ${item.tasksCompleted}/${item.tasksTotal}"
    }

    override fun getItemCount() = items.size
}