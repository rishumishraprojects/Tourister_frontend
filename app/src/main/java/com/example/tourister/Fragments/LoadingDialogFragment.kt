package com.example.tourister.Fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.tourister.databinding.FragmentLoadingDialogBinding

class LoadingDialogFragment : DialogFragment() {

    private var _binding: FragmentLoadingDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadingDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Make the dialog background transparent
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Prevent the user from dismissing it by tapping outside or pressing the back button
        isCancelable = false
    }

    companion object {
        // A tag for identifying the dialog
        const val TAG = "LoadingDialog"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}