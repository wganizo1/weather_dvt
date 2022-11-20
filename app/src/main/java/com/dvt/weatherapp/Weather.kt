package com.dvt.weatherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dvt.weatherapp.Adapters.CurrentAdapter
import com.dvt.weatherapp.Adapters.FocustAdapter
import com.dvt.weatherapp.Constants.Constants
import com.dvt.weatherapp.Dialogs.CustomProgressDialog
import com.dvt.weatherapp.ViewModels.CurrentWeatherViewModel
import com.dvt.weatherapp.ViewModels.FocustViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*


class Weather : AppCompatActivity(), LocationListener {
    protected var locationManager: LocationManager? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val progressDialog = CustomProgressDialog()
    private val permissionId = 2
    private val constants = Constants()
    private var client = OkHttpClient()
    private lateinit var favourites: ImageView
    private lateinit var home: ImageView
    private lateinit var search: ImageView
    private val currentWeatherdata = ArrayList<CurrentWeatherViewModel>()
    private val weatherFocustData = ArrayList<FocustViewModel>()
    private var myCity: String = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var onChanged = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Initialise
        home = findViewById(R.id.home)
        search = findViewById(R.id.search)
        favourites = findViewById(R.id.favourite)
        home.setImageResource(R.mipmap.my_home)

        //Initialise Location Client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Get user's current location lat, lon
        getLocation()

        //Show Favoutite Places
        favourites.setOnClickListener {
            val bundle = Bundle()
            val intent = Intent(this, Favourites::class.java)
            bundle.putString ("place", myCity)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        search.setOnClickListener{
            val intent = Intent(this, Search::class.java)
            startActivity(intent)
        }

        home.setOnClickListener{
            this.recreate()
        }
    }

    private fun getLocation() {
        showLoading()
        if (checkPermissions()) {
            if (isLocationEnabled()) {
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
                locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)

            } else {
                hideLoading()
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                finish()
            }
        } else {
            hideLoading()
            requestPermissions()
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun getWeather(lat: String, lon: String) {
        try {
            val request = Request.Builder()
                .url("${constants.baseUrl}${constants.current}lat=$lat&lon=$lon&appid=${constants.apiKey}&${constants.units}")
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    hideLoading()
                    println("Errorrr $e")
                }
                override fun onResponse(call: Call, response: Response) {
                    runOnUiThread {
                        hideLoading()
                        displayCurrentWeather(response)
                    }
                }
            })
        }catch (e: Exception){
            hideLoading()
            println("Errorr $e")
        }
    }

    private fun getWeatherFocust(lat: String, lon: String) {
        try {
            val request = Request.Builder()
                .url("${constants.baseUrl}${constants.forecast}lat=$lat&lon=$lon&appid=${constants.apiKey}&${constants.units}")
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Errorrr $e")
                }
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call, response: Response) {

                        val responseData = response.body!!.string()
                        val json = JSONObject(responseData)
                        val jsonForecast = json.getJSONArray("list")

                        val jsonArray: JSONArray = jsonForecast
                        val focustRecyclerview =
                            findViewById<RecyclerView>(R.id.focust_recyclerview)
                        // Creating a vertical layout Manager

                        runOnUiThread {
                        focustRecyclerview.layoutManager = LinearLayoutManager(applicationContext)

                        for (i in 1 until jsonArray.length()) {
                            val forecast = jsonArray.getJSONObject(i)

                            val dtForecast = forecast.getString("dt_txt")

                            if(dtForecast.take(10) != jsonArray.getJSONObject(i-1).getString("dt_txt").take(10).toString()) {
                                val mainForecast = "[" + forecast.getString("main") + "]"
                                val icon = forecast.getString("weather")

                                var jsonArray = JSONArray(mainForecast)
                                var iconArray = JSONArray(icon)
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
                        //Passing my weather data the Adapter
                        val focustWeatherAdapter = FocustAdapter(weatherFocustData,applicationContext)
                        // Setting the Adapter with the recyclerview
                        focustRecyclerview.adapter = focustWeatherAdapter
                    }
                }
            })
        }catch (e: Exception){
            println("Errorr $e")
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
                    hideLoading()
                    showWeather(jsonData.getString(getString(R.string.descr)).toUpperCase(), mainData.getString("temp_min").toDouble(),mainData.getString("temp").toDouble(),mainData.getString("temp_max").toDouble())
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
        currentWeatherdata.add(CurrentWeatherViewModel(description,min,current,maximum,myCity.toString()))
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
    override fun onLocationChanged(location: Location) {
       getCityName(location.latitude, location.longitude)
        onChanged++
        if(onChanged == 1) {
            getWeather(location.latitude.toString(), location.longitude.toString())
            getWeatherFocust(location.latitude.toString(), location.longitude.toString())
        }
    }

    override fun onProviderDisabled(provider: String) {
        Log.d(getString(R.string.lat), getString(R.string.diable))
    }

    override fun onProviderEnabled(provider: String) {
        Log.d(getString(R.string.lat), getString(R.string.enable))
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        Log.d(getString(R.string.lat), getString(R.string.status))
    }

    private fun getCityName(LATITUDE: Double, LONGITUDE: Double): String? {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: MutableList<android.location.Address>? = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                myCity = addresses.get(0).getLocality()
            } else {
                println(getString(R.string.address_not_found))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            println(getString(R.string.address_not_found))
        }
        return strAdd
    }

    private fun showLoading(){
        progressDialog.show(this,getString(R.string.please_wait))
    }

    private fun hideLoading() {
        progressDialog.dialog.dismiss()
    }
}

