package com.example.qkart2.model

data class NotificationModel(
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val read: Boolean = false
)