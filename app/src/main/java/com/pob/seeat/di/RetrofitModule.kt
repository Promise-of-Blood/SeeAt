package com.pob.seeat.di

import com.pob.seeat.data.remote.SampleRemoteDataSource
import com.pob.seeat.data.remote.SeoulApiRemoteDataSource
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

//    private const val SAMPLE_BASE_URL = "https://dapi.kakao.com"
    private const val SEOUL_RESTROOM_URL = "http://openAPI.seoul.go.kr:8088"

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

//    @Singleton
//    @Provides
//    @SampleBaseUrl
//    fun provideSampleUrl() : String = SAMPLE_BASE_URL

    @Singleton
    @Provides
    fun provideRetrofit (okHttpClient: OkHttpClient, @SeoulRestroomUrl url: String): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(url)
            .build()
    }

    // TODO 지금은 Seoul만 받지만, 나중을 고려해, DataSource의 부모도 만들 필요가 있어보임
    @Provides
    @Singleton
    fun provideSampleRemoteDataSource (retrofit: Retrofit): SampleRemoteDataSource {
        return retrofit.create(SampleRemoteDataSource::class.java)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource (retrofit: Retrofit): SeoulApiRemoteDataSource {
        return retrofit.create(SeoulApiRemoteDataSource::class.java)
    }

}