package com.example.qkart2

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.qkart2.roomdb.foodData
import com.example.qkart2.roomdb.foodDatabase
import com.example.qkart2.roomdb.historyfoodDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryAdapter(
    private val context: Context,
    private var dataList: ArrayList<DataCLasshistory>
) : RecyclerView.Adapter<HistoryAdapter.ViewHolderClass>() {

    private val dao = foodDatabase.getDatabase(context).foodDao()
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.historyitem, parent, false)
        return ViewHolderClass(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val item = dataList[position]

        Glide.with(context)
            .load(item.url)
            .into(holder.rvImage)

        holder.rvTitle.text = item.dataTitle
        holder.rvPrice.text = item.dataprice
        holder.button.text = "Buy Again"

        holder.button.setOnClickListener {

            scope.launch(Dispatchers.IO) {

                val count = dao.countItemByName(item.dataTitle)

                withContext(Dispatchers.Main) {

                    if (count > 0) {
                        Toast.makeText(
                            context,
                            "Item already in cart",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        val cartItem = foodData(
                            itemname = item.dataTitle,
                            url = item.url,
                            itemprice = item.dataprice,
                            datacount = 1,
                            description = item.description,
                            ingredients = item.ingredients,
                            Restaurant_name = item.Restaurant_name,
                            canteenid = item.canteenid
                        )

                        dao.insert(cartItem)

                        Toast.makeText(
                            context,
                            "Item added to cart",
                            Toast.LENGTH_SHORT
                        ).show()

                        holder.button.text = "Added"
                        holder.button.isEnabled = false
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvImage: ImageView = itemView.findViewById(R.id.imageView6)
        val rvTitle: TextView = itemView.findViewById(R.id.foodname)
        val rvPrice: TextView = itemView.findViewById(R.id.price)
        val button: TextView = itemView.findViewById(R.id.button)
    }
}
