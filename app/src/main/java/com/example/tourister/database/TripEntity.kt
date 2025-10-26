package com.example.tourister.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val destination: String,
    val dates: String,
    val imageUrl: String,
    val status: String,
    val type: String,
    val notes: String? = null
)
