package com.example.tourister.MyTripsSection

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.tourister.R
import com.example.tourister.databinding.FragmentAddTripDialogBinding
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class AddTripDialogFragment : DialogFragment() {

    private var _binding: FragmentAddTripDialogBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    selectedImageUri = uri
                    Glide.with(this)
                        .load(selectedImageUri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_profile)
                        .into(binding.selectedImageView)
                }
            }
        }

    override fun onStart() {
        super.onStart()
        // Increase dialog width
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTripDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Dynamic theme color
        val themeColor = ContextCompat.getColor(requireContext(), com.google.android.material.R.color.design_default_color_primary)
        binding.selectImageButton.setTextColor(themeColor)
        binding.saveButton.setBackgroundColor(themeColor)

        // Open gallery
        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
            pickImageLauncher.launch(intent)
        }

        // Open date picker
        binding.datesEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    binding.datesEditText.setText(sdf.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Save trip
        binding.saveButton.setOnClickListener {
            val savedPath = selectedImageUri?.let { saveImageToInternalStorage(it) } ?: ""

            val trip = Trip(
                destination = binding.destinationEditText.text.toString(),
                dates = binding.datesEditText.text.toString(),
                imageUrl = savedPath,
                status = "Upcoming",
                type = "manual"
            )

            parentFragmentManager.setFragmentResult("add_trip_key", Bundle().apply {
                putParcelable("new_trip", trip)
            })

            dismiss()
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().filesDir, "trip_${System.currentTimeMillis()}.jpg")
            inputStream.use { input ->
                file.outputStream().use { output ->
                    input?.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddTripDialog"
    }
}
