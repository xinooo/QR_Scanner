package com.xinooo.qrcode.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xinooo.qrcode.data.SettingItem
import com.xinooo.qrcode.databinding.ItemSettingBinding

class SettingsAdapter(
    private var items: List<SettingItem>,
    private val onCheckChanged: (String, Boolean) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemSettingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSettingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvId.text = item.id
        holder.binding.tvNote.text = item.note
        
        // 移除監聽器避免重複觸發
        holder.binding.switchCheck.setOnCheckedChangeListener(null)
        holder.binding.switchCheck.isChecked = item.isCheck
        
        holder.binding.switchCheck.setOnCheckedChangeListener { _, isChecked ->
            item.isCheck = isChecked
            onCheckChanged(item.key, isChecked)
        }
        
        holder.binding.root.setOnClickListener {
            holder.binding.switchCheck.toggle()
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<SettingItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
