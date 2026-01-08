package com.example.qkart2

import android.content.Context
import android.content.Intent
import android.util.Log
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
import com.example.qkart2.fragment.DataCLassMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchAdapter(
    private var dataList: ArrayList<DataCLassMenu>,
    private val context: Context,
    private val scope: CoroutineScope
) : RecyclerView.Adapter<SearchAdapter.ViewHolderClass>() {

    private val dao = foodDatabase.getDatabase(context).foodDao()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderClass {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_item, parent, false)
        return ViewHolderClass(view)
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val item = dataList[position]

        holder.rvTitle.text = item.name
        holder.rvprice.text = item.price

        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.m)
            .error(R.drawable.m)
            .fitCenter()
            .into(holder.rvimage)

        Log.d("IMAGE_PATH", item.imageUrl)

        holder.button1.setOnClickListener {

            if (holder.button1.text.toString() == "Add to cart") {

                scope.launch(Dispatchers.IO) {

                    val count = dao.countItemByName(item.name)

                    withContext(Dispatchers.Main) {

                        if (count > 0) {
                            Toast.makeText(
                                context,
                                "Item already added to cart",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {

                            val cartItem = foodData(
                                itemname = item.name,
                                url = item.imageUrl,
                                itemprice = item.price, datacount = 1,
                                description = item.description,
                                ingredients = item.ingredients
                            )

                            dao.insert(cartItem) // ✅ INSERT INTO ROOM

                            Toast.makeText(
                                context,
                                "Item added to cart",
                                Toast.LENGTH_SHORT
                            ).show()

                            holder.button1.text = "Added"
                            holder.button1.isEnabled = false
                        }
                    }
                }
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, DetailsActivity::class.java)
            intent.putExtra("menuname", item.name)
            intent.putExtra("menuprice", item.price)
            intent.putExtra("menuimage", item.imageUrl)
            intent.putExtra("menudescription", item.description)
            intent.putExtra("menuingredients", item.ingredients)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = dataList.size

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvimage: ImageView = itemView.findViewById(R.id.imagefood)
        val rvTitle: TextView = itemView.findViewById(R.id.foodname)
        val rvprice: TextView = itemView.findViewById(R.id.price)
        val button1: TextView = itemView.findViewById(R.id.addtocart)
    }
}
