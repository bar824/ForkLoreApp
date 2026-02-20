package com.example.forklore.ui.feed

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.forklore.R
import com.example.forklore.data.model.Post
import com.example.forklore.data.model.User
import com.example.forklore.databinding.ItemPostBinding
import com.google.android.material.chip.Chip
import java.io.File

class FeedAdapter(
    private val onPostClicked: (Post) -> Unit,
    private val onSaveClicked: (String) -> Unit,
    private val onLikeClicked: (String) -> Unit
) : ListAdapter<Post, FeedAdapter.PostViewHolder>(PostDiffCallback()) {

    private var currentUser: User? = null

    fun updateCurrentUser(user: User?) {
        currentUser = user
        notifyDataSetChanged()
    }

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
            binding.postTitle.text = post.title
            binding.authorName.text = post.ownerName.ifBlank { "Anonymous" }
            binding.postStory.text = post.story

            val createdAt = post.createdAt
            binding.postTime.text = if (createdAt > 0) {
                DateUtils.getRelativeTimeSpanString(createdAt).toString()
            } else {
                ""
            }

            val localPath = post.localImagePath
            if (!localPath.isNullOrBlank()) {
                Glide.with(binding.root.context).load(File(localPath)).into(binding.postImage)
            } else {
                Glide.with(binding.root.context).load(post.imageUrl).into(binding.postImage)
            }

            binding.tagsGroup.removeAllViews()
            post.tags.take(4).forEach { tag ->
                val chip = Chip(binding.root.context).apply {
                    text = "#$tag"
                    isClickable = false
                    isCheckable = false
                }
                binding.tagsGroup.addView(chip)
            }

            val user = currentUser
            val isSaved = user?.savedPosts?.contains(post.id) == true
            val saveIcon = if (isSaved) R.drawable.ic_bookmark else R.drawable.ic_bookmark_border
            binding.savePostButton.setImageResource(saveIcon)

            val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
            val userLiked = currentUserId?.let { post.likedBy.containsKey(it) } == true
            val likeColor = if (userLiked) R.color.fl_coral else android.R.color.darker_gray
            binding.likePostButton.setColorFilter(ContextCompat.getColor(binding.root.context, likeColor))
            binding.likesCount.text = post.likesCount.toString()

            binding.savePostButton.setOnClickListener { onSaveClicked(post.id) }
            binding.likePostButton.setOnClickListener { onLikeClicked(post.id) }
            binding.root.setOnClickListener { onPostClicked(post) }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
}
