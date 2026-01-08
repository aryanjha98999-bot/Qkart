package com.example.qkart2

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.qkart2.databinding.ActivityPayOutBinding
import com.example.qkart2.fragment.congratsBottomSheetFraagment
import com.example.qkart2.roomdb.foodDatabase
import com.example.qkart2.roomdb.historyfoodData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PayOutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPayOutBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("notedata", MODE_PRIVATE)

        val total = intent.getIntExtra("total", 0)
        binding.total.text = "₹$total"

        loadData()

        binding.button.setOnClickListener {
            if (auth.currentUser == null) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            } else {
                placeOrder(total)
            }
        }
    }

    private fun placeOrder(total: Int) {

        val user = auth.currentUser ?: return

        val name = binding.namem.text.toString().trim()
        val address = binding.addressm.text.toString().trim()
        val number = binding.number.text.toString().trim()

        if (name.isEmpty() || address.isEmpty() || number.isEmpty()) {
            Toast.makeText(this, "Fill all details", Toast.LENGTH_SHORT).show()
            return
        }

        val orderData = hashMapOf(
            "userId" to user.uid,
            "name" to name,
            "address" to address,
            "number" to number,
            "total" to total,
            "timestamp" to FieldValue.serverTimestamp(),
            "status" to "pending"
        )

        firestore.collection("orders")
            .add(orderData)
            .addOnSuccessListener { orderDoc ->
                uploadCartItems(orderDoc.id)
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadCartItems(orderId: String) {

        lifecycleScope.launch(Dispatchers.IO) {

            val db = foodDatabase.getDatabase(this@PayOutActivity)
            val cartDao = db.foodDao()
            val historyDao = db.historyfoodDao()

            val cartItems = cartDao.getAll()
            if (cartItems.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PayOutActivity, "Cart empty", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val batch = firestore.batch()

            cartItems.forEach { item ->
                val docRef = firestore.collection("orders")
                    .document(orderId)
                    .collection("cartItems")
                    .document()

                val map = hashMapOf(
                    "itemName" to item.itemname,
                    "price" to item.itemprice,
                    "quantity" to item.datacount,
                    "imageUrl" to item.url
                )

                batch.set(docRef, map)
            }

            withContext(Dispatchers.Main) {
                batch.commit()
                    .addOnSuccessListener {

                        lifecycleScope.launch(Dispatchers.IO) {

                            val historyItems = cartItems.map {
                                historyfoodData(
                                    itemname = it.itemname,
                                    itemprice = it.itemprice.toString(),
                                    datacount = it.datacount,
                                    url = it.url,
                                    description = it.description,
                                    ingredients = it.ingredients
                                )
                            }

                            historyDao.deleteAll()
                            historyDao.insert(historyItems)
                            cartDao.deleteAll()

                            withContext(Dispatchers.Main) {
                                congratsBottomSheetFraagment()
                                    .show(supportFragmentManager, "congrats")

                                Toast.makeText(
                                    this@PayOutActivity,
                                    "Order placed successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this@PayOutActivity,
                            "Cart upload failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    private fun loadData() {
        binding.namem.setText(sharedPreferences.getString("name", ""))
        binding.addressm.setText(sharedPreferences.getString("address", ""))
        binding.number.setText(sharedPreferences.getString("number", ""))
    }
}
