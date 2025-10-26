package com.example.tourister

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tourister.Fragments.HomeFragment
import com.example.tourister.Fragments.LoadingDialogFragment
import com.example.tourister.Fragments.ProfileFragment
import com.example.tourister.Fragments.RecommendationFragment
import com.example.tourister.Fragments.TripsFragment
import com.example.tourister.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // 1. Create properties to hold the single instance of each fragment.
    private lateinit var homeFragment: HomeFragment
    private var recommendationsFragment = RecommendationFragment()
    private val tripsFragment = TripsFragment()
    private val profileFragment = ProfileFragment()
    private var activeFragment: Fragment? = null

    // Create one instance of the loading dialog to reuse.
    private val loadingDialog = LoadingDialogFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Get dynamic color and theme the navigation bar ---
        val defaultColor = ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary)
        val dynamicColor = intent.getIntExtra("DYNAMIC_COLOR", defaultColor)
        setupBottomNavTheme(dynamicColor)

        // Instantiate the home fragment with its color data
        homeFragment = HomeFragment.newInstance(dynamicColor)
        recommendationsFragment = RecommendationFragment.newInstance(dynamicColor) // This is the main change

        // 2. This 'if' block is crucial. It ensures the fragments are only
        //    created the VERY FIRST time the activity starts, not on rotation.
        if (savedInstanceState == null) {
            setupInitialFragments()
        }

        // 3. Set up the listener to show/hide fragments and the loading animation.
        setupNavigationListener()
    }

    private fun setupInitialFragments() {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, profileFragment, "4").hide(profileFragment)
            add(R.id.fragment_container, tripsFragment, "3").hide(tripsFragment)
            add(R.id.fragment_container, recommendationsFragment, "2").hide(recommendationsFragment)
            // Add and show the home fragment by default.
            add(R.id.fragment_container, homeFragment, "1")
        }.commit()
        activeFragment = homeFragment
    }

    private fun setupNavigationListener() {
        binding.bottomNavigation.setOnItemSelectedListener {
            val selectedFragment = when (it.itemId) {
                R.id.nav_home -> homeFragment
                R.id.nav_recommendations -> recommendationsFragment
                R.id.nav_trips -> tripsFragment
                R.id.nav_profile -> profileFragment
                else -> null
            }

            if (selectedFragment != null && selectedFragment != activeFragment) {
                // Use a coroutine to manage the loading animation flow
                lifecycleScope.launch {
                    // Step A: Show the loading dialog
                    loadingDialog.show(supportFragmentManager, LoadingDialogFragment.TAG)

                    // Step B: Add a small delay for a smooth visual effect
                    delay(300)

                    // Step C: Perform the fragment transaction to show the new and hide the old
                    supportFragmentManager.beginTransaction().hide(activeFragment!!).show(selectedFragment).commit()
                    activeFragment = selectedFragment

                    // Step D: Dismiss the loading dialog
                    loadingDialog.dismiss()
                }
            }
            true
        }
    }

    private fun setupBottomNavTheme(color: Int) {
        val navColorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(color, Color.GRAY)
        )
        binding.bottomNavigation.itemIconTintList = navColorStateList
        binding.bottomNavigation.itemTextColor = navColorStateList
    }
}