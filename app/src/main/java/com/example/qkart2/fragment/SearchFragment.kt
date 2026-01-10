package com.example.qkart2.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qkart2.SearchAdapter
import com.example.qkart2.databinding.FragmentSearchBinding
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SearchAdapter
    private val dataList = ArrayList<DataCLassMenu>()
    private val filteredList = ArrayList<DataCLassMenu>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        adapter = SearchAdapter(
            filteredList,
            context = requireContext(),
            scope = viewLifecycleOwner.lifecycleScope
        )

        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = this@SearchFragment.adapter
        }

        setupSearchView()
        loadFoodFromFirestore()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SuspiciousIndentation")
    private fun loadFoodFromFirestore() {

        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("canteens").get().addOnSuccessListener { canteenSnapshot ->

            val canteenMap = mutableMapOf<String, String>()
            for (canteen in canteenSnapshot.documents) {
                canteenMap[canteen.id] = canteen.getString("restaurantName") ?: ""
            }

            firestore.collectionGroup("menuItems")
                .get()
                .addOnSuccessListener { result ->

                    dataList.clear()

                    for (doc in result.documents) {

                        val canteenId = doc.reference.parent.parent?.id ?: ""

                        val item = DataCLassMenu(
                            itemId = doc.id,
                            name = doc.getString("itemname") ?: "",
                            price = doc.getString("itemprice") ?: "",
                            imageUrl = doc.getString("url") ?: "",
                            description = doc.getString("description") ?: "",
                            ingredients = doc.getString("ingredients") ?: "",
                            restaurant_name = canteenMap[canteenId] ?: "Unknown",
                            canteenId = canteenId
                        )

                            dataList.add(item)
                            filterList(binding.searchView.query?.toString())
                        }
                }
            }


    .addOnFailureListener {
                if (!isAdded || _binding == null) return@addOnFailureListener
                Toast.makeText(context, "Failed to load food", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupSearchView() {

        val searchText = binding.searchView.findViewById<EditText>(
            androidx.appcompat.R.id.search_src_text
        )
        searchText.setTextColor(Color.BLACK)
        searchText.setHintTextColor(Color.GRAY)

        binding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?) = false

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

        if (_binding != null) adapter.notifyDataSetChanged()
    }
}
