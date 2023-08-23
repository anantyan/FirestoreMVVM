package com.anantyan.firestoremvvm.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anantyan.firestoremvvm.databinding.ListItemLoadStateBinding

class HomeLoadStateAdapter(
    private val onClick: () -> Unit
) : LoadStateAdapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(private val binding: ListItemLoadStateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            binding.apply {
                progressBar.isVisible = loadState is LoadState.Loading
                txtRetry.isVisible = loadState is LoadState.Error
                btnRetry.isVisible = loadState is LoadState.Error

                txtRetry.text = (loadState as? LoadState.Error)?.error.toString()

                btnRetry.setOnClickListener {
                    onClick.invoke()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, loadState: LoadState) {
        holder as ViewHolder
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): RecyclerView.ViewHolder {
        return ViewHolder(
            ListItemLoadStateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}