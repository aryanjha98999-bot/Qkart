package com.example.qkart2.fragment

import android.content.Intent
import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.qkart2.MainActivity
import com.example.qkart2.R
import com.example.qkart2.databinding.FragmentCongratsBottomSheetFraagmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class congratsBottomSheetFraagment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCongratsBottomSheetFraagmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentCongratsBottomSheetFraagmentBinding.inflate(inflater,container,false)
        binding.button5.setOnClickListener {
            val intent= Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }


}