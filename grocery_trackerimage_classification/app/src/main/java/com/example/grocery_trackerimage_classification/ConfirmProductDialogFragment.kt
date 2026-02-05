package com.example.grocery_trackerimage_classification.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.grocery_trackerimage_classification.R
import com.example.grocery_trackerimage_classification.StockViewModel
import kotlinx.coroutines.launch

class ConfirmProductDialogFragment(private val onDismiss: () -> Unit) : DialogFragment() {

    private val viewModel: StockViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_confirm_product_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName = view.findViewById<TextView>(R.id.tvProductName)
        val etQty = view.findViewById<EditText>(R.id.etQuantity)
        val btnConfirm = view.findViewById<Button>(R.id.btnConfirm)

        val product = arguments?.getString("product") ?: ""
        tvName.text = product

        btnConfirm.setOnClickListener {
            val qty = etQty.text.toString().toIntOrNull() ?: 1
            viewLifecycleOwner.lifecycleScope.launch {
                val txnId = viewModel.repository.getCurrentTxnId()
                // FIXED: Changed back to Pair
                viewModel.repository.addBatchStock(txnId, listOf(Pair(product, qty)))
                dismiss()
                onDismiss()
            }
        }
    }

    companion object {
        // FIXED: Removed confidence from newInstance
        fun newInstance(product: String, onDismiss: () -> Unit) =
            ConfirmProductDialogFragment(onDismiss).apply {
                arguments = Bundle().apply {
                    putString("product", product)
                }
            }
    }
}