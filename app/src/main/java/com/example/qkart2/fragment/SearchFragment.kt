package com.example.qkart2.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qkart2.SearchAdapter
import com.example.qkart2.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: SearchAdapter
    private val dataList = ArrayList<DataCLassMenu>()
    private val filteredList = ArrayList<DataCLassMenu>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSearchBinding.inflate(inflater, container, false)

        adapter = SearchAdapter(filteredList,
            context = requireContext(),
            scope = viewLifecycleOwner.lifecycleScope )

        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = this@SearchFragment.adapter
        }

        loadFoodFromProvider()
        setupSearchView()

        return binding.root
    }

    // 🔥 LOAD DATA FROM ADMIN APP CONTENT PROVIDER
    private fun loadFoodFromProvider() {
        val uri = Uri.parse("content://com.example.adminapp.provider/food")

        val cursor = try {
            requireContext().contentResolver.query(uri, null, null, null, null)
        } catch (e: SecurityException) {
            Log.e("MENU_DEBUG", "Permission denied: ${e.message}")
            null
        }

        if (cursor == null) return

        dataList.clear()

        cursor.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndexOrThrow("itemname"))
                val price = it.getString(it.getColumnIndexOrThrow("itemprice"))
                val imageUrl = it.getString(it.getColumnIndexOrThrow("url"))
                val description = it.getString(it.getColumnIndexOrThrow("description"))
                val ingredients = it.getString(it.getColumnIndexOrThrow("ingredients"))

                dataList.add(
                    DataCLassMenu(
                        name,
                        price,
                        imageUrl,
                        description,
                        ingredients
                    )
                )
            }
        }

        filteredList.clear()
        filteredList.addAll(dataList)
        adapter.notifyDataSetChanged()

        Log.d("MENU_DEBUG", "Items Loaded = ${dataList.size}")
    }
    @SuppressLint("ResourceAsColor")
    private fun setupSearchView() {

        val searchText = binding.searchView.findViewById<EditText>(
            androidx.appcompat.R.id.search_src_text
        )
        searchText.setTextColor(Color.BLACK)
        searchText.setHintTextColor(Color.GRAY)

        binding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(text: String?) {
        filteredList.clear()

        if (text.isNullOrBlank()) {
            filteredList.addAll(dataList)
        } else {
            val query = text.lowercase()
            for (item in dataList) {
                if (item.name.lowercase().contains(query)) {
                    filteredList.add(item)
                }
            }
        }

        adapter.notifyDataSetChanged()
    }
}
