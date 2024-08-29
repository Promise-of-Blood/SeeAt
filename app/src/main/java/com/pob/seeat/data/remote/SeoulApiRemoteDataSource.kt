package com.pob.seeat.data.remote

import com.pob.seeat.data.remote.response.seoulrestroom.SeoulRestroomResponse
import retrofit2.http.GET

interface SeoulApiRemoteDataSource {
    @GET("/454172616d61346333395577754656/json/GeoInfoPublicToiletWGS/1/100")
    suspend fun getSeoulRestroom(): SeoulRestroomResponse
}