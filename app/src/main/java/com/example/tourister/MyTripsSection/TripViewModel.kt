package com.example.tourister.MyTripsSection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourister.database.TripDatabase
import com.example.tourister.database.TripEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TripViewModel(private val db: TripDatabase) : ViewModel() {

    // 🔹 Trips flow with status and sorting
    val trips = db.tripDao().getAllTrips()
        .map { entities ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = sdf.parse(sdf.format(Date())) ?: Date()
            entities.map { entity ->
                val tripDate = try { sdf.parse(entity.dates) } catch (e: Exception) { Date() }
                val status = if (tripDate.before(today)) "Completed" else "Upcoming"
                Trip(
                    id = entity.id,
                    destination = entity.destination,
                    dates = entity.dates,
                    imageUrl = entity.imageUrl,
                    status = status,
                    type = entity.type,
                    notes = entity.notes
                )
            }.sortedWith(
                compareBy<Trip> { if (it.status == "Upcoming") 0 else 1 }
                    .thenBy { trip ->
                        try { sdf.parse(trip.dates) } catch (e: Exception) { Date() }
                    }
            )
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 🔹 Insert or update a trip
    fun insertTrip(trip: Trip) {
        viewModelScope.launch {
            val entity = TripEntity(
                id = trip.id,
                destination = trip.destination,
                dates = trip.dates,
                imageUrl = trip.imageUrl,
                status = trip.status,
                type = trip.type,
                notes = trip.notes
            )
            db.tripDao().insert(entity)
        }
    }

    // 🔹 Delete a trip
    fun deleteTrip(tripId: Int) {
        viewModelScope.launch {
            db.tripDao().deleteTrip(tripId)
        }
    }
}
