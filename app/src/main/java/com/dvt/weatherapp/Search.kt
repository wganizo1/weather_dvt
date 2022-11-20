package com.dvt.weatherapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*


class Search : AppCompatActivity() {
    var placesClient: PlacesClient? = null
    private lateinit var favourites: ImageView
    private lateinit var home: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.find_city)
        val apiKey = getString(R.string.api_key)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        home = findViewById(R.id.home)
        favourites = findViewById(R.id.favourite)

        //Show Home screen
        home.setOnClickListener {
            val intent = Intent(this, Weather::class.java)
            startActivity(intent)
        }

        // Create a new Places client instance.
        placesClient = Places.createClient(this)


        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment?
        autocompleteFragment!!.setTypeFilter(TypeFilter.CITIES)
        autocompleteFragment.setPlaceFields(
            Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.PHOTO_METADATAS
            )
        )
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                val bundle = Bundle()
                val intent = Intent(applicationContext, Favourites::class.java)
                bundle.putString ("place", place.name)
                intent.putExtras(bundle)
                startActivity(intent)
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Toast.makeText(applicationContext, status.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}