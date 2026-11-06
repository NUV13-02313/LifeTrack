package com.example.lifetrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardFragment : Fragment() {
    private lateinit var database: LifeTrackDatabase
    private lateinit var todoAdapter: TodayTodoAdapter
    private lateinit var blockAdapter: TimeBlockAdapter
    private lateinit var todayDate: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = LifeTrackDatabase.getDatabase(requireContext())
        todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // --- NEW: Set the Date Subtitle ---
        val dateSubtitle = view.findViewById<TextView>(R.id.dateSubtitle)
        val displayDateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()) // e.g., "Friday, November 6"
        dateSubtitle.text = displayDateFormat.format(Date())
        // --------------------------------



        // --- PRIORITIES (Today's to-do list) ---
        val todoRv = view.findViewById<RecyclerView>(R.id.todoRecyclerView)
        todoRv.layoutManager = LinearLayoutManager(requireContext())
        todoAdapter = TodayTodoAdapter { task ->
            lifecycleScope.launch {
                database.todayTodoDao().update(task.copy(isCompleted = !task.isCompleted))
            }
        }
        todoRv.adapter = todoAdapter
        lifecycleScope.launch {
            database.todayTodoDao().observe(todayDate).collectLatest { todoAdapter.submitList(it) }
        }
        attachSwipeDelete(todoRv) { pos ->
            lifecycleScope.launch { database.todayTodoDao().delete(todoAdapter.getItem(pos)) }
        }
        view.findViewById<Button>(R.id.addTodoButton).setOnClickListener { showAddTaskSheet() }

        // --- DAILY SCHEDULE (hour-based blocks) ---
        // --- DAILY SCHEDULE (hour-based blocks) ---
        val blockRv = view.findViewById<RecyclerView>(R.id.blockRecyclerView)
        blockRv.layoutManager = LinearLayoutManager(requireContext())

        // Pass the click listener to toggle completion instead of deleting
        blockAdapter = TimeBlockAdapter { block ->
            lifecycleScope.launch {
                database.timeBlockDao().update(block.copy(isCompleted = !block.isCompleted))
            }
        }
        blockRv.adapter = blockAdapter
        lifecycleScope.launch {
            database.timeBlockDao().observe(todayDate).collectLatest { blockAdapter.submitList(it) }
        }



        view.findViewById<Button>(R.id.addBlockButton).setOnClickListener { showAddBlockSheet() }
    }

    private fun attachSwipeDelete(rv: RecyclerView, onDelete: (Int) -> Unit) {
        val helper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) = onDelete(vh.adapterPosition)
        }
        ItemTouchHelper(helper).attachToRecyclerView(rv)
    }

    private fun showAddTaskSheet() {
        val sheet = BottomSheetDialog(requireContext())
        sheet.setContentView(R.layout.sheet_add_task)
        val input = sheet.findViewById<TextInputEditText>(R.id.taskTitleInput)!!
        val group = sheet.findViewById<MaterialButtonToggleGroup>(R.id.priorityGroup)!!
        group.check(R.id.btnMed)

        sheet.findViewById<Button>(R.id.saveButton)!!.setOnClickListener {
            val title = input.text.toString().trim()
            if (title.isEmpty()) { input.error = "Required"; return@setOnClickListener }
            val priority = when (group.checkedButtonId) { R.id.btnHigh -> 3; R.id.btnLow -> 1; else -> 2 }
            lifecycleScope.launch {
                database.todayTodoDao().insert(TodayTodoEntity(date = todayDate, title = title, priority = priority))
            }
            sheet.dismiss()
        }
        sheet.findViewById<Button>(R.id.cancelButton)!!.setOnClickListener { sheet.dismiss() }
        sheet.show()
    }

    private fun showAddBlockSheet() {
        val sheet = BottomSheetDialog(requireContext())
        sheet.setContentView(R.layout.sheet_add_block)
        val input = sheet.findViewById<TextInputEditText>(R.id.blockTitleInput)!!
        val startBtn = sheet.findViewById<Button>(R.id.startButton)!!
        val endBtn = sheet.findViewById<Button>(R.id.endButton)!!
        val durationText = sheet.findViewById<TextView>(R.id.durationPreview)!!
        var start: Int? = null
        var end: Int? = null

        fun refresh() {
            startBtn.text = start?.let { "Start: " + TimeUtil.formatMinutes(it) } ?: "Start: tap to set"
            endBtn.text = end?.let { "End: " + TimeUtil.formatMinutes(it) } ?: "End: tap to set"
            durationText.text = if (start != null && end != null)
                "Duration: " + TimeUtil.formatDuration(TimeUtil.duration(start!!, end!!)) else ""
        }

        startBtn.setOnClickListener { pickTime("Start time") { min -> start = min; refresh() } }
        endBtn.setOnClickListener { pickTime("End time") { min -> end = min; refresh() } }

        sheet.findViewById<Button>(R.id.saveButton)!!.setOnClickListener {
            val title = input.text.toString().trim()
            if (title.isEmpty()) { input.error = "Required"; return@setOnClickListener }
            if (start == null || end == null) {
                Toast.makeText(requireContext(), "Set start and end time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                database.timeBlockDao().insert(TimeBlockEntity(date = todayDate, startMinutes = start!!, endMinutes = end!!, title = title))
            }
            sheet.dismiss()
        }
        sheet.findViewById<Button>(R.id.cancelButton)!!.setOnClickListener { sheet.dismiss() }
        refresh()
        sheet.show()
    }

    private fun pickTime(title: String, onPick: (Int) -> Unit) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setTitleText(title)
            .build()
        picker.addOnPositiveButtonClickListener { onPick(picker.hour * 60 + picker.minute) }
        picker.show(parentFragmentManager, title)
    }
}