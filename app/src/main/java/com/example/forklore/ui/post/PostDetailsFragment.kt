
package com.example.forklore.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.forklore.databinding.FragmentPostDetailsBinding
import com.example.forklore.ui.BaseAuthFragment
import com.example.forklore.utils.Resource
import com.google.android.material.chip.Chip
import java.io.File

class PostDetailsFragment : BaseAuthFragment() {

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
                    binding.progressIndicator.isVisible = true
                }
                is Resource.Success -> {
                    binding.progressIndicator.isVisible = false
                    resource.data?.let { post ->
                        binding.postTitle.text = post.title
                        binding.authorName.text = post.ownerName
                        binding.storyText.text = post.story
                        binding.ingredientsText.text = post.ingredients
                        binding.stepsText.text = post.steps

                        if (post.localImagePath != null) {
                            Glide.with(this).load(File(post.localImagePath!!)).into(binding.postImage)
                        } else {
                            Glide.with(this).load(post.imageUrl).into(binding.postImage)
                        }

                        Glide.with(this).load(post.ownerPhotoUrl).into(binding.authorImage)
                        post.tags.forEach { tag ->
                            val chip = Chip(requireContext())
                            chip.text = tag
                            binding.tagsChipGroup.addView(chip)
                        }
                    }
                }
                is Resource.Error -> {
                    binding.progressIndicator.isVisible = false
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
