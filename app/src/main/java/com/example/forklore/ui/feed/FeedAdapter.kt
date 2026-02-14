package com.example.forklore.ui.feed

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.forklore.data.model.Post
import com.example.forklore.databinding.ItemPostBinding
import com.google.android.material.chip.Chip
import java.io.File

class FeedAdapter(
    private val onPostClicked: (Post) -> Unit
) : ListAdapter<Post, FeedAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            // Title
            binding.postTitle.text = post.title.orEmpty()

            // Author
            binding.authorName.text = post.ownerName?.takeIf { it.isNotBlank() } ?: "Anonymous"

            // Story preview (if exists in Post model)
            binding.postStory.text = post.story?.takeIf { it.isNotBlank() } ?: ""

            // Time (if you have createdAt in Post)
            // If your Post.createdAt is Long:
            val createdAt = post.createdAt
            binding.postTime.text = if (createdAt > 0) {
                DateUtils.getRelativeTimeSpanString(createdAt).toString()
            } else {
                ""
            }

            // Image (local cache first)
            val localPath = post.localImagePath
            if (!localPath.isNullOrBlank()) {
                Glide.with(binding.root.context)
                    .load(File(localPath))
                    .into(binding.postImage)
            } else {
                Glide.with(binding.root.context)
                    .load(post.imageUrl)
                    .into(binding.postImage)
            }

            // Tags -> Chips
            binding.tagsGroup.removeAllViews()
            post.tags?.take(4)?.forEach { tag ->
                val chip = Chip(binding.root.context).apply {
                    text = "#$tag"
                    isClickable = false
                    isCheckable = false
                }
                binding.tagsGroup.addView(chip)
            }

            // Click
            binding.root.setOnClickListener { onPostClicked(post) }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
}
