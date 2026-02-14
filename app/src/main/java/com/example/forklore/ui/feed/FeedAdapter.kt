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

            // Story preview
            binding.postStory.text = post.story?.takeIf { it.isNotBlank() } ?: ""

            // Time
            val createdAt = post.createdAt
            binding.postTime.text = if (createdAt > 0L) {
                DateUtils.getRelativeTimeSpanString(createdAt).toString()
            } else {
                ""
            }

            // Image (local cache first)
            val ctx = binding.root.context
            val localPath = post.localImagePath
            val remoteUrl = post.imageUrl

            val request = Glide.with(ctx)
                .load(
                    when {
                        !localPath.isNullOrBlank() -> File(localPath)
                        !remoteUrl.isNullOrBlank() -> remoteUrl
                        else -> null
                    }
                )
                // כדי שלא יהיה "מסך ריק" בזמן טעינה
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.ic_menu_close_clear_cancel)

            request.into(binding.postImage)

            // Tags -> Chips
            binding.tagsGroup.removeAllViews()
            val tags = post.tags.orEmpty().take(4)
            tags.forEach { tag ->
                val chip = Chip(ctx).apply {
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
