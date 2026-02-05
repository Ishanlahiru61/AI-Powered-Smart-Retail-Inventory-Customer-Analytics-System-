package com.example.grocery_trackerimage_classification

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocery_trackerimage_classification.databinding.FragmentSavedItemsBinding
import kotlinx.coroutines.launch

class SavedItemsFragment : Fragment(R.layout.fragment_saved_items) {

    private lateinit var binding: FragmentSavedItemsBinding
    private val stockViewModel: StockViewModel by activityViewModels()
    private lateinit var adapter: StockAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSavedItemsBinding.bind(view)

        adapter = StockAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SavedItemsFragment.adapter
        }

        // Fetch data from MongoDB
        viewLifecycleOwner.lifecycleScope.launch {
            val stockList = stockViewModel.fetchAllStock()
            if (stockList.isNotEmpty()) {
                adapter.submitList(stockList)
            } else {
                Toast.makeText(context, "No items found in database", Toast.LENGTH_SHORT).show()
            }
        }
    }
}