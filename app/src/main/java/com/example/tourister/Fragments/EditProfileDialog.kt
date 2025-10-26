package com.example.tourister.Fragments

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.tourister.databinding.DialogEditProfileBinding
import java.io.File
import java.io.FileOutputStream

class EditProfileDialog(
    private val onSave: (String, String, String?) -> Unit
) : DialogFragment() {

    private var selectedImagePath: String? = null
    private var _binding: DialogEditProfileBinding? = null
    private val binding get() = _binding!!

    // Gallery picker launcher
    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                uri?.let {
                    val file = saveImageToInternalStorage(it)
                    selectedImagePath = file.absolutePath
                    Glide.with(this)
                        .load(file)
                        .circleCrop()
                        .into(binding.profileImage)
                }
            }
        }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEditProfileBinding.inflate(LayoutInflater.from(context))

        // open gallery
        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePicker.launch(intent)
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Edit Profile")
            .setView(binding.root)
            .setPositiveButton("Save") { _, _ ->
                val firstName = binding.firstNameInput.text.toString().trim()
                val lastName = binding.lastNameInput.text.toString().trim()
                onSave(firstName, lastName, selectedImagePath)
            }
            .setNegativeButton("Cancel", null)
            .create()
            .apply {
                setOnShowListener {
                    window?.setLayout(
                        (resources.displayMetrics.widthPixels * 0.9).toInt(),
                        window!!.attributes.height
                    )
                }
            }
    }

    private fun saveImageToInternalStorage(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().filesDir, "profile_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
