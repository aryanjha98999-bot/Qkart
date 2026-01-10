package com.example.qkart2

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qkart2.databinding.ActivityCanteenMenuBinding
import com.example.qkart2.fragment.DataCLassMenu
import com.google.firebase.firestore.FirebaseFirestore

class CanteenMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCanteenMenuBinding
    private val dataList = ArrayList<DataCLassMenu>()
    private lateinit var adapter: MenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )
        super.onCreate(savedInstanceState)

        binding = ActivityCanteenMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val canteenId = intent.getStringExtra("canteenId") ?: ""
        val canteenName = intent.getStringExtra("canteenName") ?: ""

        if (canteenId.isEmpty()) {
            Toast.makeText(this, "Invalid canteen", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.toolbarTitle.text = canteenName
        binding.backBtn.setOnClickListener { finish() }

        adapter = MenuAdapter(
            context = this,
            dataList = dataList,
            scope = lifecycleScope
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        loadCanteenMenu(canteenId, canteenName)
    }

    private fun loadCanteenMenu(canteenId: String, canteenName: String) {

        FirebaseFirestore.getInstance()
            .collection("canteens")
            .document(canteenId)
            .collection("menuItems")
            .get()
            .addOnSuccessListener { snapshot ->

                dataList.clear()

                for (doc in snapshot.documents) {
                    dataList.add(
                        DataCLassMenu(
                            itemId = doc.id,
                            imageUrl = doc.getString("url") ?: "",
                            name = doc.getString("itemname") ?: "",
                            price = doc.getString("itemprice") ?: "",
                            description = doc.getString("description") ?: "",
                            ingredients = doc.getString("ingredients") ?: "",
                            canteenId = canteenId,
                            restaurant_name = canteenName)

                    )
                }

                adapter.notifyDataSetChanged()
            }
    }
}
