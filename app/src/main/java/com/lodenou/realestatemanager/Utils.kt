package com.lodenou.realestatemanager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.util.Date
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * Utility functions for currency conversion and date retrieval.
 *
 * Includes methods for converting currency between dollars and euros,
 * as well as for getting the current date in a standard format.
 */
object Utils {

    /**
     * Converts a given amount in dollars to euros.
     *
     * Uses a fixed conversion rate of 1 dollar to 0.812 euros.
     * The conversion result is rounded to the nearest integer.
     *
     * @param dollars The amount in dollars to be converted.
     * @return The converted amount in euros as an integer.
     */
    fun convertDollarToEuro(dollars: Int): Int {
        return (dollars * 0.812).roundToInt()
    }

    /**
     * Converts a given amount in euros to dollars.
     *
     * Uses a fixed conversion rate of 1 euro to 1.231 dollars.
     * The conversion result is rounded to the nearest integer.
     *
     * @param euros The amount in euros to be converted.
     * @return The converted amount in dollars as an integer.
     */
    fun convertEuroToDollar(euros: Int): Int {
        return (euros * 1.231).roundToInt()
    }

    fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    /**
     * I changed the method to check if there is internet access rather
     * than just wifi enabled which didn't necessarily mean internet access.
     */
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }
}