package com.lodenou.realestatemanager

import junit.framework.TestCase.assertEquals
import org.junit.Test

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
}