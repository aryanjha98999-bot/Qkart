package com.example.qkart2
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.qkart2.roomdb.foodDatabase
import com.example.qkart2.fragment.dataclasscart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartAdapter(
    private var dataList: MutableList<dataclasscart>,
    private val scope: CoroutineScope
) : RecyclerView.Adapter<CartAdapter.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]

        Glide.with(holder.itemView.context)
            .load(currentItem.url)
            .placeholder(R.drawable.m)
            .error(R.drawable.m)
            .fitCenter()
            .into(holder.rvImage)

        holder.rvTitle.text = currentItem.dataTitle
        holder.rvPrice.text = currentItem.dataprice
        holder.rvcount.text = currentItem.datacount.toString()

        holder.addbutton.setOnClickListener {

            val item = dataList[position]
            if (item.datacount < 10) {
                item.datacount++
                holder.rvcount.text = item.datacount.toString()

                scope.launch(Dispatchers.IO) {
                    foodDatabase.getDatabase(holder.itemView.context)
                        .foodDao()
                        .updateItemCount(item.dataTitle, item.datacount)
                }
            }
        }

        holder.minusbutton.setOnClickListener {
            val item = dataList[position]
            if (item.datacount > 1) {
                item.datacount--
                holder.rvcount.text = item.datacount.toString()

                scope.launch(Dispatchers.IO) {
                    foodDatabase.getDatabase(holder.itemView.context)
                        .foodDao()
                        .updateItemCount(item.dataTitle, item.datacount)
                }
            }
        }

        holder.deletebutton.setOnClickListener {
            val item = dataList[position]

            scope.launch(Dispatchers.IO) {
                foodDatabase.getDatabase(holder.itemView.context)
                    .foodDao()
                    .deleteItemByName(item.dataTitle)
            }

            dataList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount() = dataList.size
    fun updateList(newList: List<dataclasscart>) {
        dataList.clear()
        dataList.addAll(newList)
        notifyDataSetChanged()
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvImage: ImageView = itemView.findViewById(R.id.imageView4)
        val rvTitle: TextView = itemView.findViewById(R.id.foodnameid)
        val rvPrice: TextView = itemView.findViewById(R.id.priceid)
        val rvcount: TextView = itemView.findViewById(R.id.datacountid)
        val minusbutton: ImageButton = itemView.findViewById(R.id.minus)
        val addbutton: ImageButton = itemView.findViewById(R.id.add)
        val deletebutton: ImageButton = itemView.findViewById(R.id.trash)
    }
}
