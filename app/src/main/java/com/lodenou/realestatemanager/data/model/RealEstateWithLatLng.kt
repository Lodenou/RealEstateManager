package com.lodenou.realestatemanager.data.model

import com.google.android.gms.maps.model.LatLng

data class RealEstateWithLatLng(
    val realEstate: RealEstate,
    val latLng: LatLng
)