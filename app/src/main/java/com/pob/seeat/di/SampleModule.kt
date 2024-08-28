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
