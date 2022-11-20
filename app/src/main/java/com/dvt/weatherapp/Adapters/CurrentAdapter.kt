package com.dvt.weatherapp.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dvt.weatherapp.R
import com.dvt.weatherapp.ViewModels.CurrentWeatherViewModel

class CurrentAdapter(private val currentList: List<CurrentWeatherViewModel>) : RecyclerView.Adapter<CurrentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.current_weather_cardview, parent, false)
        return ViewHolder(view)
    }

    // Binding items to listview
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = currentList[position]

        // Setting the text
        holder.minimum.text = "${ItemsViewModel.minimumTemperature}°"

        //Current Temperature
        holder.current.text = "${ItemsViewModel.currentTemperature}°"

        // Show the Temperature
        holder.maximum.text = "${ItemsViewModel.maximumTemperature}°"

        //current temp
        holder.currentTemperature.text = "${ItemsViewModel.currentTemperature}°"

        //City
        holder.city.text = ItemsViewModel.city

        //Description
        holder.currentDescription.text = ItemsViewModel.description

        if(ItemsViewModel.description.toUpperCase().contains("CLOUD")) {
            holder.curr.setBackgroundResource(R.drawable.forest_cloudy)
        }
        if(ItemsViewModel.description.toUpperCase().contains("RAIN")) {
            holder.curr.setBackgroundResource(R.drawable.forest_rainy)
        }
        if(ItemsViewModel.description.toUpperCase().contains("SUN")) {
            holder.curr.setBackgroundResource(R.drawable.forest_sunny)
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return currentList.size
    }

    // Holds the views for adding it to day image and temperature of the focust
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val minimum: TextView = itemView.findViewById(R.id.minimum_temp)
        val current: TextView = itemView.findViewById(R.id.current_temp)
        val maximum: TextView = itemView.findViewById(R.id.maximum_temp)
        val city: TextView = itemView.findViewById(R.id.city)
        val currentTemperature: TextView = itemView.findViewById(R.id.current_temperature)
        val currentDescription: TextView = itemView.findViewById(R.id.current_description)
        val curr: RelativeLayout = itemView.findViewById(R.id.curr)
    }
}
