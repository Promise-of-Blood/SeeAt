package com.pob.seeat.data.remote

import com.pob.seeat.data.remote.response.seoulrestroom.SeoulRestroomResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SeoulApiRemoteDataSource {
    @GET
    suspend fun getSeoulRestroom(
        @Query("KEY") key: String,
        @Query("TYPE") type: String,
        @Query("SERVICE") service: String,
        @Query("START_INDEX") startIndex: Int,
        @Query("END_INDEX") endIndex: Int,
    ) : SeoulRestroomResponse
}