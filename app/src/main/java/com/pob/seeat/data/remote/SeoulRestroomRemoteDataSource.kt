package com.pob.seeat.data.remote

import retrofit2.http.GET

interface SeoulRestroomRemoteDataSource {
    @GET()
    suspend fun getSeoulRestroom() {

    }
}