package com.example.grocery_trackerimage_classification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery_trackerimage_classification.databinding.ItemStockBinding

class StockAdapter : ListAdapter<StockEntity, StockAdapter.StockViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val binding = ItemStockBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class StockViewHolder(private val binding: ItemStockBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StockEntity) {
            binding.tvProduct.text = item.productName
            binding.tvQuantity.text = "Quantity: ${item.quantity}"

            // We only show the time and product info now
            binding.tvTime.text = item.timestamp

            // Note: Ensure your item_stock.xml still has these IDs.
            // If you removed tvConfidence from XML, make sure to delete any reference to it here.
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<StockEntity>() {
        override fun areItemsTheSame(old: StockEntity, new: StockEntity): Boolean {
            // Compare the unique MongoDB ID
            return old.id == new.id
        }

        override fun areContentsTheSame(old: StockEntity, new: StockEntity): Boolean {
            // Compare the entire object
            return old == new
        }
    }
}