package com.example.forklore.ui.myrecipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.forklore.databinding.FragmentSavedPostsBinding
import com.example.forklore.utils.Resource

class SavedPostsFragment : Fragment() {

    private var _binding: FragmentSavedPostsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SavedPostsViewModel by viewModels()
    private lateinit var savedPostsAdapter: SavedPostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.posts.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressIndicatorSavedPosts.isVisible = true
                    binding.textViewEmptyStateSavedPosts.isVisible = false
                }
                is Resource.Success -> {
                    binding.progressIndicatorSavedPosts.isVisible = false
                    if (resource.data.isNullOrEmpty()) {
                        binding.textViewEmptyStateSavedPosts.isVisible = true
                    } else {
                        savedPostsAdapter.submitList(resource.data)
                    }
                }
                is Resource.Error -> {
                    binding.progressIndicatorSavedPosts.isVisible = false
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        savedPostsAdapter = SavedPostsAdapter(
            onPostClicked = {
                val action = SavedPostsFragmentDirections.actionSavedPostsFragmentToPostDetailsFragment(it.id)
                findNavController().navigate(action)
            },
            onRemoveClicked = {
                viewModel.unsavePost(it)
            }
        )
        binding.recyclerViewSavedPosts.apply {
            adapter = savedPostsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
