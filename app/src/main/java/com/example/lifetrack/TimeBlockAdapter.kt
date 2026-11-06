package com.example.lifetrack

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimeBlockAdapter(
    private val onClick: (TimeBlockEntity) -> Unit
) : RecyclerView.Adapter<TimeBlockAdapter.VH>() {

    private var items = listOf<TimeBlockEntity>()
    fun submitList(list: List<TimeBlockEntity>) { items = list; notifyDataSetChanged() }
    fun getItem(position: Int) = items[position]

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val timeRange: TextView = view.findViewById(R.id.timeRange)
        val durationText: TextView = view.findViewById(R.id.durationText)
        val title: TextView = view.findViewById(R.id.blockTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_time_block, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.timeRange.text = "${TimeUtil.formatMinutes(item.startMinutes)} – ${TimeUtil.formatMinutes(item.endMinutes)}"
        holder.durationText.text = TimeUtil.formatDuration(TimeUtil.duration(item.startMinutes, item.endMinutes))
        holder.title.text = item.title

        // Visual feedback for completion
        if (item.isCompleted) {
            holder.title.paintFlags = holder.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.itemView.alpha = 0.5f
        } else {
            holder.title.paintFlags = holder.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.itemView.alpha = 1.0f
        }

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}