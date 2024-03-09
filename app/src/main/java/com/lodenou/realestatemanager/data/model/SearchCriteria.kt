package com.lodenou.realestatemanager.data.model

import java.time.LocalDate

data class SearchCriteria(
    val minArea: Int?,
    val maxArea: Int?,
    val minPrice: Int?,
    val maxPrice: Int?,
//    val nearSchool: Boolean,
//    val nearShops: Boolean,
//    val onMarketSince: LocalDate
)
