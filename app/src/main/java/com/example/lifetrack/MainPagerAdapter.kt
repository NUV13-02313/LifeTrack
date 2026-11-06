package com.example.lifetrack

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 4 // Changed from 3 to 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DashboardFragment()
            1 -> FocusFragment()
            2 -> PhoneUsageFragment() // NEW
            else -> AnalyticsFragment()
        }
    }
}