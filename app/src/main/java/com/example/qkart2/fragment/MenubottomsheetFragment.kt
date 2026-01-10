package com.example.qkart2.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qkart2.MenuAdapter
import com.example.qkart2.databinding.FragmentMenubottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore

class MenubottomsheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentMenubottomsheetBinding
    private val dataList = ArrayList<DataCLassMenu>()
    private lateinit var menuAdapter: MenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenubottomsheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        menuAdapter = MenuAdapter(
            dataList,
            requireContext(),
            viewLifecycleOwner.lifecycleScope
        )
        val recyclerView = binding.RecyclerviewMenu
        recyclerView.adapter=menuAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        loadFoodFromFirestore()

        binding.backicon.setOnClickListener {
            dismiss()
        }
    }

    private fun loadFoodFromFirestore() {

        FirebaseFirestore.getInstance()
            .collection("menu")
            .get()
            .addOnSuccessListener { result->

                dataList.clear()

                for (doc in result.documents) {
                    val item = DataCLassMenu(
                        name = doc.getString("itemname") ?: "",
                        price = doc.getString("itemprice") ?: "",
                        imageUrl = doc.getString("url") ?: "",
                        description = doc.getString("description") ?: "",
                        ingredients = doc.getString("ingredients") ?: ""
                    )
                    dataList.add(item)
                }

                Toast.makeText(requireContext(), "Menu Loaded", Toast.LENGTH_SHORT).show()
                menuAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to load menu", Toast.LENGTH_SHORT).show()
            }
    }
}
