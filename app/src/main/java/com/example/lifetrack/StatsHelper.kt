package com.example.lifetrack

object StatsHelper {

    fun calculateTaskCompletion(tasks: List<TaskEntity>): String {
        if (tasks.isEmpty()) return "0%"
        val completed = tasks.count { it.status == "Completed" }
        val percentage = (completed.toDouble() / tasks.size) * 100
        return "${percentage.toInt()}% (${completed}/${tasks.size})"
    }

    fun calculateTotalFocusTime(sessions: List<FocusSessionEntity>): String {
        // CHANGED: Use actualMinutes instead of durationMinutes
        val totalMinutes = sessions.sumOf { it.actualMinutes }
        val hours = totalMinutes / 60
        val mins = totalMinutes % 60
        return "${hours}h ${mins}m"
    }

    fun calculateTotalActivityTime(sessions: List<ActivitySessionEntity>): String {
        // This one stays as durationMinutes because ActivitySessionEntity still uses it
        val totalMinutes = sessions.sumOf { it.durationMinutes }
        val hours = totalMinutes / 60
        val mins = totalMinutes % 60
        return "${hours}h ${mins}m"
    }
}