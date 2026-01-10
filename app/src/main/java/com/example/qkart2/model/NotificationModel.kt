package com.example.qkart2.model

import com.google.firebase.Timestamp

data class NotificationModel(
    val orderName: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0
)
