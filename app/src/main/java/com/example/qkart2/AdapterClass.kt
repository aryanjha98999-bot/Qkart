package com.example.qkart2

import android.content.Context
import android.content.Intent
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdapterClass(
    private val dataList: ArrayList<DataCLass>,
    private val context: Context,
    private val scope: CoroutineScope
) : RecyclerView.Adapter<AdapterClass.ViewHolderClass>() {

    private val foodDao = foodDatabase.getDatabase(context).foodDao()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ViewHolderClass(view)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {

        val item = dataList[position]

        Glide.with(context)
            .load(item.dataimaage)
            .placeholder(R.drawable.m)
            .error(R.drawable.m)
            .into(holder.rvimage)

        holder.rvTitle.text = item.dataTitle
        holder.rvprice.text = item.dataprice

        scope.launch(Dispatchers.IO) {
            val exists = foodDao.countItemByName(item.dataTitle) > 0

            withContext(Dispatchers.Main) {
                holder.addtocart.text =
                    if (exists) "Remove" else "Add to Cart"
            }
        }

        holder.addtocart.setOnClickListener {

            scope.launch(Dispatchers.IO) {

                val exists = foodDao.countItemByName(item.dataTitle) > 0

                if (!exists) {
                    val cartItem = foodData(
                        itemname = item.dataTitle,
                        url = item.dataimaage,
                        itemprice = item.dataprice,
                        datacount = 1,
                        description = item.description,
                        ingredients = item.ingredients
                    )

                    foodDao.insert(cartItem)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                        holder.addtocart.text = "Remove"
                    }

                } else {

                    foodDao.deleteItemByName(item.dataTitle)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show()
                        holder.addtocart.text = "Add to Cart"
                    }
                }
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, detailsActivity2::class.java)
            intent.putExtra("menuname", item.dataTitle)
            intent.putExtra("menuimage", item.dataimaage)
            intent.putExtra("ingredients", item.ingredients)
            intent.putExtra("description", item.description)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = dataList.size

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvimage: ImageView = itemView.findViewById(R.id.imagefood)
        val rvTitle: TextView = itemView.findViewById(R.id.foodname)
        val rvprice: TextView = itemView.findViewById(R.id.price)
        val addtocart: TextView = itemView.findViewById(R.id.addtocart)
    }
}
