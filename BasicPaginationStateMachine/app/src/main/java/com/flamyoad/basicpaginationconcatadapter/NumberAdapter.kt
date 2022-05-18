package com.flamyoad.basicpaginationconcatadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.basicpaginationconcatadapter.databinding.BasicListItemBinding
import com.flamyoad.basicpaginationconcatadapter.model.NumberPojo


class NumberAdapter(
    private val listener: PaginationListener,
) : ListAdapter<NumberPojo, NumberAdapter.NumberViewHolder>(COMPARATOR) {

    interface PaginationListener {
        fun fetchNextPage()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
        val binding = BasicListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NumberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        if (position == RecyclerView.NO_POSITION) {
            return
        }

        holder.bind(getItem(position))
        if (itemCount - position <= 5) {
            listener.fetchNextPage()
        }
    }

    inner class NumberViewHolder(private val binding: BasicListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(number: NumberPojo) {
            binding.txtNumber.text = number.value
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<NumberPojo>() {
            override fun areItemsTheSame(oldItem: NumberPojo, newItem: NumberPojo): Boolean {
                return oldItem.value == newItem.value
            }

            override fun areContentsTheSame(oldItem: NumberPojo, newItem: NumberPojo): Boolean {
                return oldItem == newItem
            }
        }
    }
}