
package com.example.forklore.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
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
    private lateinit var discoverAdapter: DiscoverAdapter

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

        setupRecyclerView()

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.searchRecipes(query)
                }
                true
            } else {
                false
            }
        }

        viewModel.recipes.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    discoverAdapter.submitList(resource.data)
                }
                is Resource.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        discoverAdapter = DiscoverAdapter {
            val action = DiscoverFragmentDirections.actionDiscoverFragmentToExternalDetailsFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.apply {
            adapter = discoverAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
