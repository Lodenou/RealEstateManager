package com.lodenou.realestatemanager.data


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import io.reactivex.rxjava3.core.Observable

import com.lodenou.realestatemanager.GeocodeResult
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory

interface RealestateApi {
    @GET("geocode/json?")
    fun getLatLngFromAddress(
        @retrofit2.http.Query("address") address: String,
        @retrofit2.http.Query("key") apiKey: String
    ): Observable<GeocodeResult>

    companion object {
        val retrofitService: RealestateApi by lazy {
            Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder().addInterceptor(
                        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                    ).build()
                )
                .build().create(RealestateApi::class.java)
        }
    }
}