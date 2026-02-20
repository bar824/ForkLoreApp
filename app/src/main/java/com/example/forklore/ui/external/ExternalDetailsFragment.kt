
package com.example.forklore.ui.external

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.forklore.databinding.FragmentExternalDetailsBinding
import com.example.forklore.ui.BaseAuthFragment
import com.example.forklore.utils.Resource

class ExternalDetailsFragment : BaseAuthFragment() {

    private var _binding: FragmentExternalDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExternalDetailsViewModel by viewModels()
    private val args: ExternalDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExternalDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup toolbar with back navigation
        setupToolbarNavigation(binding.toolbar)

        viewModel.getRecipeDetails(args.recipeId)

        viewModel.recipe.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Show progress
                }
                is Resource.Success -> {
                    resource.data?.let { recipe ->
                        binding.recipeName.text = recipe.name
                        binding.instructionsText.text = recipe.instructions
                        Glide.with(this).load(recipe.thumbnail).into(binding.recipeImage)
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.saveRecipeFab.setOnClickListener {
            viewModel.saveRecipe()
            Toast.makeText(requireContext(), "Recipe saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
