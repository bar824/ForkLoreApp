
package com.example.forklore.ui.myrecipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.forklore.R
import com.example.forklore.data.model.Post
import com.example.forklore.databinding.ItemPostWithOptionsBinding

class MyRecipesAdapter(
    private val onPostClicked: (Post) -> Unit,
    private val onEditClicked: (Post) -> Unit,
    private val onDeleteClicked: (Post) -> Unit
) : ListAdapter<Post, MyRecipesAdapter.MyPostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostViewHolder {
        val binding = ItemPostWithOptionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyPostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
        holder.itemView.setOnClickListener { onPostClicked(post) }
        holder.binding.optionsButton.setOnClickListener { showPopupMenu(it, post) }
    }

    private fun showPopupMenu(view: View, post: Post) {
        val popup = PopupMenu(view.context, view)
        popup.inflate(R.menu.post_options_menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> {
                    onEditClicked(post)
                    true
                }
                R.id.action_delete -> {
                    onDeleteClicked(post)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    inner class MyPostViewHolder(val binding: ItemPostWithOptionsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.postTitle.text = post.title
            binding.postAuthor.text = post.ownerName
            Glide.with(binding.root.context).load(post.imageUrl).into(binding.postImage)
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
