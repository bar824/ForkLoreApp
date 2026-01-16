
package com.example.forklore.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.forklore.databinding.FragmentPostDetailsBinding
import com.example.forklore.utils.Resource
import com.google.android.material.chip.Chip

class PostDetailsFragment : Fragment() {

    private var _binding: FragmentPostDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostDetailsViewModel by viewModels()
    private val args: PostDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPost(args.postId)

        viewModel.post.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Show progress
                }
                is Resource.Success -> {
                    resource.data?.let { post ->
                        binding.postTitle.text = post.title
                        binding.authorName.text = post.ownerName
                        binding.storyText.text = post.story
                        binding.ingredientsText.text = post.ingredients
                        binding.stepsText.text = post.steps
                        Glide.with(this).load(post.imageUrl).into(binding.postImage)
                        Glide.with(this).load(post.ownerPhotoUrl).into(binding.authorImage)
                        post.tags.forEach { tag ->
                            val chip = Chip(requireContext())
                            chip.text = tag
                            binding.tagsChipGroup.addView(chip)
                        }
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
