package com.example.qkart2

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.ArrayAdapter
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
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject

class PayOutActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivityPayOutBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var totalAmount = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val modeofpayment = arrayOf(
            "Online Payment",
        )



        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            modeofpayment
        )


        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("notedata", MODE_PRIVATE)

        totalAmount = intent.getIntExtra("total", 0)
        binding.total.text = "₹$totalAmount"

        binding.backicon2.setOnClickListener { finish() }

        loadSavedData()
        binding.listoflocations.setAdapter(adapter)

        binding.button.setOnClickListener {
            if (auth.currentUser == null) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            }

            else if (binding.listoflocations.text.toString().isEmpty()) {
                Toast.makeText(this, "Please select the mode of payment", Toast.LENGTH_SHORT).show()
            }
            else {
                binding.button.isEnabled = false  //prevent double payment
                startPayment()
            }

        }
    }
  private fun startPayment() {

        val name = binding.namem.text.toString().trim()
        val address = binding.addressm.text.toString().trim()
        val number = binding.number.text.toString().trim()

        if (name.isEmpty() || address.isEmpty() || number.isEmpty()) {
            Toast.makeText(this, "Fill all details", Toast.LENGTH_SHORT).show()
            binding.button.isEnabled = true
            return
        }

        val checkout = Checkout()
        checkout.setKeyID("rzp_test_S7aSn8POMRQyGi")

        val options = JSONObject().apply {
            put("name", "QKart")
            put("description", "Food Order Payment")
            put("currency", "INR")
            put("amount", totalAmount * 100)

            put("prefill", JSONObject().apply {
                put("contact", number)
                put("email", auth.currentUser?.email ?: "")
            })
        }

        try {
            checkout.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Payment error", Toast.LENGTH_SHORT).show()
            binding.button.isEnabled = true
        }
    }

    override fun onPaymentSuccess(paymentId: String?) {
        Checkout.clearUserData(this)

        saveUserData()

        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
        placeOrder(paymentId)
    }

    override fun onPaymentError(code: Int, response: String?) {
        Checkout.clearUserData(this)
        binding.button.isEnabled = true
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        Checkout.clearUserData(this)
        super.onDestroy()
    }

    private fun placeOrder(paymentId: String?) {

        lifecycleScope.launch(Dispatchers.IO) {

            val user = auth.currentUser
            if (user == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PayOutActivity, "User not logged in", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val name = binding.namem.text.toString().trim()
            val address = binding.addressm.text.toString().trim()
            val number = binding.number.text.toString().trim()

            val db = foodDatabase.getDatabase(this@PayOutActivity)
            val cartItems = db.foodDao().getAll()

            if (cartItems.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PayOutActivity, "Cart empty", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            val canteenId = cartItems.first().canteenid

            val orderRef = firestore.collection("canteens")
                .document(canteenId)
                .collection("orders")
                .document()

            val total = cartItems.sumOf { parsePrice(it.itemprice) * it.datacount }

            val orderData = hashMapOf(
                "userId" to user.uid,
                "name" to name,
                "address" to address,
                "number" to number,
                "paymentId" to paymentId,
                "total" to total,
                "timestamp" to FieldValue.serverTimestamp(),
                "status" to "paid"
            )
            orderRef.set(orderData).await()

            for (item in cartItems) {

                val itemData = hashMapOf(
                    "itemName" to item.itemname,
                    "price" to parsePrice(item.itemprice),
                    "quantity" to item.datacount,
                    "imageUrl" to item.url
                )

                orderRef.collection("cartItems")
                    .add(itemData)
                    .await()
            }


            val historyItems = cartItems.map {
                historyfoodData(
                    url = it.url,
                    itemname = it.itemname,
                    itemprice = it.itemprice,
                    datacount = it.datacount,
                    description = it.description,
                    ingredients = it.ingredients,
                    Restaurant_name = it.Restaurant_name,
                    canteenid = it.canteenid
                )
            }

            db.historyfoodDao().insert(historyItems)
            db.foodDao().deleteAll()

            withContext(Dispatchers.Main) {
                congratsBottomSheetFraagment().show(supportFragmentManager, "congrats")
                Toast.makeText(this@PayOutActivity, "Order placed successfully", Toast.LENGTH_SHORT)
                    .show()
                binding.button.isEnabled = true
            }
        }
    }

    private fun loadSavedData() {
        binding.namem.setText(sharedPreferences.getString("name", ""))
        binding.addressm.setText(sharedPreferences.getString("address", ""))
        binding.number.setText(sharedPreferences.getString("number", ""))
    }

    private fun saveUserData() {
        sharedPreferences.edit()
            .putString("name", binding.namem.text.toString())
            .putString("address", binding.addressm.text.toString())
            .putString("number", binding.number.text.toString())
            .apply()
    }

    private fun parsePrice(price: String): Int {
        return price.replace("₹", "").trim().toIntOrNull() ?: 0
    }
}
