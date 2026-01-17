
package com.example.forklore.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.forklore.R
import com.example.forklore.databinding.FragmentProfileBinding
import com.example.forklore.ui.BaseAuthFragment
import com.example.forklore.utils.Resource

class ProfileFragment : BaseAuthFragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    private var imageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            Glide.with(this).load(it).into(binding.profileImage)
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

        viewModel.getUser()

        binding.profileImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.updateProfileButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            viewModel.updateProfile(name, imageUri)
        }

        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.nameEditText.setText(it.displayName)
                it.photoUrl?.let {
                    Glide.with(this).load(it).into(binding.profileImage)
                }
            }
        }

        viewModel.updateStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
