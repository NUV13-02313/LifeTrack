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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhoneUsageFragment : Fragment() {

    private lateinit var database: LifeTrackDatabase
    private lateinit var usageText: TextView

    private var autoRefreshJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_phone_usage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = LifeTrackDatabase.getDatabase(requireContext())
        usageText = view.findViewById(R.id.phoneUsageText)

        view.findViewById<Button>(R.id.refreshUsageButton).setOnClickListener {
            lifecycleScope.launch {
                refreshUsage(openSettingsIfPermissionMissing = true)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Auto-refresh immediately and then every 60 seconds while this tab is open
        autoRefreshJob?.cancel()
        autoRefreshJob = lifecycleScope.launch {
            while (isActive) {
                refreshUsage(openSettingsIfPermissionMissing = false)
                delay(60_000L)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }

    private suspend fun refreshUsage(openSettingsIfPermissionMissing: Boolean) {
        if (!PhoneUsageTracker.hasUsageStatsPermission(requireContext())) {
            usageText.text = "Usage access is not granted.\n\nTap Refresh Usage Data to allow permission."

            if (openSettingsIfPermissionMissing) {
                Toast.makeText(
                    requireContext(),
                    "Please grant Usage Access to LifeTrack",
                    Toast.LENGTH_LONG
                ).show()

                PhoneUsageTracker.openUsageStatsSettings(requireContext())
            }

            return
        }

        usageText.text = "Loading phone usage..."

        try {
            val usageList = PhoneUsageTracker.getTodayUsage(requireContext())

            if (usageList.isEmpty()) {
                usageText.text = "No usage data found yet.\n\nAndroid may take a few minutes to update usage stats."
                return
            }

            val today = usageList.first().date

            // Important: remove old today's rows before inserting fresh data
            database.phoneUsageDao().deleteUsageForDate(today)
            usageList.forEach { database.phoneUsageDao().insertUsage(it) }

            val updatedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

            val text = StringBuilder()
            text.append("Top 6 Apps Today\n")
            text.append("Updated: $updatedTime\n\n")

            usageList.forEachIndexed { index, usage ->
                text.append("${index + 1}. ${usage.appName}\n")
                text.append("   ${formatHoursMinutes(usage.usageMinutes)}\n\n")
            }

            usageText.text = text.toString().trim()

        } catch (e: Exception) {
            usageText.text = "Could not load phone usage.\n\n${e.message}"
        }
    }

    private fun formatHoursMinutes(minutes: Long): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return String.format(Locale.getDefault(), "%02dh %02dm", hours, mins)
    }
}