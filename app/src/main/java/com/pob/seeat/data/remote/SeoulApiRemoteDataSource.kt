package com.pob.seeat.data.remote

import com.pob.seeat.data.remote.response.test.SeoulRestroomResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SeoulApiRemoteDataSource {
    @GET("/454172616d61346333395577754656/json/GeoInfoPublicToiletWGS/1/100")
    suspend fun getSeoulRestroom(): SeoulRestroomResponse
}