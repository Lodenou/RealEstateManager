package com.lodenou.realestatemanager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun convertDollarToEuro(dollars: Int): Int {
        return Math.round(dollars * 0.812).toInt()
    }

    fun convertEuroToDollar(euros: Int): Int {
        return Math.round(euros * 1.231).toInt()
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

    // Add after

    fun LocalDate.toFirestoreTimestamp(): Timestamp {
        val date = Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
        return com.google.firebase.Timestamp(date)
    }

    // Extension function pour convertir un Timestamp de Firestore en LocalDate
    fun Timestamp.toLocalDate(): LocalDate {
        return this.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }
}