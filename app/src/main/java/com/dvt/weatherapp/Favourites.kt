package com.dvt.weatherapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dvt.weatherapp.Adapters.CurrentAdapter
import com.dvt.weatherapp.Constants.Constants
import com.dvt.weatherapp.ViewModels.CurrentWeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*


class Favourites : AppCompatActivity() {
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val constants = Constants()
    private var client = OkHttpClient()
    private lateinit var home: ImageView
    private lateinit var myFav: ImageView
    private lateinit var search: ImageView
    private val currentWeatherdata = ArrayList<CurrentWeatherViewModel>()
    private var myCity: String = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Initialise
        home = findViewById(R.id.home)
        myFav = findViewById(R.id.favourite)
        search = findViewById(R.id.search)
        myFav.setImageResource(R.mipmap.my_fav)

        //Initialise Location Client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val bundle = intent.extras
        myCity = bundle!!.getString("place", "Default")

        //Get Weather
        getWeather(myCity)

        //Show Home screen
        home.setOnClickListener {
            val intent = Intent(this, Weather::class.java)
            startActivity(intent)
        }
        //Show Find screen
        search.setOnClickListener {
            val intent = Intent(this, Search::class.java)
            startActivity(intent)
        }
    }

    private fun getWeather(city: String) {
        try {
            val request = Request.Builder()
                .url("${constants.baseUrl}${constants.current}q=$city&appid=${constants.apiKey}&${constants.units}")
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Errorrr $e")
                }
                override fun onResponse(call: Call, response: Response) {
                    runOnUiThread {
                        displayCurrentWeather(response)
                    }
                }
            })
        }catch (e: Exception){
            println("Errrr $e")
        }
    }

    private fun displayCurrentWeather(response: Response){
        try {
            val responseData = response.body!!.string()
            val json = JSONObject(responseData)
            val weatherArray = json.getJSONArray("weather")

            val jsonMain = "["+json.get("main")+"]"
            var jsonArray = JSONArray(jsonMain)
            for (i in 0 until weatherArray.length()) {
                val jsonData = weatherArray.getJSONObject(i)
                val mainData = jsonArray.getJSONObject(i)
                runOnUiThread {
                    showWeather(jsonData.getString("description").toUpperCase(), mainData.getString("temp_min").toDouble(),mainData.getString("temp").toDouble(),mainData.getString("temp_max").toDouble())
                }

            }
        }catch (e: Exception){
            println("Error : $e")
        }
    }

    private fun showWeather(description: String, min: Double, current: Double, maximum: Double){
        // Getting my recyclerview for the weather focust
        val currentRecyclerview = findViewById<RecyclerView>(R.id.current_recyclerview)
        // Creating a vertical layout Manager
        currentRecyclerview.layoutManager = LinearLayoutManager(this)
        currentWeatherdata.add(CurrentWeatherViewModel(description,min,current,maximum,myCity))
        // Passing my weather data the Adapter
        val currentWeatherAdapter = CurrentAdapter(currentWeatherdata)
        // Setting the Adapter with the recyclerview
        currentRecyclerview.adapter = currentWeatherAdapter
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeekDayName(tempDate: String?): DayOfWeek {
        val date = LocalDate.parse(tempDate?.take(10).toString())
        return date.dayOfWeek
    }
}

