package com.pob.seeat.utils

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object GetUserLocation {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    suspend fun getCurrentLocation(context: Context): GeoPoint? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        return suspendCoroutine { continuation ->
            if (ActivityCompat.checkSelfPermission(
                    context,
                    ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 권한이 없을 경우 null 반환
                Timber.e("위치 권한이 없습니다.")
                continuation.resume(null)
                return@suspendCoroutine
            }

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val geoPoint = GeoPoint(location.latitude, location.longitude)
                    continuation.resume(geoPoint)
                } else {
                    // 위치 정보가 없는 경우 null 반환
                    Timber.e("현재 위치를 가져올 수 없습니다.")
                    continuation.resume(null)
                }
            }.addOnFailureListener { exception ->
                // 에러 발생 시 null 반환
                Timber.e("위치 정보를 가져오는 중 오류 발생: $exception")
                continuation.resume(null)
            }
        }

    }
}