package com.dvt.weatherapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dvt.weatherapp.Constants.Constants
import com.dvt.weatherapp.R
import com.dvt.weatherapp.ViewModels.FocustViewModel

class FocustAdapter(private val focustList: List<FocustViewModel>, private val context: Context) : RecyclerView.Adapter<FocustAdapter.ViewHolder>() {
    private val constants = Constants()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weather_focust_cardview, parent, false)
        return ViewHolder(view)
    }

    // Binding items to listview
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = focustList[position]

        // Setting the text for day eg Monday
        holder.day.text = ItemsViewModel.day

        // Setting the image
        Glide.with(context)
            .load("${constants.baseImageUrl}${ItemsViewModel.image}")
            .into(holder.imageView)

        // Show the Temperature
        holder.temp.text = "${ItemsViewModel.temp}°"
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return focustList.size
    }

    // Holds the views for adding it to day image and temperature of the focust
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val day: TextView = itemView.findViewById(R.id.day)
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val temp: TextView = itemView.findViewById(R.id.temp)
    }
}
