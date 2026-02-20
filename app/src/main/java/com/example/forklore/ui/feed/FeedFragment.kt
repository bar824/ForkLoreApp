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
import com.example.forklore.data.model.User
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

        // The adapter is now set up inside the user observer

        binding.fabAddPost.setOnClickListener {
            val action = FeedFragmentDirections.actionFeedFragmentToPostEditorFragment(null)
            findNavController().navigate(action)
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            // Setup RecyclerView and Adapter once we have the user data
            if (!::feedAdapter.isInitialized) {
                setupRecyclerView(user)
            }
            // When user data changes (e.g., a post is saved), resubmit the list to update the icons
            feedAdapter.submitList(viewModel.posts.value?.data)
        }

        viewModel.posts.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    // Check if adapter is initialized before submitting
                    if (::feedAdapter.isInitialized) {
                        feedAdapter.submitList(resource.data)
                    }
                }
                is Resource.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRecyclerView(currentUser: User?) {
        feedAdapter = FeedAdapter(
            onPostClicked = {
                val action = FeedFragmentDirections.actionFeedFragmentToPostDetailsFragment(it.id)
                findNavController().navigate(action)
            },
            onSaveClicked = {
                viewModel.toggleSaveStatus(it)
            },
            currentUser = currentUser
        )
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
