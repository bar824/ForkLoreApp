
package com.example.forklore.ui.post

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.forklore.data.model.Post
import com.example.forklore.databinding.FragmentPostEditorBinding
import com.example.forklore.ui.BaseAuthFragment
import com.example.forklore.utils.Resource
import com.google.android.material.chip.Chip

class PostEditorFragment : BaseAuthFragment() {

    private var _binding: FragmentPostEditorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostEditorViewModel by viewModels()
    private val args: PostEditorFragmentArgs by navArgs()

    private var imageUri: Uri? = null
    private var currentPost: Post? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            Glide.with(this).load(it).into(binding.postImage)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup toolbar with back navigation
        setupToolbarNavigation(binding.toolbar)

        args.postId?.let {
            viewModel.getPost(it)
        }

        binding.selectImageButton.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.addTagButton.setOnClickListener {
            addTag()
        }

        binding.saveButton.setOnClickListener {
            if (validateInput()) {
                savePost()
            }
        }

        viewModel.post.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressIndicator.isVisible = true
                }
                is Resource.Success -> {
                    binding.progressIndicator.isVisible = false
                    currentPost = resource.data
                    populateFields()
                }
                is Resource.Error -> {
                    binding.progressIndicator.isVisible = false
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.saveStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressIndicator.isVisible = true
                }
                is Resource.Success -> {
                    binding.progressIndicator.isVisible = false
                    Toast.makeText(requireContext(), "Post saved", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is Resource.Error -> {
                    binding.progressIndicator.isVisible = false
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun populateFields() {
        currentPost?.let {
            binding.titleEditText.setText(it.title)
            binding.storyEditText.setText(it.story)
            binding.ingredientsEditText.setText(it.ingredients)
            binding.stepsEditText.setText(it.steps)
            it.imageUrl?.let {
                Glide.with(this).load(it).into(binding.postImage)
            }
            binding.tagsChipGroup.removeAllViews()
            it.tags.forEach { tag ->
                addChipToGroup(tag)
            }
        }
    }

    private fun addTag() {
        val tagText = binding.tagEditText.text.toString().trim()
        if (tagText.isNotBlank()) {
            addChipToGroup(tagText)
            binding.tagEditText.text?.clear()
        }
    }

    private fun addChipToGroup(tag: String) {
        val chip = Chip(requireContext()).apply {
            text = tag
            isCloseIconVisible = true
            setOnCloseIconClickListener { binding.tagsChipGroup.removeView(it) }
        }
        binding.tagsChipGroup.addView(chip)
    }

    private fun validateInput(): Boolean {
        var isValid = true

        if (binding.titleEditText.text.toString().trim().isEmpty()) {
            binding.titleLayout.error = "Title is required"
            isValid = false
        } else {
            binding.titleLayout.error = null
        }

        if (binding.storyEditText.text.toString().trim().isEmpty()) {
            binding.storyLayout.error = "Story is required"
            isValid = false
        } else {
            binding.storyLayout.error = null
        }

        if (binding.ingredientsEditText.text.toString().trim().isEmpty()) {
            binding.ingredientsLayout.error = "Ingredients are required"
            isValid = false
        } else {
            binding.ingredientsLayout.error = null
        }

        if (binding.stepsEditText.text.toString().trim().isEmpty()) {
            binding.stepsLayout.error = "Steps are required"
            isValid = false
        } else {
            binding.stepsLayout.error = null
        }

        return isValid
    }

    private fun savePost() {
        val title = binding.titleEditText.text.toString().trim()
        val story = binding.storyEditText.text.toString().trim()
        val ingredients = binding.ingredientsEditText.text.toString().trim()
        val steps = binding.stepsEditText.text.toString().trim()
        val tags = binding.tagsChipGroup.children.map { (it as Chip).text.toString() }.toList()

        val post = currentPost?.copy(
            title = title,
            story = story,
            ingredients = ingredients,
            steps = steps,
            tags = tags
        ) ?: Post(
            title = title,
            story = story,
            ingredients = ingredients,
            steps = steps,
            tags = tags
        )

        viewModel.savePost(post, imageUri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
