package com.lodenou.realestatemanager.ui.components.detailactivitycomponents

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lodenou.realestatemanager.data.model.RealEstate

@Composable
fun Info(realEstate: RealEstate){

    val pointsOfInterestText = buildString {
        if (realEstate.restaurant) append("Restaurant, ")
        if (realEstate.cinema) append("Cinéma, ")
        if (realEstate.ecole) append("École, ")
        if (realEstate.commerces) append("Commerces, ")
    }.removeSuffix(", ")

    Text(
        text = "Date d'entrée du bien : ${realEstate.marketEntryDate}",
        modifier = Modifier.padding(start = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(5.dp))
    if (realEstate.saleDate != null) {
        Text(
            text = "Date de vente du bien : ${realEstate.saleDate}",
            modifier = Modifier.padding(start = 8.dp),
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
    }
    Spacer(modifier = Modifier.height(5.dp))
    Text(
        text = "Agent: ${realEstate.realEstateAgent}",
        modifier = Modifier.padding(start = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(5.dp))
    Text(
        text = "Prix : ${realEstate.price}",
        modifier = Modifier.padding(start = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(5.dp))
    Text(
        text = "Type du bien : ${realEstate.type}",
        modifier = Modifier.padding(start = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(5.dp))
    Text(
        text = "Statut: ${realEstate.status}",
        modifier = Modifier.padding(start = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(5.dp))
    Text(
        text = "Date d'entrée du bien : ${realEstate.marketEntryDate}",
        modifier = Modifier.padding(start = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(5.dp))
    if (realEstate.saleDate != null) {
        Text(
            text = "Date de vente du bien : ${realEstate.saleDate}",
            modifier = Modifier.padding(start = 8.dp),
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
    }
    Spacer(modifier = Modifier.height(5.dp))
    Text(
        text = "Agent: ${realEstate.realEstateAgent}",
        modifier = Modifier.padding(start = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(5.dp))
    Text(
        text = "Prix : ${realEstate.price}",
        modifier = Modifier.padding(start = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(5.dp))
    Text(
        text = "Type du bien : ${realEstate.type}",
        modifier = Modifier.padding(start = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(5.dp))
    Text(
        text = "Statut: ${realEstate.status}",
        modifier = Modifier.padding(start = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(5.dp))

    if (pointsOfInterestText.isNotEmpty()) {
        Text(
            text = "Points d'intérêts : $pointsOfInterestText",
            modifier = Modifier.padding(start = 8.dp),
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
    } else {
        Text(
            text = "Points d'intérêts : Aucun",
            modifier = Modifier.padding(start = 8.dp),
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
    }
    Spacer(modifier = Modifier.height(5.dp))
}