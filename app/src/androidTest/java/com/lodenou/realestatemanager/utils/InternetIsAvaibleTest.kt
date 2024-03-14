package com.lodenou.realestatemanager.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.platform.app.InstrumentationRegistry
import com.lodenou.realestatemanager.Utils.isInternetAvailable


@RunWith(AndroidJUnit4::class)
class NetworkUtilTest {

    // TURN ON INTERNET
    @Test
    fun testIsInternetAvailableWithInternetOn() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertTrue("Internet should be available", isInternetAvailable(context))
    }


    // TURN OFF INTERNET
    @Test
    fun testIsInternetAvailableWithInternetOff() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertFalse("Internet should not be available", isInternetAvailable(context))
    }
}