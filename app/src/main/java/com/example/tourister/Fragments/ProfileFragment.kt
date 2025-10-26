package com.example.tourister.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.tourister.ProfileSection.BadgeAdapter
import com.example.tourister.ProfileSection.ProfileViewModel
import com.example.tourister.database.TripDatabase
import com.example.tourister.databinding.FragmentProfileBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        val db = TripDatabase.getDatabase(requireContext())
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(db) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔹 RecyclerView for badges in 2 columns instead of horizontal scroll
        binding.badgeRecycler.layoutManager = GridLayoutManager(requireContext(), 2)

        // Observe trip count
        lifecycleScope.launch {
            viewModel.tripCount.collectLatest { count ->
                binding.tripCount.text = "Trips Saved: $count"
            }
        }

        // Observe badges
        lifecycleScope.launch {
            viewModel.badges.collectLatest { badges ->
                binding.badgeRecycler.adapter = BadgeAdapter(badges)
            }
        }

        // Observe user profile
        lifecycleScope.launch {
            viewModel.user.collectLatest { user ->
                if (user != null) {
                    // Show full name
                    binding.profileName.text = "${user.firstName} ${user.lastName}"

                    // Load image with Glide
                    if (!user.profileImagePath.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(user.profileImagePath)
                            .circleCrop()
                            .into(binding.profileImage)
                    } else {
                        binding.profileImage.setImageResource(com.example.tourister.R.drawable.ic_profile)
                    }
                }
            }
        }

        // ✅ Edit profile button inside onViewCreated
        binding.editProfileButton.setOnClickListener {
            EditProfileDialog { first, last, img ->
                viewModel.saveUser(first, last, img)
            }.show(parentFragmentManager, "editProfile")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
