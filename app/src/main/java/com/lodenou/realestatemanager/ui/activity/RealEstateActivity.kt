package com.lodenou.realestatemanager.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.lodenou.realestatemanager.ui.viewmodel.RealEstateViewModel
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import com.lodenou.realestatemanager.ui.components.realestateactivitycomponents.DrawerContent
import com.lodenou.realestatemanager.ui.components.realestateactivitycomponents.RealEstateListScreen
import com.lodenou.realestatemanager.ui.components.realestateactivitycomponents.ToolbarRealEstate
import com.lodenou.realestatemanager.ui.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RealEstateActivity : ComponentActivity() {


    private val viewModel: RealEstateViewModel by viewModels()

    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private val searchViewModel : SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialisation de permissionLauncher
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // La permission a été accordée, continuez avec l'opération
                setupContent(searchViewModel)
            } else {
                // La permission a été refusée, gérez le cas
                Toast.makeText(this, "Permission required for app functionality", Toast.LENGTH_LONG).show()
            }
        }

        checkAndRequestPermissions(searchViewModel)
    }

    private fun checkAndRequestPermissions(searchViewModel: SearchViewModel) {
        when {
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.S -> {
                // Pour API 32 et inférieures, demandez READ_EXTERNAL_STORAGE et WRITE_EXTERNAL_STORAGE
                val hasReadPermission = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                val hasWritePermission = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

                if (!hasReadPermission || !hasWritePermission) {
                    permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else {
                    setupContent(searchViewModel)
                }
            }
            else -> {
                setupContent(searchViewModel)
            }
        }
    }


    private fun setupContent(searchViewModel: SearchViewModel) {
        setContent {
//            RealEstateManagerTheme {
                MainScreen(viewModel, searchViewModel)
//            }
        }
    }
}
@Composable
fun MainScreen(viewModel: RealEstateViewModel, searchViewModel: SearchViewModel) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val searchResults by searchViewModel.searchResults.collectAsState()

    ModalNavigationDrawer(
        drawerContent = { DrawerContent(onNavigate = { intent -> context.startActivity(intent) }) },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                ToolbarRealEstate(onMenuClick = {
                    coroutineScope.launch {
                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                    }
                }, viewModel,searchViewModel )
            }
        ) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                color = MaterialTheme.colorScheme.background
            ) {

                val realEstatesToShow = if (!searchViewModel.searchPerformed.value) {
                    // Si aucune recherche n'a été effectuée, affichez tous les biens
                    viewModel.realEstates.observeAsState(emptyList()).value
                } else {
                    // Sinon, utilisez les résultats de la recherche (même s'ils sont vides)
                    searchResults
                }
                RealEstateListScreen(realEstates = realEstatesToShow)
            }
        }
    }
}

