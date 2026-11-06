package com.example.lifetrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val onTaskClicked: (TaskEntity) -> Unit,
    private val onDeleteClicked: (TaskEntity) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var tasks = listOf<TaskEntity>()

    fun submitList(newTasks: List<TaskEntity>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.taskTitle)
        val statusText: TextView = view.findViewById(R.id.taskStatus)
        val priorityBar: View = view.findViewById(R.id.priorityBar)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]

        holder.titleText.text = currentTask.title
        holder.statusText.text = "Status: ${currentTask.status}"

        // 1. Set Priority Color (Red=High, Yellow=Medium, Green=Low)
        val color = when (currentTask.priority) {
            3 -> ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_light)
            2 -> ContextCompat.getColor(holder.itemView.context, android.R.color.holo_orange_light)
            else -> ContextCompat.getColor(holder.itemView.context, android.R.color.holo_green_light)
        }
        holder.priorityBar.setBackgroundColor(color)

        // 2. Visual feedback for Completed status
        if (currentTask.status == "Completed") {
            holder.titleText.paintFlags = holder.titleText.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            holder.titleText.alpha = 0.5f
            holder.statusText.alpha = 0.5f
        } else {
            holder.titleText.paintFlags = holder.titleText.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.titleText.alpha = 1.0f
            holder.statusText.alpha = 1.0f
        }

        // 3. Handle Clicks
        holder.itemView.setOnClickListener { onTaskClicked(currentTask) }
        holder.deleteButton.setOnClickListener { onDeleteClicked(currentTask) }
    }

    override fun getItemCount() = tasks.size
}