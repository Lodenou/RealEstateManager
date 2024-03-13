package com.lodenou.realestatemanager.di

import android.content.Context
import androidx.room.Room
import com.lodenou.realestatemanager.data.RealEstateDao
import com.lodenou.realestatemanager.data.RealEstateRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRealEstateRoomDatabase(@ApplicationContext context: Context): RealEstateRoomDatabase {
        return Room.databaseBuilder(
            context,
            RealEstateRoomDatabase::class.java,
            "real_estate_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideRealEstateDao(database: RealEstateRoomDatabase): RealEstateDao {
        return database.realEstateDao()
    }


    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext appContext: Context): Context = appContext
}