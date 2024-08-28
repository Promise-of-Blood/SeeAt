package com.pob.seeat.di

import com.pob.seeat.data.remote.SampleRemoteDataSource
import com.pob.seeat.data.repository.SampleRepositoryImpl
import com.pob.seeat.domain.repository.SampleRepository
import com.pob.seeat.network.AuthorizationInterceptor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SampleRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindSampleRepository(
        sampleRepositoryImpl: SampleRepositoryImpl
    ): SampleRepository

}

@Module
@InstallIn(SingletonComponent::class)
abstract class UrlModule {

    @Singleton
    @Binds
    abstract fun bindUrl(
        @Named("url_seoul_restroom") url: String
    ): String

}

@Module
@InstallIn(SingletonComponent::class)
object SampleModuleRetrofitModule {

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
    @Named("url_seoul_restroom")
    fun provideSeoulRestroomUrl() : String = SEOUL_RESTROOM_URL

    @Singleton
    @Provides
    @Named("url_sample")
    fun provideSampleUrl() : String = SAMPLE_BASE_URL

    @Singleton
    @Provides
    fun provideRetrofit (okHttpClient: OkHttpClient, @Named("url_seoul_restroom") url: String): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(url)
            .build()
    }

    @Provides
    @Singleton
    fun provideSampleRemoteDataSource (retrofit: Retrofit): SampleRemoteDataSource {
        return retrofit.create(SampleRemoteDataSource::class.java)
    }

}
