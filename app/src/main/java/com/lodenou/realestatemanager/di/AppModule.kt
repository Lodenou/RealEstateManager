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


/**
 * Provides application-wide dependencies for the Real Estate application.
 * This module is installed in the SingletonComponent to ensure that the provided
 * instances are singleton and available throughout the application.
 *
 * Functions within this object provide dependencies such as the Room database instance
 * and DAOs (Data Access Objects) for accessing the database. It also provides the application
 * context to any part of the application requiring it.
 *
 * Usage of the Singleton annotation ensures that a single instance of each provided
 * dependency is created and shared across the application, promoting efficient resource
 * use and consistent data management.
 *
 * @Module marks this class as a Dagger module, indicating that it provides dependencies.
 * @InstallIn(SingletonComponent::class) specifies that the lifespan of the provided dependencies
 * matches the lifespan of the application itself.
 */
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