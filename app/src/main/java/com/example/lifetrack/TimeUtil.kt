package com.example.lifetrack

object TimeUtil {
    fun formatMinutes(min: Int): String {
        val h = min / 60
        val m = min % 60
        val amPm = if (h < 12) "AM" else "PM"
        var h12 = h % 12
        if (h12 == 0) h12 = 12
        return String.format("%d:%02d %s", h12, m, amPm)
    }

    fun duration(start: Int, end: Int): Int =
        if (end > start) end - start else (end + 1440) - start

    fun formatDuration(mins: Int): String {
        val h = mins / 60
        val m = mins % 60
        return if (h > 0) "${h}h ${m}m" else "${m}m"
    }
}