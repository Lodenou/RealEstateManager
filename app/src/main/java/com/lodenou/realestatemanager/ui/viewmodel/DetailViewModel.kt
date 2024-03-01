package com.lodenou.realestatemanager.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lodenou.realestatemanager.Location
import com.lodenou.realestatemanager.data.model.ImageWithDescription
import com.lodenou.realestatemanager.data.model.RealEstate
import com.lodenou.realestatemanager.data.repository.RealEstateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: RealEstateRepository
) : ViewModel() {


    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    private val compositeDisposable = CompositeDisposable()
    /**
     * Retrieves a real estate property by its ID.
     */
    fun getRealEstateFromRoomById(id: String): LiveData<RealEstate> {
        return repository.getRealEstateByIdRoom(id).asLiveData()
    }

    private val _realEstate = MutableLiveData<RealEstate?>()
    val realEstate: LiveData<RealEstate?> = _realEstate

    fun getRealEstateFromFirestore(id: String) = viewModelScope.launch {
        try {
            val documentSnapshot = repository.getRealEstateByIdFirestore(id).await()
            documentSnapshot.let { document ->
                val realEstate = document.getTimestamp("marketEntryDate")?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                    ?.let {
                        RealEstate(
                            id = document.id,
                            type = document.getString("type") ?: "",
                            price = document.getDouble("price") ?: 0.0,
                            area = document.getDouble("area") ?: 0.0,
                            numberOfRooms = document.getLong("numberOfRooms")?.toInt() ?: 0,
                            description = document.getString("description") ?: "",
                            images = (document["images"] as? List<Map<String, Any>>)?.map { imageMap ->
                                ImageWithDescription(
                                    localUri = imageMap["localUri"] as? String ?: "",
                                    cloudUri = imageMap["cloudUri"] as? String ?: "",
                                    description = imageMap["description"] as? String ?: ""
                                )
                            } ?: listOf(),
                            address = document.getString("address") ?: "",
                            pointsOfInterest = document.getString("pointsOfInterest") ?: "",
                            status = document.getString("status") ?: "",
                            marketEntryDate = it,
                            saleDate = document.getTimestamp("saleDate")?.toDate()?.toInstant()?.atZone(
                                ZoneId.systemDefault())?.toLocalDate(),
                            realEstateAgent = document.getString("realEstateAgent") ?: ""
                        )
                    }
                _realEstate.value = realEstate
            }
        } catch (e: Exception) {
            Log.e("RealEstateDetailVM", "Error loading Real Estate", e)
            _realEstate.value = null
        }
    }

    fun getLatLngFromAddress(address: String) {
        compositeDisposable.add(
            repository.getLatLngFromAddress(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { geocodeResult ->
                    // Vérifiez si les résultats sont non vides, sinon retournez null de manière sûre
                    geocodeResult.results.firstOrNull()?.geometry?.location ?: Location(35.4, 25.0)
                }
                .subscribe({ location ->
                    // Ici, location peut être null si aucun résultat n'est trouvé ou si une erreur se produit
                    _location.value = location
                }, { error ->
                    Log.e("DetailViewModel", "Error fetching location", error)
                    _location.value = null // Assurez-vous de gérer l'erreur en mettant à jour _location avec null si nécessaire
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear() // Clean sub when vm is destroyed
    }

}