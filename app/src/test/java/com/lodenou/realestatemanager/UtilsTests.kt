package com.lodenou.realestatemanager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.lodenou.realestatemanager.Utils.isInternetAvailable
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class UtilsTests {

    @Test
    fun testConvertDollarToEuro() {
        assertEquals(12, Utils.convertDollarToEuro(15)) // Test 15*0.812 = 12.18 rounded = 12
        assertEquals(8, Utils.convertDollarToEuro(10)) // Test 10*0.812 = 8.12 rounded = 8
        assertEquals(0, Utils.convertDollarToEuro(0)) // Test for 0 exception
        assertEquals(-8, Utils.convertDollarToEuro(-10)) // Test for negatives numbers
    }

    @Test
    fun testConvertEurosToDollar() {
        assertEquals(15, Utils.convertEuroToDollar(12)) // Tests unversed
        assertEquals(10, Utils.convertEuroToDollar(8))
        assertEquals(0, Utils.convertEuroToDollar(0))
        assertEquals(-10, Utils.convertEuroToDollar(-8))
    }

    @Test
    fun testGetTodayDate() {

        val utils = Utils
        val expectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())


        val result = utils.getTodayDate()


        assertEquals(expectedDate, result)
    }

    @Test
    fun `isInternetAvailable returns true when internet is available`() {
        // Mock Context
        val context = mock(Context::class.java)
        // Mock ConnectivityManager
        val connectivityManager = mock(ConnectivityManager::class.java)
        // Mock NetworkCapabilities
        val networkCapabilities = mock(NetworkCapabilities::class.java)

        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        `when`(connectivityManager.activeNetwork).thenReturn(null)
        `when`(connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)).thenReturn(networkCapabilities)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)

        assertTrue(isInternetAvailable(context))
    }

    @Test
    fun `isInternetAvailable returns false when internet is not available`() {
        // Mock Context
        val context = mock(Context::class.java)
        // Mock ConnectivityManager
        val connectivityManager = mock(ConnectivityManager::class.java)

        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)
        `when`(connectivityManager.activeNetwork).thenReturn(null)
        `when`(connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)).thenReturn(null)

        assertFalse(isInternetAvailable(context))
    }


}