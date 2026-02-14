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
import com.example.forklore.databinding.FragmentMyPostsBinding
import com.example.forklore.utils.Resource

class MyPostsFragment : Fragment() {

    private var _binding: FragmentMyPostsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyPostsViewModel by viewModels()
    private lateinit var myPostsAdapter: MyPostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.posts.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressIndicatorMyPosts.isVisible = true
                    binding.textViewEmptyStateMyPosts.isVisible = false
                }
                is Resource.Success -> {
                    binding.progressIndicatorMyPosts.isVisible = false
                    if (resource.data.isNullOrEmpty()) {
                        binding.textViewEmptyStateMyPosts.isVisible = true
                    } else {
                        myPostsAdapter.submitList(resource.data)
                    }
                }
                is Resource.Error -> {
                    binding.progressIndicatorMyPosts.isVisible = false
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        myPostsAdapter = MyPostsAdapter {
            val action = MyPostsFragmentDirections.actionMyPostsFragmentToPostDetailsFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerViewMyPosts.apply {
            adapter = myPostsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
