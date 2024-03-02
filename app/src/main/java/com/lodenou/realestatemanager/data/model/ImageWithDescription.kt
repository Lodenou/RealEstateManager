package com.lodenou.realestatemanager.data.model

data class ImageWithDescription(
    var imageUri: String = "", // URI local pour accès hors ligne
    val description: String = ""
)