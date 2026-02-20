package com.example.forklore.ui.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.forklore.R
import com.example.forklore.databinding.FragmentRegisterBinding
import com.example.forklore.utils.Resource

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Register
        binding.btnCreateAccount.setOnClickListener {
            val name = binding.fullNameInput.text?.toString()?.trim().orEmpty()
            val email = binding.emailInput.text?.toString()?.trim().orEmpty()
            val password = binding.passwordInput.text?.toString()?.trim().orEmpty()

            viewModel.register(name, email, password)
        }

        // Go to login
        binding.loginLink.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        // Observe register state
        viewModel.registerStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressIndicator.isVisible = true
                    binding.btnCreateAccount.isEnabled = false
                    binding.loginLink.isEnabled = false
                }

                is Resource.Success -> {
                    binding.progressIndicator.isVisible = false
                    binding.btnCreateAccount.isEnabled = true
                    binding.loginLink.isEnabled = true
                    findNavController().navigate(R.id.action_registerFragment_to_feedFragment)
                }

                is Resource.Error -> {
                    binding.progressIndicator.isVisible = false
                    binding.btnCreateAccount.isEnabled = true
                    binding.loginLink.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        resource.message ?: "Registration failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}