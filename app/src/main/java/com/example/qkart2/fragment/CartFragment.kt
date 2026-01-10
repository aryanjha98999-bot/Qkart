package com.example.qkart2.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qkart2.roomdb.foodDatabase
import com.example.qkart2.CartAdapter
import com.example.qkart2.PayOutActivity
import com.example.qkart2.databinding.FragmentCartBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class

CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartAdapter: CartAdapter
    private val cartList = ArrayList<dataclasscart>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCartBinding.inflate(inflater, container, false)

        cartAdapter = CartAdapter(cartList, viewLifecycleOwner.lifecycleScope)


        binding.cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())

            setHasFixedSize(true)
            adapter = cartAdapter
        }
                binding.button4.setOnClickListener {

                    var b = 0
                    for (i in cartList.indices) {
                        var a = cartList[i].dataprice
                            .filter { it.isDigit() }
                            .toIntOrNull() ?: 0
                        a=a*cartList[i].datacount
                        b += a

                        Log.d("TAG", "onCreateView: $b")


                    }

                    Toast.makeText(context, "your total payable amount is ₹$b", Toast.LENGTH_SHORT).show()

                    val intent = Intent(requireContext(), PayOutActivity::class.java)
                    intent.putExtra("total", b)
                    startActivity(intent)
                }

        loadCartItems()



        return binding.root
    }

    private fun loadCartItems() {

        val dao = foodDatabase.getDatabase(requireContext()).foodDao()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

            val roomItems = dao.getAll()

            val mappedItems = roomItems.map {
                dataclasscart(
                   it.url,it.itemname,it.itemprice,1,it.description,it.ingredients,it.Restaurant_name,it.canteenid
                )
            }

            withContext(Dispatchers.Main) {
                cartList.clear()
                cartList.addAll(mappedItems)
                cartAdapter.updateList(mappedItems)

            }
        }
    }
}
