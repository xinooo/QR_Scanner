package com.xinooo.qrcode.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xinooo.qrcode.data.QrCodeScanResult
import com.xinooo.qrcode.databinding.ItemScanHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScanHistoryAdapter : ListAdapter<QrCodeScanResult, ScanHistoryAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ItemScanHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QrCodeScanResult) {
            binding.item = item
            binding.tvTimestamp.text = formatTimestamp(item.timestamp)
            binding.executePendingBindings()
        }

        private fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScanHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object DiffCallback : DiffUtil.ItemCallback<QrCodeScanResult>() {
        override fun areItemsTheSame(oldItem: QrCodeScanResult, newItem: QrCodeScanResult): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: QrCodeScanResult, newItem: QrCodeScanResult): Boolean {
            return oldItem == newItem
        }
    }
}