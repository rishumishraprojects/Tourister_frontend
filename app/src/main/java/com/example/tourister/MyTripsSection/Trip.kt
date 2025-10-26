package com.example.tourister.MyTripsSection

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trip(
    val id: Int = 0, // unique id from database
    val destination: String,
    val dates: String,
    val imageUrl: String,
    val status: String,
    val type: String,
    val notes: String? = null
) : Parcelable
