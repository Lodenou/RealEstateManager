package com.lodenou.realestatemanager.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.lodenou.realestatemanager.ui.components.detailactivitycomponents.RealEstateDetailScreen
import com.lodenou.realestatemanager.ui.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : ComponentActivity() {

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val realEstateId = intent.getStringExtra("realEstateId") ?: return

        setContent {
                RealEstateDetailScreen(realEstateId, viewModel, onBackButtonClick = {
                    finish()
                }, onUpdateButtonClick = { //TODO
                 })
        }
    }
}


