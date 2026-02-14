package com.example.forklore.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forklore.databinding.FragmentFeedBinding
import com.example.forklore.ui.BaseAuthFragment
import com.example.forklore.utils.Resource

class FeedFragment : BaseAuthFragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FeedViewModel by viewModels()
    private lateinit var feedAdapter: FeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()


        viewModel.posts.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    feedAdapter.submitList(resource.data)
                }
                is Resource.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        feedAdapter = FeedAdapter {
            val action = FeedFragmentDirections.actionFeedFragmentToPostDetailsFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.apply {
            adapter = feedAdapter
            val linearLayoutManager = LinearLayoutManager(requireContext())
            layoutManager = linearLayoutManager
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val visibleItemCount = linearLayoutManager.childCount
                    val totalItemCount = linearLayoutManager.itemCount
                    val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        viewModel.loadNextPage()
                    }
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
