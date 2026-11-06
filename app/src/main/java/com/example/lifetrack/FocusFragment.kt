package com.example.lifetrack

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class FocusFragment : Fragment() {
    private lateinit var database: LifeTrackDatabase

    // Bulletproof Timer Variables
    private var timer: CountDownTimer? = null
    private var plannedMs: Long = 0
    private var remainingMs: Long = 0
    private var isRunning: Boolean = false
    private var currentTaskTitle: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_focus, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = LifeTrackDatabase.getDatabase(requireContext())

        val timerText = view.findViewById<TextView>(R.id.focusTimerText)
        val hoursInput = view.findViewById<TextInputEditText>(R.id.focusHoursInput)
        val minutesInput = view.findViewById<TextInputEditText>(R.id.focusMinutesInput)
        val taskInput = view.findViewById<TextInputEditText>(R.id.focusTaskInput)

        val startBtn = view.findViewById<Button>(R.id.startResumeButton)
        val pauseBtn = view.findViewById<Button>(R.id.pauseButton)
        val stopBtn = view.findViewById<Button>(R.id.stopButton)

        val historyRv = view.findViewById<RecyclerView>(R.id.focusHistoryRecyclerView)
        historyRv.layoutManager = LinearLayoutManager(requireContext())
        val historyAdapter = FocusHistoryAdapter()
        historyRv.adapter = historyAdapter

        // Watch history
        lifecycleScope.launch {
            database.focusSessionDao().getAllFocusSessions().collectLatest { sessions ->
                historyAdapter.submitList(sessions)
            }
        }

        // Presets
        view.findViewById<Button>(R.id.preset25).setOnClickListener {
            minutesInput.setText("25"); hoursInput.setText("0"); resetTimerState()
        }
        view.findViewById<Button>(R.id.preset50).setOnClickListener {
            minutesInput.setText("50"); hoursInput.setText("0"); resetTimerState()
        }
        view.findViewById<Button>(R.id.preset60).setOnClickListener {
            minutesInput.setText("0"); hoursInput.setText("1"); resetTimerState()
        }

        fun updateTimerDisplay(ms: Long) {
            val totalSecs = ms / 1000
            val h = totalSecs / 3600
            val m = (totalSecs % 3600) / 60
            val s = totalSecs % 60
            timerText.text = String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
        }

        // --- START / RESUME ---
        startBtn.setOnClickListener {
            if (!isRunning) {
                if (remainingMs == 0L) {
                    // Fresh start
                    val h = hoursInput.text.toString().toIntOrNull() ?: 0
                    val m = minutesInput.text.toString().toIntOrNull() ?: 0
                    plannedMs = ((h * 60) + m) * 60 * 1000L

                    if (plannedMs <= 0) {
                        Toast.makeText(requireContext(), "Set a time greater than 0", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    remainingMs = plannedMs
                    currentTaskTitle = taskInput.text.toString().trim().ifEmpty { "General Focus" }
                }

                isRunning = true
                startBtn.isEnabled = false
                startBtn.text = "Running..."
                pauseBtn.isEnabled = true
                pauseBtn.text = "Pause"
                stopBtn.isEnabled = true

                timer = object : CountDownTimer(remainingMs, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        remainingMs = millisUntilFinished // Save exact remaining time!
                        updateTimerDisplay(millisUntilFinished)
                    }
                    override fun onFinish() {
                        remainingMs = 0
                        updateTimerDisplay(0)
                        val mins = (plannedMs / 60000).toInt()
                        finishSession(mins, mins, null, "Completed")
                        resetControls()
                    }
                }.start()
            }
        }

        // --- PAUSE ---
        pauseBtn.setOnClickListener {
            if (isRunning) {
                timer?.cancel()
                isRunning = false
                startBtn.isEnabled = true
                startBtn.text = "Resume"
                pauseBtn.isEnabled = false
            }
        }

        // --- STOP ---
        stopBtn.setOnClickListener {
            if (isRunning || remainingMs > 0) {
                timer?.cancel()
                isRunning = false

                val actualMins = ((plannedMs - remainingMs) / 60000).toInt().coerceAtLeast(1)
                val plannedMins = (plannedMs / 60000).toInt()

                val bottomSheet = BottomSheetDialog(requireContext())
                bottomSheet.setContentView(R.layout.dialog_end_focus)

                val noteInput = bottomSheet.findViewById<TextInputEditText>(R.id.noteInput)!!
                val saveButton = bottomSheet.findViewById<Button>(R.id.saveButton)!!
                val discardButton = bottomSheet.findViewById<Button>(R.id.discardButton)!!

                saveButton.setOnClickListener {
                    val noteText = noteInput.text.toString().trim().ifEmpty { null }
                    val status = if (actualMins >= plannedMins) "Completed" else "Stopped Early"
                    finishSession(plannedMins, actualMins, noteText, status)
                    bottomSheet.dismiss()
                    resetControls()
                }

                discardButton.setOnClickListener {
                    bottomSheet.dismiss()
                    resetControls()
                }

                bottomSheet.show()
            }
        }
    }

    private fun finishSession(planned: Int, actual: Int, note: String?, status: String) {
        lifecycleScope.launch {
            database.focusSessionDao().insertSession(
                FocusSessionEntity(
                    taskTitle = currentTaskTitle,
                    plannedMinutes = planned,
                    actualMinutes = actual,
                    startTime = System.currentTimeMillis() - (actual * 60 * 1000),
                    endTime = System.currentTimeMillis(),
                    note = note,
                    status = status
                )
            )
            Toast.makeText(requireContext(), "Focus session saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetControls() {
        timer = null
        isRunning = false
        remainingMs = 0
        plannedMs = 0
        view?.findViewById<TextView>(R.id.focusTimerText)?.text = "00:00:00"
        view?.findViewById<Button>(R.id.startResumeButton)?.apply { isEnabled = true; text = "Start" }
        view?.findViewById<Button>(R.id.pauseButton)?.apply { isEnabled = false; text = "Pause" }
        view?.findViewById<Button>(R.id.stopButton)?.isEnabled = false
    }

    private fun resetTimerState() {
        timer?.cancel()
        isRunning = false
        remainingMs = 0
        plannedMs = 0
        view?.findViewById<TextView>(R.id.focusTimerText)?.text = "00:00:00"
        view?.findViewById<Button>(R.id.startResumeButton)?.apply { isEnabled = true; text = "Start" }
        view?.findViewById<Button>(R.id.pauseButton)?.apply { isEnabled = false; text = "Pause" }
        view?.findViewById<Button>(R.id.stopButton)?.isEnabled = false
    }
}