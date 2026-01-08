package com.example.qkart2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qkart2.adapter.NotificationAdapter
import com.example.qkart2.databinding.FragmentNotificationBottomSheetBinding
import com.example.qkart2.model.NotificationModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotificationBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNotificationBottomSheetBinding
    private val list = mutableListOf<NotificationModel>()
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNotificationBottomSheetBinding.inflate(inflater, container, false)

        adapter = NotificationAdapter(list)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        loadNotifications()

        return binding.root
    }

    private fun loadNotifications() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1) // 🔥 SHOW ONLY LATEST NOTIFICATION
            .addSnapshotListener { snapshot, _ ->

                if (snapshot == null || snapshot.isEmpty) return@addSnapshotListener

                list.clear()
                list.add(snapshot.documents[0].toObject(NotificationModel::class.java)!!)
                adapter.notifyDataSetChanged()
            }
    }

}
