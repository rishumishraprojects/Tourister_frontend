package com.example.tourister.Fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tourister.MyTripsSection.*
import com.example.tourister.database.TripDatabase
import com.example.tourister.databinding.FragmentTripsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TripsFragment : Fragment() {

    private var _binding: FragmentTripsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TripAdapter
    private val themeColor by lazy { resources.getColor(com.google.android.material.R.color.design_default_color_primary) }

    private val viewModel: TripViewModel by viewModels {
        val db = TripDatabase.getDatabase(requireContext())
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return TripViewModel(db) as T
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TripAdapter(
            emptyList(),
            onTripClicked = ::openTripDetails,
            themeColor = themeColor,
            onTripLongClicked = { trip ->
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Delete Trip")
                    .setMessage("Are you sure you want to delete this trip?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteTrip(trip.id)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            })

        binding.tripsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tripsRecyclerView.adapter = adapter

        binding.addTripFab.backgroundTintList = ColorStateList.valueOf(themeColor)
        binding.addTripFab.setOnClickListener {
            AddTripDialogFragment().show(parentFragmentManager, AddTripDialogFragment.TAG)
        }

        // Listen for new trips from AddTripDialogFragment
        parentFragmentManager.setFragmentResultListener("add_trip_key", viewLifecycleOwner) { _, bundle ->
            val newTrip = bundle.getParcelable<Trip>("new_trip")
            if (newTrip != null) {
                viewModel.insertTrip(newTrip)
            }
        }

        observeTrips()
    }

    private fun observeTrips() {
        lifecycleScope.launch {
            viewModel.trips.collectLatest { tripsList ->
                adapter.updateTrips(tripsList)
            }
        }
    }

    private fun openTripDetails(trip: Trip) {
        val intent = TripDetailsActivity.getIntent(requireContext(), trip, themeColor)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
