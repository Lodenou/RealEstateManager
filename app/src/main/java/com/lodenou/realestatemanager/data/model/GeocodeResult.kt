package com.lodenou.realestatemanager

import com.google.gson.annotations.SerializedName


data class GeocodeResult (
  @SerializedName("results" ) var results : ArrayList<Results> = arrayListOf(),
  @SerializedName("status"  ) var status  : String?            = null
)