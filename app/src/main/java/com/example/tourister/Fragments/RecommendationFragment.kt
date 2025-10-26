package com.example.tourister.Fragments

import android.R
import android.content.res.ColorStateList
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.tourister.ApiRequest.RetrofitInstance
import com.example.tourister.RecommendationSection.Recommendation
import com.example.tourister.databinding.FragmentRecommendationBinding
import kotlinx.coroutines.launch
import android.util.Log
import androidx.core.content.ContextCompat

class RecommendationFragment : Fragment() {

    private var _binding: FragmentRecommendationBinding? = null
    private val binding get() = _binding!!
    private var dynamicColor: Int = 0
    private val homeFragment = HomeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dynamicColor = it.getInt("DYNAMIC_COLOR")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        _binding = FragmentRecommendationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyThemeColor(dynamicColor)
        binding.placeName.paintFlags = binding.placeName.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        fetchRecommendationRecommendation()
    }

    private fun fetchRecommendationRecommendation() {
        lifecycleScope.launch {
            try {
                val Recommendation: Recommendation = RetrofitInstance.api.getTrendingRecommendation()

                // Load data into UI
                binding.placeName.text = Recommendation.placeName
                binding.topPickReason.text = Recommendation.reason ?: "No reason available"
                binding.placeImage.load(Recommendation.imageUrl) {
                    crossfade(true)
                    placeholder(android.R.drawable.ic_menu_report_image)
                    error(android.R.drawable.ic_menu_report_image)
                }

            } catch (e: Exception) {
                Log.e("RecommendationFragment", "Error fetching Recommendation", e)
                Toast.makeText(requireContext(), "Failed to load Recommendation recommendation", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        // Factory method to create a new instance and pass the color
        fun newInstance(color: Int): RecommendationFragment {
            val fragment = RecommendationFragment()
            val args = Bundle()
            args.putInt("DYNAMIC_COLOR", color)
            fragment.arguments = args
            return fragment
        }
    }
    // New function to apply the dynamic theme color
    private fun applyThemeColor(color: Int) {
        val colorStateList = ColorStateList.valueOf(color)
        binding.cardView.setStrokeColor(colorStateList)
    }
}
