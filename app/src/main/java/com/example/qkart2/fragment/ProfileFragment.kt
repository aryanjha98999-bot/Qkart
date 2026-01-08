package com.example.qkart2.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.qkart2.LoginActivity
import com.example.qkart2.databinding.FragmentProfileBinding
import com.example.qkart2.model.Usermodel
import com.example.qkart2.roomdb.foodDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        sharedPreferences =
            requireContext().getSharedPreferences("notedata", Context.MODE_PRIVATE)
        loadData()
        loadProfileData()

        binding.save.setOnClickListener {
            saveData()
        }
        binding.button6.setOnClickListener {
            logoutUser()
        }

        return binding.root
    }

    private fun loadProfileData() {
        val uid = auth.currentUser?.uid ?: return

        FirebaseDatabase.getInstance().reference
            .child("users")
            .child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) return

                    val user = snapshot.getValue(Usermodel::class.java)

                    if (!user?.name.isNullOrBlank()) {
                        binding.namem.setText(user?.name)
                    }
                    if (!user?.email.isNullOrBlank()) {
                        binding.emailm.setText(user?.email)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load profile",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun saveData() {
        sharedPreferences.edit().apply {
            putString("name", binding.namem.text.toString())
            putString("address", binding.addressm.text.toString())
            putString("email", binding.emailm.text.toString())
            putString("number", binding.number.text.toString())
            apply()
        }

        Toast.makeText(
            requireContext(),
            "Information saved successfully 😊",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun loadData() {
        binding.namem.setText(sharedPreferences.getString("name", ""))
        binding.addressm.setText(sharedPreferences.getString("address", ""))
        binding.emailm.setText(sharedPreferences.getString("email", ""))
        binding.number.setText(sharedPreferences.getString("number", ""))
    }
    private fun logoutUser() {

        sharedPreferences.edit().clear().apply()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}
