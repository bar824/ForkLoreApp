
package com.example.forklore.ui.myrecipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.forklore.databinding.FragmentMyRecipesBinding
import com.example.forklore.utils.Resource

class MyRecipesFragment : Fragment() {

    private var _binding: FragmentMyRecipesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyRecipesViewModel by viewModels()
    private lateinit var myRecipesAdapter: MyRecipesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.fabAddRecipe.setOnClickListener {
            findNavController().navigate(MyRecipesFragmentDirections.actionMyRecipesFragmentToPostEditorFragment())
        }

        viewModel.myPosts.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    myRecipesAdapter.submitList(resource.data)
                }
                is Resource.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.deleteStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), "Post deleted", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.getMyPosts()
    }

    private fun setupRecyclerView() {
        myRecipesAdapter = MyRecipesAdapter(
            onPostClicked = {
                val action = MyRecipesFragmentDirections.actionMyRecipesFragmentToPostDetailsFragment(it.id)
                findNavController().navigate(action)
            },
            onEditClicked = {
                val action = MyRecipesFragmentDirections.actionMyRecipesFragmentToPostEditorFragment(it.id)
                findNavController().navigate(action)
            },
            onDeleteClicked = { viewModel.deletePost(it) }
        )
        binding.recyclerView.apply {
            adapter = myRecipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
