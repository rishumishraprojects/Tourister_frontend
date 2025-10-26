package com.example.tourister.MyTripsSection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tourister.R
import com.example.tourister.databinding.ItemTripBinding

class TripAdapter(
    private var trips: List<Trip>,
    private val onTripClicked: (Trip) -> Unit,
    private val themeColor: Int,
    private val onTripLongClicked: ((Trip) -> Unit)? = null
) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding, onTripClicked, themeColor, onTripLongClicked)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(trips[position])
    }

    override fun getItemCount(): Int = trips.size

    fun updateTrips(newTrips: List<Trip>) {
        trips = newTrips
        notifyDataSetChanged()
    }



    class TripViewHolder(
        private val binding: ItemTripBinding,
        private val onTripClicked: (Trip) -> Unit,
        private val themeColor: Int,
        private val onTripLongClicked: ((Trip) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: Trip) {
            Glide.with(binding.root.context)
                .load(trip.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_profile)
                .into(binding.tripImage)

            binding.tripDestination.text = trip.destination
            binding.tripDates.text = trip.dates
            binding.tripStatus.text = trip.status
            binding.cardView.setStrokeColor(themeColor)

            val statusColor = if (trip.status == "Upcoming") binding.root.context.getColor(android.R.color.holo_red_dark) else binding.root.context.getColor(android.R.color.holo_green_dark)
            binding.tripStatus.setTextColor(statusColor)

            binding.root.setOnClickListener { onTripClicked(trip) }
            binding.root.setOnLongClickListener {
                onTripLongClicked?.invoke(trip)
                true  // indicates the long click was handled
            }
        }
    }
}
