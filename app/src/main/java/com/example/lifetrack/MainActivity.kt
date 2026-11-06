package com.example.lifetrack

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Detect system dark mode and apply the matching theme BEFORE drawing anything
        val isDark = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        setTheme(if (isDark) R.style.Theme_LifeTrack_Dark else R.style.Theme_LifeTrack)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Make status bar + navigation bar icons black on light, white on dark
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.isAppearanceLightStatusBars = !isDark
        controller.isAppearanceLightNavigationBars = !isDark

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = MainPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Tasks & Plan"
                1 -> "Focus"
                2 -> "Phone" // NEW
                else -> "Analytics"
            }
        }.attach()
    }
}