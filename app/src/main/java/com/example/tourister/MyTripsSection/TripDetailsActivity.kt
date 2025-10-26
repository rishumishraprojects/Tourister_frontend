package com.example.tourister.MyTripsSection

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.tourister.databinding.ActivityTripDetailsBinding
import com.example.tourister.database.TripDatabase
import com.example.tourister.database.TripEntity
import com.example.tourister.R
import kotlinx.coroutines.launch

class TripDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTripDetailsBinding
    private lateinit var trip: Trip
    private var tripId: Int = 0
    private var themeColor: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve data from intent
        tripId = intent.getIntExtra("TRIP_ID", 0)
        themeColor = intent.getIntExtra(
            "DYNAMIC_COLOR",
            ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary)
        )

        trip = Trip(
            id = tripId,
            destination = intent.getStringExtra("DESTINATION") ?: "",
            dates = intent.getStringExtra("DATES") ?: "",
            imageUrl = intent.getStringExtra("IMAGE_URL") ?: "",
            status = intent.getStringExtra("STATUS") ?: "Upcoming",
            type = intent.getStringExtra("TYPE") ?: "manual",
            notes = intent.getStringExtra("NOTES")
        )

        // Set UI
        binding.tripDetailsDestination.text = trip.destination
        binding.tripDetailsDates.text = trip.dates
        binding.notesEditText.setText(trip.notes)
        binding.tripDetailsStatus.text = trip.status
        binding.tripDetailsStatus.setTextColor(
            if (trip.status == "Upcoming") resources.getColor(R.color.red)
            else resources.getColor(R.color.green)
        )

        // Dynamic theming
        applyThemeColor(themeColor)

        if (trip.imageUrl.isNotBlank()) {
            Glide.with(this)
                .load(trip.imageUrl)
                .centerCrop()
                .into(binding.tripDetailsImage)
        }

        binding.saveTripFab.setOnClickListener { saveTrip() }
    }

    private fun applyThemeColor(color: Int) {
        val colorStateList = ColorStateList.valueOf(color)
        binding.saveTripFab.backgroundTintList = colorStateList
        binding.notesTextInputLayout.setBoxStrokeColor(color)
        binding.notesTextInputLayout.setBoxStrokeWidth(3)
    }

    private fun saveTrip() {
        lifecycleScope.launch {
            try {
                val tripDao = TripDatabase.getDatabase(applicationContext).tripDao()
                val tripEntity = TripEntity(
                    id = trip.id, // IMPORTANT: update existing trip
                    destination = trip.destination,
                    dates = trip.dates,
                    imageUrl = trip.imageUrl,
                    status = trip.status,
                    type = trip.type,
                    notes = binding.notesEditText.text.toString()
                )
                tripDao.insert(tripEntity) // replaces existing row
                Toast.makeText(this@TripDetailsActivity, "Trip saved successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@TripDetailsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun getIntent(context: Context, trip: Trip, themeColor: Int): Intent {
            return Intent(context, TripDetailsActivity::class.java).apply {
                putExtra("TRIP_ID", trip.id)
                putExtra("DESTINATION", trip.destination)
                putExtra("DATES", trip.dates)
                putExtra("IMAGE_URL", trip.imageUrl)
                putExtra("STATUS", trip.status)
                putExtra("TYPE", trip.type)
                putExtra("NOTES", trip.notes)
                putExtra("DYNAMIC_COLOR", themeColor)
            }
        }
    }



}
