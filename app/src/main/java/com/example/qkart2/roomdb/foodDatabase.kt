package com.example.qkart2.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.qkart2.roomdb.foodData
import com.example.qkart2.roomdb.historyfoodDao

@Database(entities = [foodData::class, historyfoodData::class ], version = 8)
abstract class foodDatabase : RoomDatabase() {

    abstract fun foodDao(): foodDao
    abstract fun historyfoodDao(): historyfoodDao



    companion object {
        @Volatile
        private var INSTANCE: foodDatabase? = null

        fun getDatabase(context: Context): foodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    foodDatabase::class.java,
                    "food_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}