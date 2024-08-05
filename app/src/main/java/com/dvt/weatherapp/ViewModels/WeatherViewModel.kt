// WeatherViewModel.kt
package com.dvt.weatherapp.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dvt.weatherapp.Models.MyWeatherModel
import org.json.JSONObject

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MyWeatherModel()
    val weatherData = MutableLiveData<JSONObject>()
    val weatherFocustData = MutableLiveData<JSONObject>()
    val error = MutableLiveData<String>()

    fun getWeather(city: String) {
        repository.getWeather(city) { result ->
            result.onSuccess {
                weatherData.postValue(it)
            }.onFailure {
                error.postValue(it.message)
            }
        }
    }

    fun getWeatherFocust(lat: Double, lon: Double) {
        repository.getWeatherFocust(lat, lon) { result ->
            result.onSuccess {
                weatherFocustData.postValue(it)
            }.onFailure {
                error.postValue(it.message)
            }
        }
    }
}
