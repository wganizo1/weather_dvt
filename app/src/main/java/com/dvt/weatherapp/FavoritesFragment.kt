// Favourites.kt
package com.dvt.weatherapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import org.json.JSONArray
import org.json.JSONObject

class FavoritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        // JSON data as a list of city names
        val dbHelper = CitiesDatabaseHelper(requireContext())
        val citiesJson = dbHelper.getAllCitiesAsJson()
        val cities = parseCityNames(citiesJson)

        // Get the ListView and set an adapter to it
        val listView: ListView = view.findViewById(R.id.cityListView)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, cities)
        listView.adapter = adapter

        // Set an item click listener
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedCity = cities[position]
            val bundle = Bundle()
            val intent = Intent(requireContext(), Weather::class.java)
            bundle.putString("place", selectedCity)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        return view
    }

    private fun parseCityNames(json: String): List<String> {
        val cityNames = mutableListOf<String>()
        val jsonArray = JSONArray(json)
        for (i in 0 until jsonArray.length()) {
            val cityObject: JSONObject = jsonArray.getJSONObject(i)
            cityNames.add(cityObject.getString("city_name"))
        }
        return cityNames
    }
}
