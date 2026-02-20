package com.example.forklore.ui.myrecipes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.forklore.data.model.Post
import com.example.forklore.databinding.ItemPostBinding

class SavedPostsAdapter(
    private val onPostClicked: (Post) -> Unit,
    private val onRemoveClicked: (String) -> Unit
) : ListAdapter<Post, SavedPostsAdapter.SavedPostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedPostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SavedPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedPostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
        holder.itemView.setOnClickListener { onPostClicked(post) }
    }

    inner class SavedPostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.postTitle.text = post.title
            binding.authorName.text = post.ownerName
            binding.postStory.text = post.story
            Glide.with(binding.root.context).load(post.imageUrl).into(binding.postImage)

            binding.savePostButton.setImageResource(com.example.forklore.R.drawable.ic_bookmark)
            binding.savePostButton.setOnClickListener {
                onRemoveClicked(post.id)
            }
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}
