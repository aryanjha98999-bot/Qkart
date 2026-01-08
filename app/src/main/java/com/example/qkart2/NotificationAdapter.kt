package com.example.qkart2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qkart2.databinding.ItemNotificationBinding
import com.example.qkart2.model.NotificationModel

class NotificationAdapter(
    private val list: MutableList<NotificationModel>
) : RecyclerView.Adapter<NotificationAdapter.VH>() {

    class VH(val binding: ItemNotificationBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        holder.binding.tvTitle.text = item.title
        holder.binding.tvMessage.text = item.message
    }

    override fun getItemCount() = list.size
}