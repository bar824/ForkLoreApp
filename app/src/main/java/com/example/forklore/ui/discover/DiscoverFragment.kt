package com.example.forklore.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.forklore.databinding.FragmentDiscoverBinding
import com.example.forklore.ui.BaseAuthFragment
import com.example.forklore.utils.Resource

class DiscoverFragment : BaseAuthFragment() {

    private var _binding: FragmentDiscoverBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiscoverViewModel by viewModels()
    private lateinit var adapter: DiscoverAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DiscoverAdapter { recipe ->
            val action =
                DiscoverFragmentDirections.actionDiscoverFragmentToExternalDetailsFragment(recipe.id)
            findNavController().navigate(action)
        }

        // Recycler (matches: @+id/recycler_view_articles)
        binding.recyclerViewArticles.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewArticles.adapter = adapter

        binding.refreshIcon.setOnClickListener {
            // TODO: hook refresh action to ViewModel (e.g., viewModel.searchRecipes(...) or viewModel.loadCurated())
            Toast.makeText(requireContext(), "Refresh clicked", Toast.LENGTH_SHORT).show()
        }

        viewModel.recipes.observe(viewLifecycleOwner) { res ->
            binding.progressIndicator.isVisible = res is Resource.Loading

            when (res) {
                is Resource.Success -> {
                    val list = res.data.orEmpty()
                    binding.emptyStateText.isVisible = list.isEmpty()
                    adapter.submitList(list)
                }

                is Resource.Error -> {
                    binding.emptyStateText.isVisible = true
                    adapter.submitList(emptyList())
                    Toast.makeText(
                        requireContext(),
                        res.message ?: "Failed to load results",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Resource.Loading -> Unit
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}