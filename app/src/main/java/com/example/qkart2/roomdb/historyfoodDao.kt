package com.example.qkart2.roomdb

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface historyfoodDao {
    @Query("SELECT * FROM item2_table")
    fun getAll(): List<historyfoodData>

    // For ContentProvider (Other App)
    @Query("SELECT * FROM item2_table")
    fun getAllCursor(): Cursor

    @Query("DELETE FROM item2_table")
    suspend fun clearHistory()



    @Query("SELECT * FROM item2_table WHERE id = :itemId")
    suspend fun getItemById(itemId: Int): historyfoodData

    @Query("DELETE FROM item2_table WHERE id = :id")
    fun deleteById(id: Int?)


    @Query("DELETE FROM item2_table WHERE itemname = :name")
     fun deleteItemByName(name: String)


    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(historyfooddataclass: List<historyfoodData>)


    @Delete
    suspend fun Delete(historyfooddataclass: historyfoodData)

    @Query("DELETE FROM item2_table")
    suspend fun deleteAll()


    @Query("SELECT * FROM item2_table ORDER BY id DESC LIMIT 1")
    suspend fun getLastItem(): historyfoodData?
    @Query("UPDATE item2_table SET datacount = :count WHERE itemname = :title")
    fun updateItemCount(title: String, count: Int)





    @Query("SELECT COUNT(*) FROM item2_table WHERE itemname = :name")
    suspend infix fun countItemByName(name: String): Int

    @Query("SELECT * FROM item2_table WHERE itemname = :name LIMIT 1")
    suspend fun getItemByName(name: String): historyfoodData?
}