package com.lodenou.realestatemanager.data.repository

import android.net.Uri
import android.util.Log
import androidx.annotation.WorkerThread
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.lodenou.realestatemanager.BuildConfig
import com.lodenou.realestatemanager.GeocodeResult
import com.lodenou.realestatemanager.Utils.toFirestoreTimestamp
import io.reactivex.rxjava3.core.Observable
import com.lodenou.realestatemanager.data.RealEstateDao
import com.lodenou.realestatemanager.data.RealestateApi
import com.lodenou.realestatemanager.data.model.ImageWithDescription
import com.lodenou.realestatemanager.data.model.RealEstate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

class RealEstateRepository @Inject constructor(
    private val realEstateDao: RealEstateDao,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore) {


    // ROOM
    // Flow of all real estate properties. Unsorted
    val allRealEstates: Flow<List<RealEstate>> = realEstateDao.getAllRealEstates()


    @WorkerThread
    suspend fun insert(realEstate: RealEstate) {
        realEstateDao.insert(realEstate)
    }

    @WorkerThread
    fun getAllRealEstatesSync() : Flow<List<RealEstate>> {
        return  realEstateDao.getAllRealEstatesSync()
    }

    @WorkerThread
    suspend fun update(realEstate: RealEstate) {
        realEstateDao.update(realEstate)
    }

    @WorkerThread
    suspend fun delete(realEstate: RealEstate) {
        realEstateDao.delete(realEstate)
    }

    fun getRealEstateByIdRoom(id: String): Flow<RealEstate> {
        return realEstateDao.getRealEstateById(id)
    }

    suspend fun deleteRealEstateById(id: String) {
        realEstateDao.deleteRealEstateById(id)
    }




    // Cloud storage
    suspend fun uploadImage(imageUri: Uri): String {
        val imageRef = storage.reference.child("images/${UUID.randomUUID()}")
        imageRef.putFile(imageUri).await() // wait end of the upload
        return imageRef.downloadUrl.await().toString() // get & send url
    }



    // Firestore


    // listen to firestore to know if document is delete and need to be in room db
    fun setupFirestoreDocumentListener(scope: CoroutineScope) {
        firestore.collection("RealEstate").addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("Repository", "Listen failed.", e)
                return@addSnapshotListener
            }

            snapshots?.documentChanges?.forEach { dc ->
                when (dc.type) {
                    DocumentChange.Type.REMOVED -> {
                        val id = dc.document.id
                        scope.launch(Dispatchers.IO) {
                            deleteRealEstateById(id)
                        }
                    }
                    // Gérez les ajouts et les mises à jour selon votre besoin
                    else -> {}
                }
            }
        }
    }

    fun listenToRealEstatesUpdates(): Flow<List<RealEstate>> = callbackFlow {
        val listenerRegistration = firestore.collection("RealEstate")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("RealEstateRepository", "Listen failed.", e)
                    close(e) // Ferme le Flow avec une erreur
                } else {
                    val realEstateList = snapshots?.documents?.mapNotNull { document ->
                        documentToRealEstate(document)
                    }.orEmpty()
                    trySend(realEstateList).isSuccess
                }
            }
        // unsubscribe
        awaitClose {
            listenerRegistration.remove()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun documentToRealEstate(document: DocumentSnapshot): RealEstate? {
        val id = document.getString("id") ?: return null
        val type = document.getString("type") ?: return null // null if missing, same for all
        val price = document.getDouble("price") ?: return null
        val area = document.getDouble("area") ?: return null
        val numberOfRooms = document.getLong("numberOfRooms")?.toInt() ?: return null
        val description = document.getString("description") ?: return null

        val images = (document.get("images") as? List<Map<String, Any>>)?.mapNotNull { imageMap ->
            // Assurez-vous d'extraire correctement localUri, cloudUri, et description
            val localUri = imageMap["localUri"] as? String ?: return@mapNotNull null
            val cloudUri = imageMap["cloudUri"] as? String ?: "" // might be null
            val description = imageMap["description"] as? String ?: ""

            ImageWithDescription(localUri = localUri, cloudUri = cloudUri, description = description)
        } ?: listOf() // Liste vide si aucune image

        val address = document.getString("address") ?: return null
        val pointsOfInterest = document.getString("pointsOfInterest") ?: ""
        val status = document.getString("status") ?: return null
        val marketEntryDate: LocalDate? = try {
            document.getTimestamp("marketEntryDate")?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
        } catch (e: Exception) {
            null
        }
        val saleDate: LocalDate? = try {
            document.getTimestamp("saleDate")?.toDate()?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
        } catch (e: Exception) {
            null
        }

        val realEstateAgent = document.getString("realEstateAgent") ?: return null
        val needsSyncToRoom = document.getBoolean("needsSyncToRoom") ?: true
        return marketEntryDate?.let {
            RealEstate(
                id = id,
                type = type,
                price = price,
                area = area,
                numberOfRooms = numberOfRooms,
                description = description,
                images = images,
                address = address,
                pointsOfInterest = pointsOfInterest,
                status = status,
                marketEntryDate = it,
                saleDate = saleDate,
                realEstateAgent = realEstateAgent,
                needsSyncToRoom = needsSyncToRoom,
                needsSyncToFirestore = false
            )
        }
    }


    fun saveRealEstateToFirestore(realEstate: RealEstate, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val firestoreData = hashMapOf(
            "id" to realEstate.id,
            "type" to realEstate.type,
            "price" to realEstate.price,
            "area" to realEstate.area,
            "numberOfRooms" to realEstate.numberOfRooms,
            "description" to realEstate.description,
            "images" to realEstate.images?.map { image ->
                hashMapOf(
                    "localUri" to image.localUri,
                    "cloudUri" to image.cloudUri, // might be null
                    "description" to image.description
                )
            },
            "address" to realEstate.address,
            "pointsOfInterest" to realEstate.pointsOfInterest,
            "status" to realEstate.status,
            "marketEntryDate" to realEstate.marketEntryDate.toFirestoreTimestamp(),
            "saleDate" to realEstate.saleDate?.toFirestoreTimestamp(),
            "realEstateAgent" to realEstate.realEstateAgent,
            "needsSyncToRoom" to realEstate.needsSyncToRoom,
            "needsSyncToFirestore" to false
        )

        // Use our autogenerated id to set it as document id
        firestore.collection("RealEstate").document(realEstate.id).set(firestoreData)
            .addOnSuccessListener {
                Log.d("Repository", "Document saved to Firestore ")
                onSuccess(realEstate.id)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun synchronizeFirestoreToRoom() = withContext(Dispatchers.IO) {
        try {
            val documents = firestore.collection("RealEstate")
                .whereEqualTo("needsSync", true)
                .get()
                .await()

            documents.forEach { document ->

                val id = document.getString("id") ?: return@forEach
                // Extraction manuelle des images
                val imagesList: List<ImageWithDescription>? = document.get("images")?.let { images ->
                    (images as? List<Map<String, Any>>)?.map { imageMap ->
                        ImageWithDescription(
                            localUri = imageMap["localUri"] as? String ?: "",
                            cloudUri = imageMap["cloudUri"] as? String, // might be null
                            description = imageMap["description"] as? String ?: ""
                        )
                    }
                }

                val realEstate = RealEstate(
                    id = id,
                    type = document.getString("type"),
                    price = document.getDouble("price"),
                    area = document.getDouble("area"),
                    numberOfRooms = document.getLong("numberOfRooms")?.toInt(),
                    description = document.getString("description"),
                    images = imagesList, // Utilisez la liste d'images extraite
                    address = document.getString("address"),
                    pointsOfInterest = document.getString("pointsOfInterest"),
                    status = document.getString("status"),
                    marketEntryDate = document.getDate("marketEntryDate")?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate() ?: LocalDate.now(),
                    saleDate = document.getDate("saleDate")?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate(),
                    realEstateAgent = document.getString("realEstateAgent"),
                    needsSyncToRoom = false
                )


                val existingRealEstate = realEstateDao.getRealEstateById(id).firstOrNull()

                if (existingRealEstate != null) {
                    Log.d("Repository", "Update : Synchronization from Firestore to Room succeeded")
                    // if object exist update
                    realEstateDao.update(realEstate)
                } else {
                    // else insert new object
                    Log.d("Repository", "Insert : Synchronization from Firestore to Room succeeded")
                    realEstateDao.insert(realEstate)
                }


                // Marquez l'objet comme synchronisé en définissant needsSync sur false dans Firestore
                markRealEstateAsSyncedInFirestore(id)
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error synchronizing Firestore to Room", e)
        }
    }
    private suspend fun markRealEstateAsSyncedInFirestore(realEstateId: String) = withContext(Dispatchers.IO) {
        try {
            firestore.collection("RealEstate").document(realEstateId)
                .update("needsSyncToRoom", false)
                .await()  // Utilisez `await` pour s'assurer que l'opération est complétée avant de continuer
            Log.d("Repository", "RealEstate $realEstateId marked as synced in Firestore.")
        } catch (e: Exception) {
            Log.e("Repository", "Failed to mark RealEstate $realEstateId as synced in Firestore.", e)
        }
    }

    suspend fun synchronizeRoomToFirestore() = withContext(Dispatchers.IO) {
        realEstateDao.getUnsyncedRealEstates().collect { realEstatesList ->
            realEstatesList.forEach { realEstate ->
                try {
                    // Préparation de l'objet pour Firestore avec conversion des données nécessaires
                    val firestoreObject = hashMapOf(
                        "id" to realEstate.id,
                        "type" to realEstate.type,
                        "price" to realEstate.price,
                        "area" to realEstate.area,
                        "numberOfRooms" to realEstate.numberOfRooms,
                        "description" to realEstate.description,
                        "images" to Gson().toJson(realEstate.images), // Conversion des images
                        "address" to realEstate.address,
                        "pointsOfInterest" to realEstate.pointsOfInterest,
                        "status" to realEstate.status,
                        "marketEntryDate" to realEstate.marketEntryDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        "saleDate" to realEstate.saleDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
                        "realEstateAgent" to realEstate.realEstateAgent,
                        "needsSyncToRoom" to false,
                        "needsSyncToFirestore" to false
                    )

                    // Envoi à Firestore
                    firestore.collection("RealEstate").document(realEstate.id).set(firestoreObject).await()
                    Log.d("Repository", "Synchronization from Room to Firestore succeeded")
                    // Mise à jour du statut de synchronisation dans Room
                    realEstateDao.updateRealEstateSyncStatus(realEstate.id, needsSyncToFirestore = false)
                } catch (e: Exception) {
                    Log.e("Repository", "Error syncing RealEstate ${realEstate.id} to Firestore", e)
                }
            }
        }
    }

    fun getRealEstateByIdFirestore(id: String): Task<DocumentSnapshot> {
        return firestore.collection("RealEstate").document(id).get()
    }

    // Map api

    fun getLatLngFromAddress(address: String): Observable<GeocodeResult> {
        return RealestateApi.retrofitService.getLatLngFromAddress(address, BuildConfig.API_KEY)
    }
}
