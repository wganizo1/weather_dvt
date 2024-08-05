// WeatherRepository.kt
package com.dvt.weatherapp.Models

import com.dvt.weatherapp.Constants.Constants
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MyWeatherModel {

    private val client = OkHttpClient()
    private val constants = Constants()

    // Fetch current weather data based on city name
    fun getWeather(city: String, callback: (Result<JSONObject>) -> Unit) {
        val url = "${constants.baseUrl}${constants.current}q=$city&appid=${constants.apiKey}&${constants.units}"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonResponse = response.body?.string()
                    jsonResponse?.let {
                        val jsonObject = JSONObject(it)
                        callback(Result.success(jsonObject))
                    } ?: callback(Result.failure(NullPointerException("Response body is null")))
                } else {
                    callback(Result.failure(IOException("Unexpected code $response")))
                }
            }
        })
    }

    // Fetch weather forecast data based on latitude and longitude
    fun getWeatherFocust(lat: Double, lon: Double, callback: (Result<JSONObject>) -> Unit) {
        val url = "${constants.baseUrl}${constants.forecast}lat=$lat&lon=$lon&appid=${constants.apiKey}&${constants.units}"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonResponse = response.body?.string()
                    jsonResponse?.let {
                        val jsonObject = JSONObject(it)
                        callback(Result.success(jsonObject))
                    } ?: callback(Result.failure(NullPointerException("Response body is null")))
                } else {
                    callback(Result.failure(IOException("Unexpected code $response")))
                }
            }
        })
    }
}
