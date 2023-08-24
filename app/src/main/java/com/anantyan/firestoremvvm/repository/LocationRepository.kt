package com.anantyan.firestoremvvm.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.anantyan.firestoremvvm.utils.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

interface LocationPresenter {
    suspend fun isUserAtSpecificLocation(): Flow<Resource<Boolean>>
}

class LocationRepository @Inject constructor(
    private val geoLocation: FusedLocationProviderClient
) : LocationPresenter {
    @SuppressLint("MissingPermission")
    override suspend fun isUserAtSpecificLocation(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        val priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val point = geoLocation.getCurrentLocation(priority, CancellationTokenSource().token).await()
        val nearbyLocation = dummyLocation().map {
            val distance = haversine(point.latitude, point.longitude, it.latitude, it.longitude) / 1000 // 1km
            it.distance = distance
            it
        }.filter { it.distance <= 0.05 } // 50m
        emit(Resource.Success(nearbyLocation.isEmpty()))
    }.catch { error(Resource.Error<Boolean>(it.message)) }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c * 1000
    }

    private fun dummyLocation() = mutableListOf(
        com.anantyan.firestoremvvm.model.Location(
            name = "Rumah",
            latitude = -7.392098,
            longitude = 109.233921
        ),
        com.anantyan.firestoremvvm.model.Location(
            name = "Kampus",
            latitude = -7.400704,
            longitude = 109.231162
        ),
        com.anantyan.firestoremvvm.model.Location(
            name = "Java Heritage",
            latitude = -7.415600,
            longitude = 109.238748
        )
    )
}