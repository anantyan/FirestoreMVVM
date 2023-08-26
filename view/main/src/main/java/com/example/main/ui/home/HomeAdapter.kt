package com.example.main.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.main.databinding.ListItemHomeBinding

class HomeAdapter : PagingDataAdapter<com.example.core.data.model.Note, RecyclerView.ViewHolder>(
    diffUtilCallback
) {
    private var _onClick: ((position: Int, id: String) -> Unit)? = null
    private var _onLongClick: ((position: Int, id: String) -> Unit)? = null

    inner class ViewHolder(private val binding: ListItemHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                _onClick?.let {
                    it(bindingAdapterPosition, getItem(bindingAdapterPosition)?.id ?: "")
                }
            }

            itemView.setOnLongClickListener {
                _onLongClick?.let {
                    it(bindingAdapterPosition, getItem(bindingAdapterPosition)?.id ?: "")
                }
                true
            }
        }

        fun bind(item: com.example.core.data.model.Note) {
            binding.apply {
                txtTitle.text = item.title
                txtContent.text = item.content
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        val item = getItem(position)
        item?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            ListItemHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun onClick(listener: (position: Int, id: String) -> Unit) {
        _onClick = listener
    }

    fun onLongClick(listener: (position: Int, id: String) -> Unit) {
        _onLongClick = listener
    }
}

val diffUtilCallback = object : DiffUtil.ItemCallback<com.example.core.data.model.Note>() {
    override fun areItemsTheSame(oldItem: com.example.core.data.model.Note, newItem: com.example.core.data.model.Note): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: com.example.core.data.model.Note, newItem: com.example.core.data.model.Note): Boolean {
        return oldItem == newItem
    }
}