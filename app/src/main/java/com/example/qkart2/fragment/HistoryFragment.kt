package com.example.qkart2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qkart2.roomdb.foodDatabase
import com.example.qkart2.DataCLasshistory
import com.example.qkart2.HistoryAdapter
import com.example.qkart2.databinding.FragmentHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private val cartList = ArrayList<DataCLasshistory>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        historyAdapter = HistoryAdapter(
            dataList = cartList,
            context = requireContext()
        )
        binding.Recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
            setHasFixedSize(true)
        }

        loadHistoryItems()

        return binding.root
    }

    private fun loadHistoryItems() {

        val dao = foodDatabase.getDatabase(requireContext()).historyfoodDao()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

            val roomItems = dao.getAll()

            val mappedItems = roomItems.map {
                DataCLasshistory(
                    it.url,
                    it.itemname,
                    it.itemprice.toString(),
                    it.datacount,
                    it.description,
                    it.ingredients,
                    it.Restaurant_name,
                    it.canteenid
                )
            }

            withContext(Dispatchers.Main) {

                cartList.clear()
                cartList.addAll(mappedItems)
                historyAdapter.notifyDataSetChanged()

                if (cartList.isEmpty()) {
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.Recyclerview.visibility = View.GONE
                } else {
                    binding.emptyLayout.visibility = View.GONE
                    binding.Recyclerview.visibility = View.VISIBLE
                }
            }
        }
    }




}



