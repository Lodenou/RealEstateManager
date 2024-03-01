package com.lodenou.realestatemanager.data.model

data class ImageWithDescription(
    var localUri: String = "", // URI local pour accès hors ligne
    var cloudUri: String? = "", // URI Cloud pour accès en ligne
    val description: String = ""
)