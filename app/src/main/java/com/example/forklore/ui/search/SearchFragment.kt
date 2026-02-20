package com.example.forklore.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.forklore.R
import com.example.forklore.databinding.FragmentSearchBinding
import com.example.forklore.ui.BaseAuthFragment

class SearchFragment : BaseAuthFragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.searchToolbar.setNavigationOnClickListener {
            findNavController().navigate(
                R.id.feedFragment,
                null,
                navOptions {
                    launchSingleTop = true
                    popUpTo(R.id.feedFragment) { inclusive = false }
                }
            )
        }

        binding.searchEditText.addTextChangedListener {
            binding.searchButton.visibility = if (it.isNullOrBlank()) View.GONE else View.VISIBLE
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchEditText.text.toString()
                viewModel.search(query)
                return@setOnEditorActionListener true
            }
            false
        }

        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            viewModel.search(query)
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            if (results.isEmpty()) {
                binding.emptySearchTextView.visibility = View.VISIBLE
                binding.searchResultsRecyclerView.visibility = View.GONE
            } else {
                binding.emptySearchTextView.visibility = View.GONE
                binding.searchResultsRecyclerView.visibility = View.VISIBLE
                searchAdapter.submitList(results)
            }
        }
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchAdapter { post ->
            val action = SearchFragmentDirections.actionSearchFragmentToPostDetailsFragment(post.id)
            findNavController().navigate(action)
        }
        binding.searchResultsRecyclerView.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
