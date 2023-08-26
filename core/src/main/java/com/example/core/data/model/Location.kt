package com.example.core.data.model

data class Location(
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var distance: Double = 0.0
)
