package com.pob.seeat.data.remote

import dagger.Provides
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Named

interface SampleRemoteDataSource {
    @GET("/v2/search/image")
    suspend fun getSearchImage(
        @Query("query") query: String,
        @Query("sort") sort: String = "accuracy",
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10,
    ): SampleResponse<ImageDocumentResponse>

    @GET("v2/search/vclip")
    suspend fun getSearchVideo(
        @Query("query") query: String,
        @Query("sort") sort: String = "accuracy",
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): SampleResponse<VideoDocumentResponse>

}