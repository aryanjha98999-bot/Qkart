package com.example.qkart2.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qkart2.MenuAdapter
import com.example.qkart2.databinding.FragmentMenubottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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

        binding.RecyclerviewMenu.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = menuAdapter
        }

        loadFoodFromProvider()

        binding.backicon.setOnClickListener {
            dismiss()
        }
    }

    private fun loadFoodFromProvider() {
        val uri = Uri.parse("content://com.example.adminapp.provider/food")

        val cursor = try {
            requireContext().contentResolver.query(uri, null, null, null, null)
        } catch (e: Exception) {
            Log.e("MENU_DEBUG", "Query failed", e)
            null
        }

        if (cursor == null) {
            Log.e("MENU_DEBUG", "Cursor is NULL")
            return
        }

        dataList.clear()

        cursor.use { c ->
            Log.d("MENU_DEBUG", "Rows found = ${c.count}")

            while (c.moveToNext()) {

                val name = c.getString(c.getColumnIndexOrThrow("itemname"))
                val price = c.getString(c.getColumnIndexOrThrow("itemprice"))
                val imageUrl = c.getString(c.getColumnIndexOrThrow("url"))

                val description = runCatching {
                    c.getString(c.getColumnIndexOrThrow("description"))
                }.getOrDefault("")

                val ingredients = runCatching {
                    c.getString(c.getColumnIndexOrThrow("ingredients"))
                }.getOrDefault("")

                dataList.add(
                    DataCLassMenu(
                        name = name,
                        price = price,
                        imageUrl = imageUrl,
                        description = description,
                        ingredients = ingredients
                    )
                )
            }
        }

        Log.d("MENU_DEBUG", "Final list size = ${dataList.size}")

        menuAdapter.notifyDataSetChanged()
    }
}
