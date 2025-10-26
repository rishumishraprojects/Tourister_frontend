package com.example.tourister

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.tourister.databinding.ItemFlightResultBinding
import kotlinx.coroutines.launch

// Update the constructor to accept a callback function
class FlightResultsAdapter(
    private val flights: List<Flight>,
    private val dynamicColor: Int,
    private val onBookClicked: (Flight) -> Unit // The callback function
) : RecyclerView.Adapter<FlightResultsAdapter.FlightViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val binding = ItemFlightResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FlightViewHolder(binding, dynamicColor, onBookClicked)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        val flight = flights[position]
        holder.bind(flight)
    }

    override fun getItemCount(): Int = flights.size

    // The ViewHolder now accepts the callback as well
    class FlightViewHolder(
        private val binding: ItemFlightResultBinding,
        private val dynamicColor: Int,
        private val onBookClicked: (Flight) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(flight: Flight) {
            binding.airlineName.text = flight.airline
            binding.flightNumber.text = flight.flightNumber
            binding.fromLocation.text = flight.from
            binding.toLocation.text = flight.to
            binding.departureTime.text = flight.departureTime
            binding.arrivalTime.text = flight.arrivalTime
            binding.price.text = flight.price

            binding.root.setStrokeColor(ColorStateList.valueOf(dynamicColor))
            binding.price.setTextColor(dynamicColor)
            binding.bookChip.chipBackgroundColor = ColorStateList.valueOf(dynamicColor)


            // The click listener now just calls the callback function
            binding.bookChip.setOnClickListener {
                onBookClicked(flight)
            }
        }
    }
}