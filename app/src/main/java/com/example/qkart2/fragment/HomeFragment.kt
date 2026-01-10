package com.example.qkart2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.qkart2.AdapterClass
import com.example.qkart2.DataCLass
import com.example.qkart2.R
import com.example.qkart2.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val dataList = ArrayList<DataCLass>()
    private lateinit var adapter: AdapterClass
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupImageSlider()     // ✅ unchanged
        setupRecyclerView()
        setupMenuClick()

        loadNewestItemsFromEachCanteen() // 🔥 real data

        return binding.root
    }

    // ================= SLIDER (UNCHANGED) =================

    private fun setupImageSlider() {

        val slides = arrayListOf(
            SlideModel(R.drawable.download, ScaleTypes.FIT),
            SlideModel(R.drawable.download2, ScaleTypes.FIT),
            SlideModel(R.drawable.download3, ScaleTypes.FIT)
        )

        binding.imageSlider.setImageList(slides, ScaleTypes.FIT)

        binding.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                when (position) {
                    0 -> {
                        MenubottomsheetFragment()
                            .show(parentFragmentManager, "Menu")
                    }
                    1 -> {
                        requireActivity()
                            .findViewById<BottomNavigationView>(R.id.botton_navigation)
                            .selectedItemId = R.id.search
                    }
                    2 -> {
                        requireActivity()
                            .findViewById<BottomNavigationView>(R.id.botton_navigation)
                            .selectedItemId = R.id.cart
                    }
                }
            }

            override fun doubleClick(position: Int) {}
        })
    }

    // ================= RECYCLER VIEW =================

    private fun setupRecyclerView() {

        adapter = AdapterClass(
            dataList = dataList,
            context = requireContext(),
            scope = viewLifecycleOwner.lifecycleScope
        )

        binding.Recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeFragment.adapter
        }
    }

    private fun setupMenuClick() {
        binding.textViewMenu.setOnClickListener {
            MenubottomsheetFragment()
                .show(parentFragmentManager, "Menu")
        }
    }

    // ================= FIRESTORE LOGIC =================

    private fun loadNewestItemsFromEachCanteen() {

        dataList.clear()
        adapter.notifyDataSetChanged()

        firestore.collection("canteens")
            .get()
            .addOnSuccessListener { canteensSnapshot ->

                for (canteenDoc in canteensSnapshot.documents) {

                    val canteenId = canteenDoc.id
                    val canteenName =
                        canteenDoc.getString("restaurantName") ?: "Unknown Canteen"

                    firestore.collection("canteens")
                        .document(canteenId)
                        .collection("menuItems")
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { menuSnapshot ->

                            if (!menuSnapshot.isEmpty) {

                                val itemDoc = menuSnapshot.documents[0]

                                val item = DataCLass(
                                    dataimaage = itemDoc.getString("url") ?: "",
                                    dataTitle = itemDoc.getString("itemname") ?: "",
                                    dataprice = itemDoc.getString("itemprice") ?:"",
                                    description = itemDoc.getString("description") ?: "",
                                    ingredients = itemDoc.getString("ingredients") ?: "",
                                    Restaurant_name = canteenName,
                                    canteenid = canteenId
                                )

                                dataList.add(item)
                                adapter.notifyItemInserted(dataList.size - 1)
                            }
                        }
                }
            }
    }
}
