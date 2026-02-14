package com.example.forklore.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.forklore.R
import com.example.forklore.databinding.FragmentProfileBinding
import androidx.navigation.navGraphViewModels

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Get the shared ViewModel, scoped to the navigation graph.
    private val viewModel: ProfileViewModel by navGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    viewModel.logout()
                    findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                    true
                }
                else -> false
            }
        }

        binding.editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        binding.myPostsButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_myPostsFragment)
        }

        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.profileName.text = it.displayName
                binding.profileEmail.text = it.email
                it.photoUrl?.let { url ->
                    Glide.with(this).load(url).into(binding.profileImage)
                }
            }
        }

        viewModel.recipeCount.observe(viewLifecycleOwner) { count ->
            binding.postsCount.text = getString(R.string.recipe_count, count)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
