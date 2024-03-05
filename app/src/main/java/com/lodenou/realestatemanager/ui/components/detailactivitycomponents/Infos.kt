package com.lodenou.realestatemanager.ui.components.detailactivitycomponents

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
    Text(
        text = "Date d'entrée du bien : ${realEstate.marketEntryDate}",
        modifier = Modifier.padding(start = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold)
    )
    if (realEstate.saleDate != null) {
        Text(
            text = "Date de vente du bien : ${realEstate.saleDate}",
            modifier = Modifier.padding(start = 8.dp),
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
    }
    Text(
        text = "Agent: ${realEstate.realEstateAgent}",
        modifier = Modifier.padding(start = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold)
    )
    Text(
        text = "Prix : ${realEstate.price}",
        modifier = Modifier.padding(start = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold)
    )
    Text(
        text = "Type du bien : ${realEstate.type}",
        modifier = Modifier.padding(start = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold)
    )
    Text(
        text = "Statut: ${realEstate.status}",
        modifier = Modifier.padding(start = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold)
    )
    Text(
        text = "Points d'intérêts  : ${realEstate.pointsOfInterest}",
        modifier = Modifier.padding(start = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold)
    )
}