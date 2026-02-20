package com.example.forklore.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.forklore.R
import com.example.forklore.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Scoped to nav graph (keep if it works in your project)
    private val viewModel: ProfileViewModel by navGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Settings icon (right side)
        binding.settingsIcon.setOnClickListener {
            // If you have SettingsFragment action, replace this
            Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show()
        }

        // Edit profile
        binding.editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        // My recipes / my posts
        binding.myRecipesButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_myPostsFragment)
        }

        // RecyclerView (if you don’t have an adapter yet, it's OK to leave it empty)
        binding.recyclerViewProfilePosts.layoutManager = LinearLayoutManager(requireContext())
        // binding.recyclerViewProfilePosts.adapter = yourAdapter

        // User data
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user == null) return@observe

            binding.profileName.text =
                user.displayName ?: getString(R.string.profile_name_placeholder)

            // In your XML you don’t have profileEmail, so we show email in handle
            binding.profileHandle.text =
                user.email ?: getString(R.string.profile_handle_placeholder)

            val photoUrl = user.photoUrl
            if (photoUrl != null) {
                Glide.with(this).load(photoUrl).into(binding.profileImage)
            } else {
                binding.profileImage.setImageResource(android.R.drawable.sym_def_app_icon)
            }
        }

        // Posts count (recipes count)
        viewModel.recipeCount.observe(viewLifecycleOwner) { count ->
            binding.postsCount.text = (count ?: 0).toString()
        }

        // Since you DON'T have followers/following LiveData yet,
        // we just put placeholders (or 0)
        binding.followersCount.text = "0"
        binding.followingCount.text = "0"

        // Empty state default (until you connect posts list)
        binding.textViewEmptyState.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}