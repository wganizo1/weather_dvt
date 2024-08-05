// Weather.kt
package com.dvt.weatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dvt.weatherapp.Adapters.CurrentAdapter
import com.dvt.weatherapp.Adapters.FocustAdapter
import com.dvt.weatherapp.Dialogs.CustomProgressDialog
import com.dvt.weatherapp.Utils.LocationUtils
import com.dvt.weatherapp.ViewModels.CurrentWeatherViewModel
import com.dvt.weatherapp.ViewModels.FocustViewModel
import com.dvt.weatherapp.ViewModels.WeatherViewModel
import com.google.android.gms.location.LocationServices
import org.json.JSONArray
import org.json.JSONObject
import java.time.DayOfWeek
import java.time.LocalDate

class Weather : AppCompatActivity() {
    private val weatherViewModel: WeatherViewModel by viewModels()
    private lateinit var locationUtils: LocationUtils
    private lateinit var home: ImageView
    private lateinit var myFav: ImageView
    private lateinit var search: ImageView
    private val progressDialog = CustomProgressDialog()
    private var cityName: String? = null
    private var lat: Double? = null
    private var lon: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather)

        home = findViewById(R.id.home)
        myFav = findViewById(R.id.favourite)
        search = findViewById(R.id.search)

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationUtils = LocationUtils(this, locationManager, LocationServices.getFusedLocationProviderClient(this))
        locationUtils.getLocation()
        weatherViewModel.weatherData.observe(this) { json ->
            displayCurrentWeather(json)
        }

        weatherViewModel.weatherFocustData.observe(this) { json ->
            displayWeatherFocust(json)
        }

        weatherViewModel.error.observe(this) { errorMessage ->
            hideLoading()
        }
        val bundle = intent.extras
        if (bundle != null) {
            cityName = bundle.getString("place", "Default")
            showLoading()
            weatherViewModel.getWeather(cityName!!)
        } else {
            showLoading()
            getCurrentLocation()
        }
        myFav.setOnClickListener {
            // Hide the activity's content
            findViewById<View>(R.id.weather_scrollview).visibility = View.GONE

            // Show the FavoritesFragment
            val fragment = FavoritesFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        search.setOnClickListener {
            val intent = Intent(this, Search::class.java)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            // Show the activity's content again
            findViewById<View>(R.id.weather_scrollview).visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }


    private fun displayCurrentWeather(json: JSONObject) {
        val weatherArray = json.getJSONArray("weather")
        val jsonMain = "[" + json.get("main") + "]"
        val jsonArray = JSONArray(jsonMain)

        val description = weatherArray.getJSONObject(0).getString("description").toUpperCase()
        val mainData = jsonArray.getJSONObject(0)
        val min = mainData.getString("temp_min").toDouble()
        val current = mainData.getString("temp").toDouble()
        val maximum = mainData.getString("temp_max").toDouble()

        showWeather(description, min, current, maximum)
    }

    private fun displayWeatherFocust(json: JSONObject) {
        val jsonForecast = json.getJSONArray("list")
        val weatherFocustData = ArrayList<FocustViewModel>()

        for (i in 1 until jsonForecast.length()) {
            val forecast = jsonForecast.getJSONObject(i)
            val dtForecast = forecast.getString("dt_txt")

            if (dtForecast.take(10) != jsonForecast.getJSONObject(i - 1).getString("dt_txt").take(10)) {
                val mainForecast = "[" + forecast.getString("main") + "]"
                val icon = forecast.getString("weather")

                val jsonArray = JSONArray(mainForecast)
                val iconArray = JSONArray(icon)
                val forecastData = jsonArray.getJSONObject(0)
                val iconData = iconArray.getJSONObject(0)

                weatherFocustData.add(
                    FocustViewModel(
                        getWeekDayName(dtForecast).toString(),
                        "${iconData.getString("icon")}.png",
                        forecastData.getString("temp").toDouble()
                    )
                )
            }
        }

        val focustRecyclerview = findViewById<RecyclerView>(R.id.focust_recyclerview)
        focustRecyclerview.layoutManager = LinearLayoutManager(applicationContext)
        val focustWeatherAdapter = FocustAdapter(weatherFocustData, applicationContext)
        focustRecyclerview.adapter = focustWeatherAdapter
        hideLoading()
    }

    private fun showWeather(description: String, min: Double, current: Double, maximum: Double) {
        val currentRecyclerview = findViewById<RecyclerView>(R.id.current_recyclerview)
        currentRecyclerview.layoutManager = LinearLayoutManager(this)
        val currentWeatherdata = ArrayList<CurrentWeatherViewModel>()
        currentWeatherdata.add(CurrentWeatherViewModel(description, min, current, maximum, cityName!!))
        val currentWeatherAdapter = CurrentAdapter(currentWeatherdata)
        currentRecyclerview.adapter = currentWeatherAdapter
        hideLoading()
    }

    private fun getWeekDayName(tempDate: String?): DayOfWeek {
        val date = LocalDate.parse(tempDate?.take(10).toString())
        return date.dayOfWeek
    }

    private fun getCurrentLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    lat = location.latitude
                    lon = location.longitude
                    cityName = locationUtils.getCityName(lat!!, lon!!).toString()
                    weatherViewModel.getWeather(cityName!!)
                    weatherViewModel.getWeatherFocust(lat!!, lon!!)
                    hideLoading()
                }
            }
        }catch (e: Exception){
            println("Error $e")
        }
    }

    private fun showLoading() {
        progressDialog.show(this, getString(R.string.please_wait))
    }

    private fun hideLoading() {
        progressDialog.dialog.dismiss()
    }
}
