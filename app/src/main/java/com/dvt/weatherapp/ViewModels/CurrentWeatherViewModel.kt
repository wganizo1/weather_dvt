package com.dvt.weatherapp.ViewModels

// This is my model for the current weather details
data class CurrentWeatherViewModel(
    val description: String,
    val minimumTemperature: Double,
    val currentTemperature: Double,
    val maximumTemperature: Double,
    val city: String

)
