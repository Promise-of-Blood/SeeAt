package com.pob.seeat.di

import com.pob.seeat.data.remote.SampleRemoteDataSource
import com.pob.seeat.network.AuthorizationInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class SampleBaseUrl

@Qualifier
annotation class SeoulRestroomUrl

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    private const val SAMPLE_BASE_URL = "https://dapi.kakao.com"
    private const val SEOUL_RESTROOM_URL = "https://openAPI.seoul.go.kr:8088/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor())
            .addNetworkInterceptor(interceptor)
            .build()
    }

    @Singleton
    @Provides
    @SeoulRestroomUrl
    fun provideSeoulRestroomUrl() : String = SEOUL_RESTROOM_URL

    @Singleton
    @Provides
    @SampleBaseUrl
    fun provideSampleUrl() : String = SAMPLE_BASE_URL

    @Singleton
    @Provides
    fun provideRetrofit (okHttpClient: OkHttpClient, @SeoulRestroomUrl url: String): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(url)
            .build()
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource (retrofit: Retrofit): SampleRemoteDataSource {
        return retrofit.create(SampleRemoteDataSource::class.java)
    }

}