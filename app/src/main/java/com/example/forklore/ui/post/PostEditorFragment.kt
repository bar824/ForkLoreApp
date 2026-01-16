
package com.example.forklore.ui.post

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.forklore.data.model.Post
import com.example.forklore.databinding.FragmentPostEditorBinding
import com.example.forklore.utils.Resource
import com.google.android.material.chip.Chip

class PostEditorFragment : Fragment() {

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

        args.postId?.let {
            viewModel.getPost(it)
        }

        binding.selectImageButton.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.saveButton.setOnClickListener {
            savePost()
        }

        viewModel.post.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    currentPost = resource.data
                    populateFields()
                }
                is Resource.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.saveStatus.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), "Post saved", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is Resource.Error -> {
                    binding.progressIndicator.visibility = View.GONE
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
            it.tags.forEach { tag ->
                val chip = Chip(requireContext())
                chip.text = tag
                binding.tagsChipGroup.addView(chip)
            }
        }
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
