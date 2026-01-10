package com.example.qkart2

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.qkart2.databinding.ActivityDetailsBinding
import com.example.qkart2.roomdb.foodData
import com.example.qkart2.roomdb.foodDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class detailsActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val dao = foodDatabase.getDatabase(this).foodDao()

        val name = intent.getStringExtra("menuname") ?: ""
        val image = intent.getStringExtra("menuimage") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val ingredients = intent.getStringExtra("ingredients") ?: ""
        val price = intent.getStringExtra("menuprice") ?: ""
        val canteenId = intent.getStringExtra("canteenId") ?: ""
        val restaurant_name = intent.getStringExtra("restaurant_name") ?: ""


        binding.textView22.text = restaurant_name
        binding.textView17.text = name
        binding.textView20.text = description
        binding.textView23.text = ingredients
        binding.backicon3.setOnClickListener {
            finish()
        }

        Glide.with(this)
            .load(image)
            .placeholder(R.drawable.m)
            .error(R.drawable.m)
            .into(binding.imageView8)
        binding.addToCartBtn.setOnClickListener {

            lifecycleScope.launch(Dispatchers.IO) {

                val count = dao.countItemByName(name)

                withContext(Dispatchers.Main) {

                    if (count > 0) {
                        Toast.makeText(
                            this@detailsActivity2,
                            "Item already added to cart",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                        val cartItem = foodData(
                            itemname = name,
                            url = image,
                            itemprice = price,
                            datacount = 1,
                            description = description,
                            ingredients = ingredients,
                            Restaurant_name = restaurant_name,
                            canteenid = canteenId
                        )

                        dao.insert(cartItem)

                        Toast.makeText(
                            this@detailsActivity2,
                            "Item added to cart",
                            Toast.LENGTH_SHORT
                        ).show()

                        binding.addToCartBtn.text = "Added"
                        binding.addToCartBtn.isEnabled = false
                    }
                }
            }
        }
    }


}
