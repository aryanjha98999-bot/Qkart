package com.example.qkart2.roomdb

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "item_table",
    indices = [Index(value = ["itemname"], unique = true)]
)
data class foodData(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val url: String,
    val itemname: String,
    val itemprice: String,
    val datacount: Int,
    val description: String,
    val ingredients: String,
    val Restaurant_name: String,
    val canteenid: String
)