package com.example.forklore.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.forklore.R
import com.example.forklore.databinding.FragmentEditProfileBinding
import com.example.forklore.utils.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.navGraphViewModels

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    // Get the shared ViewModel, scoped to the navigation graph.
    private val viewModel: ProfileViewModel by navGraphViewModels(R.id.nav_graph)

    private var imageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            Glide.with(this).load(it).into(binding.ivProfileImage)
            viewModel.onImageChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
        setupTextWatchers()
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnChangeImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnSaveChanges.setOnClickListener { view ->
            view.hideKeyboard()
            view.post {
                val displayName = binding.etDisplayName.text.toString().trim()
                val bio = binding.etBio.text.toString().trim()
                viewModel.saveProfile(displayName, bio, imageUri)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.etDisplayName.setText(it.displayName)
                binding.etBio.setText(it.bio)
                it.photoUrl?.let { url ->
                    Glide.with(this).load(url).into(binding.ivProfileImage)
                }
            }
        }

        viewModel.isSaveEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.btnSaveChanges.isEnabled = isEnabled
        }

        viewModel.editProfileUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is EditProfileUiState.Error -> {
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                }
                else -> { /* Do nothing */ }
            }
        }

        viewModel.navigateToProfile.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { 
                Snackbar.make(binding.root, "Profile updated successfully", Snackbar.LENGTH_SHORT).show()
                // The automatic navigation is commented out, as you requested.
                // findNavController().navigateUp()
            }
        }
    }

    private fun setupTextWatchers() {
        binding.etDisplayName.addTextChangedListener {
            viewModel.onDisplayNameChanged(it.toString())
        }

        binding.etBio.addTextChangedListener {
            viewModel.onBioChanged(it.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
