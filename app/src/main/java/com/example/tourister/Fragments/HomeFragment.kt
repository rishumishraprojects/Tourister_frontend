package com.example.tourister.Fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.tourister.AiPromptActivity
import com.example.tourister.Photo
import com.example.tourister.PhotoCarouselAdapter
import com.example.tourister.R
import com.example.tourister.SearchResultsActivity
import com.example.tourister.databinding.FragmentHomeBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone



class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var dynamicColor: Int = 0

    private val loadingDialog = LoadingDialogFragment()
    private val handler = Handler(Looper.getMainLooper())
    private var slideshowRunnable: Runnable? = null
    private var currentIndex = 0

    private val photos = listOf(
        "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=800",
        "https://images.unsplash.com/photo-1503264116251-35a269479413?w=800",
        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800",
        "https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=800",
        "https://images.unsplash.com/photo-1529156069898-49953e39b3ac?w=800",
        "https://images.unsplash.com/photo-1519985176271-adb1088fa94c?w=800",
        "https://images.unsplash.com/photo-1526772662000-3f88f10405ff?w=800",
        "https://images.unsplash.com/photo-1470770841072-f978cf4d019e?w=800",
        "https://images.unsplash.com/photo-1549880338-65ddcdfd017b?w=600",
        "https://fastly.picsum.photos/id/11/2500/1667.jpg?hmac=xxjFJtAPgshYkysU_aqx2sZir-kIOjNR9vx0te7GycQ",
        "https://fastly.picsum.photos/id/29/4000/2670.jpg?hmac=rCbRAl24FzrSzwlR5tL-Aqzyu5tX_PA95VJtnUXegGU",
    )

    companion object {
        fun newInstance(color: Int): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putInt("DYNAMIC_COLOR", color)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dynamicColor = it.getInt("DYNAMIC_COLOR")
        }
        hideStatusBar()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyThemeColor(dynamicColor)
        setupAutoCompleteTextViews()
        setupClickListeners()
        startSlideshow()
    }

    private fun startSlideshow() {
        if (photos.isEmpty()) return
        // Load the first image immediately
        Glide.with(requireContext()).load(photos[0]).into(binding.cardImageView)

        slideshowRunnable = object : Runnable {
            override fun run() {
                currentIndex = (currentIndex + 1) % photos.size

                val fadeOut = AlphaAnimation(1f, 0f)
                fadeOut.duration = 600
                val fadeIn = AlphaAnimation(0f, 1f)
                fadeIn.duration = 600

                fadeOut.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationEnd(animation: Animation?) {
                        Glide.with(requireContext())
                            .load(photos[currentIndex])
                            .into(binding.cardImageView)
                        binding.cardImageView.startAnimation(fadeIn)
                    }
                    override fun onAnimationRepeat(animation: Animation?) {}
                })

                binding.cardImageView.startAnimation(fadeOut)
                handler.postDelayed(this, 3000)
            }
        }

        handler.postDelayed(slideshowRunnable!!, 2000)
    }

    private fun setupAutoCompleteTextViews() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val jsonString = requireContext().assets.open("cities.json").bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(jsonString)
                val cityList = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    val cityObject = jsonArray.getJSONObject(i)
                    val cityName = cityObject.getString("name")
                    val country = cityObject.getString("country")
                    cityList.add("$cityName, $country")
                }

                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cityList)
                    binding.fromAutoCompleteText.setAdapter(adapter)
                    binding.toAutoCompleteText.setAdapter(adapter)
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error reading or parsing cities.json", e)
            }
        }
    }

    private fun setupClickListeners() {
        binding.departureDateEditText.setOnClickListener {
            showDatePicker("Select Departure Date", binding.departureDateEditText)
        }

        binding.swapButton.setOnClickListener {
            val fromString = binding.fromAutoCompleteText.text.toString()
            val toString = binding.toAutoCompleteText.text.toString()
            binding.fromAutoCompleteText.setText(toString)
            binding.toAutoCompleteText.setText(fromString)
        }

        binding.exploreButton.setOnClickListener {
            val fromLocation = binding.fromAutoCompleteText.text.toString()
            val toLocation = binding.toAutoCompleteText.text.toString()
            val departureDate = binding.departureDateEditText.text.toString()

            if (fromLocation.isNotBlank() && toLocation.isNotBlank() && departureDate.isNotBlank()) {
                loadingDialog.show(parentFragmentManager, LoadingDialogFragment.TAG)
                lifecycleScope.launch {
                    delay(1000)
                    val intent = Intent(requireContext(), SearchResultsActivity::class.java).apply {
                        putExtra("FROM_LOCATION", fromLocation)
                        putExtra("TO_LOCATION", toLocation)
                        putExtra("DEPARTURE_DATE", departureDate)
                        putExtra("IS_ONE_WAY", true)
                        putExtra("DYNAMIC_COLOR", dynamicColor)
                    }
                    startActivity(intent)
                    loadingDialog.dismiss()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill in all the required details.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.exploreDealsButton.setOnClickListener {
            val intent = Intent(requireContext(), AiPromptActivity::class.java).apply {
                putExtra("DYNAMIC_COLOR", dynamicColor)
            }
            startActivity(intent)
        }
    }

    private fun applyThemeColor(color: Int) {
        val colorStateList = ColorStateList.valueOf(color)
        binding.exploreButton.backgroundTintList = colorStateList
        binding.exploreDealsButton.backgroundTintList = colorStateList
        binding.swapButton.imageTintList = colorStateList
        binding.departureDateTextInputLayout.setStartIconTintList(colorStateList)
        binding.newChip.chipBackgroundColor = colorStateList
        binding.suggestionCard.setStrokeColor(colorStateList)
        binding.photoCard.setStrokeColor(colorStateList)

        val textFieldColorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_focused),
                intArrayOf()
            ),
            intArrayOf(
                color,
                ContextCompat.getColor(requireContext(), com.google.android.material.R.color.material_on_surface_stroke)
            )
        )
        binding.fromTextInputLayout.setBoxStrokeColorStateList(textFieldColorStateList)
        binding.toTextInputLayout.setBoxStrokeColorStateList(textFieldColorStateList)
        binding.departureDateTextInputLayout.setBoxStrokeColorStateList(textFieldColorStateList)
    }

    private fun showDatePicker(title: String, editText: TextInputEditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(title)
            .build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selection
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = format.format(calendar.time)
            editText.setText(formattedDate)
        }
        datePicker.show(parentFragmentManager, "DATE_PICKER_TAG")
    }

    override fun onPause() {
        super.onPause()
        slideshowRunnable?.let { handler.removeCallbacks(it) }
    }

    override fun onResume() {
        super.onResume()
        hideStatusBar()
        slideshowRunnable?.let { handler.postDelayed(it, 2000) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        slideshowRunnable?.let { handler.removeCallbacks(it) }
        _binding = null
    }

    fun hideStatusBar() {
        val activityWindow = requireActivity().window
        val controller = WindowInsetsControllerCompat(activityWindow, activityWindow.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars())
        // Optional: allow swipe-down to show temporarily
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }


}
