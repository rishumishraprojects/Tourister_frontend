package com.example.tourister.ProfileSection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tourister.R
import com.example.tourister.database.TripDatabase
import com.example.tourister.database.User
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import android.util.Log

class ProfileViewModel(private val db: TripDatabase) : ViewModel() {

    // Trip count
    val tripCount = db.tripDao().getAllTrips()
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // Badges with name, description, icon, unlocked state
    val badges = tripCount.map { count ->
        listOf(
            Badge(
                name = "First Trip Saved",
                description = "Unlocked after completing 1 trip",
                iconRes = R.drawable.ic_badge_first_trip,
                isUnlocked = count >= 1
            ),
            Badge(
                name = "Explorer",
                description = "Unlocked after completing 3 trips",
                iconRes = R.drawable.ic_badge_explorer,
                isUnlocked = count >= 3
            ),
            Badge(
                name = "Adventurer",
                description = "Unlocked after completing 5 trips",
                iconRes = R.drawable.ic_badge_addict,
                isUnlocked = count >= 5
            )
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // User profile
    val user = db.userDao().getUser()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun saveUser(first: String, last: String, img: String?) {
        viewModelScope.launch {
            val user = User(firstName = first, lastName = last, profileImagePath = img)
            db.userDao().insertUser(user)
            Log.d("ProfileViewModel", "✅ Saved user with image: $img")
        }
    }
}
