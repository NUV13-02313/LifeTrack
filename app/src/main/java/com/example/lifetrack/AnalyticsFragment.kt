package com.example.lifetrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AnalyticsFragment : Fragment() {
    private lateinit var database: LifeTrackDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_analytics, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = LifeTrackDatabase.getDatabase(requireContext())

        val reportsRv = view.findViewById<RecyclerView>(R.id.reportsRecyclerView)
        reportsRv.layoutManager = LinearLayoutManager(requireContext())
        val reportAdapter = DailyReportAdapter()
        reportsRv.adapter = reportAdapter

        // Auto-Purge & Report Generation Logic (Runs on load)
        lifecycleScope.launch {
            val yesterday = ReportHelper.getYesterdayDate()
            val existingReport = database.dailyReportDao().getReportForDate(yesterday)

            if (existingReport == null) {
                val focusSessions = database.focusSessionDao().getAllFocusSessions().first()
                val activitySessions = database.activitySessionDao().getAllSessionsFlow().first()
                val phoneUsages = database.phoneUsageDao().getUsageForDate(yesterday).first()
                val tasks = database.taskDao().getAllTasksFlow().first()

                val report = ReportHelper.generateDailyReport(yesterday, focusSessions, activitySessions, phoneUsages, tasks)
                database.dailyReportDao().insertReport(report)
            }
        }

        // Watch Reports
        lifecycleScope.launch {
            database.dailyReportDao().getAllReports().collectLatest { reports ->
                reportAdapter.submitList(reports)
            }
        }
    }
}