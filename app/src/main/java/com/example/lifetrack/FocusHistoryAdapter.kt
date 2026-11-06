package com.example.lifetrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FocusHistoryAdapter : RecyclerView.Adapter<FocusHistoryAdapter.VH>() {
    private var items = listOf<FocusSessionEntity>()
    fun submitList(list: List<FocusSessionEntity>) { items = list; notifyDataSetChanged() }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.historyTitle)
        val time: TextView = view.findViewById(R.id.historyTime)
        val status: TextView = view.findViewById(R.id.historyStatus)
        val note: TextView = view.findViewById(R.id.historyNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_focus_history, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.taskTitle
        holder.status.text = item.status

        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(item.startTime))
        holder.time.text = "$timeStr • ${item.actualMinutes} mins (Planned: ${item.plannedMinutes}m)"

        if (!item.note.isNullOrEmpty()) {
            holder.note.text = " ${item.note}"
            holder.note.visibility = View.VISIBLE
        } else {
            holder.note.visibility = View.GONE
        }
    }

    override fun getItemCount() = items.size
}