package com.example.qkart2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qkart2.databinding.ItemNotificationBinding
import com.example.qkart2.model.NotificationModel

class NotificationAdapter(
    private val list: List<NotificationModel>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemNotificationBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.tvOrderName.text = "Order: ${item.orderName}"
        holder.binding.tvTitle.text = item.title
        holder.binding.tvMessage.text = item.message
    }

    override fun getItemCount() = list.size
}
