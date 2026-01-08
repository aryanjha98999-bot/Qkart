package com.example.qkart2.roomdb

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.qkart2.roomdb.foodData

@Dao
interface foodDao {
    @Query("SELECT * FROM item_table")
    fun getAll(): List<foodData>

    // For ContentProvider (Other App)
    @Query("SELECT * FROM item_table")
    fun getAllCursor(): Cursor


    @Query("SELECT * FROM item_table WHERE id = :itemId")
    suspend fun getItemById(itemId: Int): foodData

    @Query("DELETE FROM item_table WHERE id = :id")
    fun deleteById(id: Int?)


    @Query("DELETE FROM item_table WHERE itemname = :name")
     fun deleteItemByName(name: String)


    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(fooddataclass: foodData)


    @Delete
    suspend fun Delete(fooddataclass: foodData)

    @Query("DELETE FROM item_table")
    suspend fun deleteAll()


    @Query("SELECT * FROM item_table ORDER BY id DESC LIMIT 1")
    suspend fun getLastItem(): foodData?
    @Query("UPDATE item_table SET datacount = :count WHERE itemname = :title")
    fun updateItemCount(title: String, count: Int)





    @Query("SELECT COUNT(*) FROM item_table WHERE itemname = :name")
    suspend infix fun countItemByName(name: String): Int

    @Query("SELECT * FROM item_table WHERE itemname = :name LIMIT 1")
    suspend fun getItemByName(name: String): foodData?
}