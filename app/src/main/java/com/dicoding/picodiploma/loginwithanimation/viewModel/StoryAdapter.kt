package com.dicoding.picodiploma.loginwithanimation.viewModel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemEventBinding

class StoryAdapter(private val onItemClick: ((String?) -> Unit)? = null) :
    ListAdapter<ListStoryItem, StoryAdapter.StoryEventViewHolder>(DIFF_CALLBACK) {

    class StoryEventViewHolder(
        private val binding: ItemEventBinding,
        private val onItemClick: ((String?) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListStoryItem) {
            binding.tvItemName.text = event.name
            Glide.with(binding.root.context).load(event.photoUrl).into(binding.ivItemPhoto)

            binding.root.setOnClickListener{
                onItemClick?.invoke(event.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryEventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryEventViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: StoryEventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
