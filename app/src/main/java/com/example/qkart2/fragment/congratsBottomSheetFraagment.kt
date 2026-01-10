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

    private var _binding: FragmentCongratsBottomSheetFraagmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCongratsBottomSheetFraagmentBinding.inflate(inflater, container, false)

        binding.button5.setOnClickListener {

            val activity = activity ?: return@setOnClickListener

            val intent = Intent(activity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            dismiss()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
