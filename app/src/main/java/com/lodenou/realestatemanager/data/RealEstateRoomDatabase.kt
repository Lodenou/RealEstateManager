package com.lodenou.realestatemanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lodenou.realestatemanager.data.model.Converters
import com.lodenou.realestatemanager.data.model.RealEstate

@Database(entities = [RealEstate::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RealEstateRoomDatabase : RoomDatabase() {

    abstract fun realEstateDao(): RealEstateDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: RealEstateRoomDatabase? = null

        fun getDatabase(context: Context): RealEstateRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RealEstateRoomDatabase::class.java,
                    "real_estate_database"
                )
                    .fallbackToDestructiveMigration() // Don't use in production just for development
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}