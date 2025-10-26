package com.example.tourister

data class Flight(
    val airline: String,
    val flightNumber: String,
    val from: String,
    val to: String,
    val departureTime: String,
    val arrivalTime: String,
    val price: String
)