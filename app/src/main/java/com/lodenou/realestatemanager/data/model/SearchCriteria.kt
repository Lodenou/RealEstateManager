package com.lodenou.realestatemanager.data.model

import java.time.LocalDate

data class SearchCriteria(
    val areaRange: ClosedFloatingPointRange<Float>,
    val priceRange: ClosedRange<Float>,
    val nearSchool: Boolean,
    val nearShops: Boolean,
    val onMarketSince: LocalDate
)
