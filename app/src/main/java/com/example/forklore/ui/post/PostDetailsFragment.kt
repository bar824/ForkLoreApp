package com.example.forklore.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.forklore.databinding.FragmentPostDetailsBinding
import com.example.forklore.ui.BaseAuthFragment
import com.example.forklore.utils.Resource
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
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

        // Setup toolbar with back navigation
        setupToolbarNavigation(binding.toolbar)

        viewModel.getPost(args.postId)
        viewModel.observeLikes(args.postId)

        viewModel.post.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressIndicator.isVisible = true
                }
                is Resource.Success -> {
                    binding.progressIndicator.isVisible = false
                    resource.data?.let { post ->
                        binding.tagsChipGroup.removeAllViews()
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

                        val currentUser = FirebaseAuth.getInstance().currentUser
                        binding.likeToggleButton.setOnClickListener {
                            viewModel.toggleLike(post.id)
                        }

                        if (post.ownerId == currentUser?.uid) {
                            binding.fabEditPost.visibility = View.VISIBLE
                            binding.fabEditPost.setOnClickListener {
                                val action = PostDetailsFragmentDirections.actionPostDetailsFragmentToPostEditorFragment(post.id)
                                findNavController().navigate(action)
                            }
                        } else {
                            binding.fabEditPost.visibility = View.GONE
                            binding.likedByText.isVisible = false
                        }
                    }
                }
                is Resource.Error -> {
                    binding.progressIndicator.isVisible = false
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.likeState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val state = resource.data ?: return@observe
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    binding.likesCountText.text = "${state.likesCount} likes"
                    binding.likeToggleButton.text = if (state.isLikedByCurrentUser) "Unlike" else "Like"

                    val postOwnerId = (viewModel.post.value as? Resource.Success)?.data?.ownerId
                    val isOwner = postOwnerId == currentUser?.uid
                    if (isOwner && state.likedByNames.isNotEmpty()) {
                        binding.likedByText.isVisible = true
                        binding.likedByText.text = "Liked by: ${state.likedByNames.joinToString(", ")}"
                    } else {
                        binding.likedByText.isVisible = false
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> Unit
            }
        }

        viewModel.likeActionStatus.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Error) {
                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
