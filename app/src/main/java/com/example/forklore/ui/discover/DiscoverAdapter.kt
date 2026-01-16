
package com.example.forklore.ui.discover

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.forklore.data.model.ExternalRecipe
import com.example.forklore.databinding.ItemExternalRecipeBinding

class DiscoverAdapter(private val onRecipeClicked: (ExternalRecipe) -> Unit) : ListAdapter<ExternalRecipe, DiscoverAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemExternalRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = getItem(position)
        holder.bind(recipe)
        holder.itemView.setOnClickListener { onRecipeClicked(recipe) }
    }

    inner class RecipeViewHolder(private val binding: ItemExternalRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: ExternalRecipe) {
            binding.recipeName.text = recipe.name
            Glide.with(binding.root.context).load(recipe.thumbnail).into(binding.recipeImage)
        }
    }
}

class RecipeDiffCallback : DiffUtil.ItemCallback<ExternalRecipe>() {
    override fun areItemsTheSame(oldItem: ExternalRecipe, newItem: ExternalRecipe): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ExternalRecipe, newItem: ExternalRecipe): Boolean {
        return oldItem == newItem
    }
}
