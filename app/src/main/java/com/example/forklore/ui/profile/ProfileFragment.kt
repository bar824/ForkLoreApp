package com.example.forklore.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
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

        binding.settingsIcon.setOnClickListener { showSettingsMenu() }

        binding.editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        binding.myRecipesButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_myPostsFragment)
        }

        binding.fabAddRecipe.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_postEditorFragment)
        }

        binding.recyclerViewProfilePosts.layoutManager = LinearLayoutManager(requireContext())

        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user == null) return@observe

            binding.profileName.text =
                user.displayName ?: getString(R.string.profile_name_placeholder)
            binding.profileHandle.text =
                user.email ?: getString(R.string.profile_handle_placeholder)
            binding.profileBio.text = user.bio?.ifBlank {
                getString(R.string.profile_bio_placeholder)
            } ?: getString(R.string.profile_bio_placeholder)

            val photoUrl = user.photoUrl
            if (photoUrl != null) {
                Glide.with(this).load(photoUrl).into(binding.profileImage)
            } else {
                binding.profileImage.setImageResource(android.R.drawable.sym_def_app_icon)
            }
        }

        viewModel.recipeCount.observe(viewLifecycleOwner) { count ->
            binding.postsCount.text = (count ?: 0).toString()
        }

        binding.textViewEmptyState.visibility = View.VISIBLE
    }

    private fun showSettingsMenu() {
        val popupMenu = PopupMenu(requireContext(), binding.settingsIcon)
        MenuInflater(requireContext()).inflate(R.menu.profile_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.logout) {
                viewModel.logout()
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                true
            } else {
                false
            }
        }
        popupMenu.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
