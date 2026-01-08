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

class CartAdapter (private var dataList: ArrayList<dataclasscart>) :
    RecyclerView.Adapter<CartAdapter.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
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
        holder.rvPrice.text = currentItem.dataprice.toString()
        holder.rvcount.text=currentItem.datacount.toString()

        holder.addbutton.setOnClickListener {
            if (dataList[position].datacount < 10) {
                dataList[position].datacount++
                holder.rvcount.text = currentItem.datacount.toString()

                Thread {
                    foodDatabase.getDatabase(holder.itemView.context)
                        .foodDao()
                        .updateItemCount(
                            currentItem.dataTitle,
                            currentItem.datacount
                        )
                }.start()

            }
        }


        holder.minusbutton.setOnClickListener {
                if (dataList[position].datacount>1){
                    dataList[position].datacount--
                    holder.rvcount.text=currentItem.datacount.toString()
                    Thread {
                        foodDatabase.getDatabase(holder.itemView.context)
                            .foodDao()
                            .updateItemCount(
                                currentItem.dataTitle,
                                currentItem.datacount
                            )
                    }.start()

                }

        }
        holder.deletebutton.setOnClickListener {
            dataList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,dataList.size)
            Thread {
                foodDatabase.getDatabase(holder.itemView.context).foodDao()
                    .deleteItemByName(currentItem.dataTitle)
            }.start()
        }


    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rvImage: ImageView = itemView.findViewById(R.id.imageView4)
        val rvTitle: TextView = itemView.findViewById(R.id.foodnameid)
        val rvPrice: TextView = itemView.findViewById(R.id.priceid)
        val rvcount: TextView = itemView.findViewById(R.id.datacountid)

        val minusbutton: ImageButton=itemView.findViewById(R.id.minus)
        val addbutton: ImageButton=itemView.findViewById(R.id.add)

        val deletebutton : ImageButton=itemView.findViewById(R.id.trash)









    }


}


