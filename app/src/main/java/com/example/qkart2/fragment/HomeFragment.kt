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

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val dataList = ArrayList<DataCLass>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupImageSlider()
        setupRecyclerView()
        setupMenuClick()

        return binding.root
    }

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

            override fun doubleClick(position: Int) {

            }
        })
    }



    private fun setupRecyclerView() {

        loadData()

        binding.Recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = AdapterClass(
                dataList = dataList,
                context = requireContext(),
                scope = viewLifecycleOwner.lifecycleScope
            )
        }
    }

    private fun setupMenuClick() {
        binding.textViewMenu.setOnClickListener {
            MenubottomsheetFragment()
                .show(parentFragmentManager, "Menu")
        }
    }

    private fun loadData() {

        val imageList = arrayOf(
            "https://res.cloudinary.com/dgfxkudik/image/upload/v1766656119/xvm2t0wdaxq9ojubps3f.jpg",
            "https://res.cloudinary.com/dgfxkudik/image/upload/v1766661542/aj04fzmsvvfkg8iy1can.jpg",
            "https://res.cloudinary.com/dgfxkudik/image/upload/v1766661506/rxd9luu4jhejqfi0awxk.jpg",
            "https://res.cloudinary.com/dgfxkudik/image/upload/v1766661587/xwt8kdxi72ujats7etzj.jpg",
            "https://res.cloudinary.com/dgfxkudik/image/upload/v1766661648/q4rhsdzw23ih7t00pkdw.jpg"
        )

        val titleList = arrayOf("Burger", "Pasta", "Pizza", "Burrito", "Momos")
        val priceList = arrayOf("₹80", "₹100", "₹120", "₹40", "₹50")

        val descList = arrayOf(
            "A juicy sandwich made with a grilled patty placed inside a soft bun.",
            "An Italian dish made from boiled pasta tossed in a rich sauce.",
            "A baked flatbread topped with sauce and cheese.",
            "A Mexican wrap filled with rice, beans, and veggies.",
            "Steamed dumplings filled with spiced vegetables or meat."
        )

        val ingList = arrayOf(
            "Veg / Chicken Patty\nLettuce\nTomato\nOnion\nCheese",
            "Pasta\nOlive oil\nGarlic\nOnion\nSauce",
            "Pizza base\nMozzarella\nCapsicum\nOnion",
            "Tortilla\nRice\nBeans\nVeggies",
            "Maida\nCabbage\nCarrot\nOnion"
        )

        for (i in imageList.indices) {
            dataList.add(
                DataCLass(
                    imageList[i],
                    titleList[i],
                    priceList[i],
                    descList[i],
                    ingList[i]
                )
            )
        }
    }
}
