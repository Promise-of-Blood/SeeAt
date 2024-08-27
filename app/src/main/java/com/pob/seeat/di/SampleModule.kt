package com.pob.seeat.di

import android.content.Context
import com.pob.seeat.data.remote.SampleRemoteDataSource
import com.pob.seeat.data.repository.SampleRepositoryImpl
import com.pob.seeat.domain.repository.SampleRepository
import com.pob.seeat.network.AuthorizationInterceptor
import com.pob.seeat.network.RetrofitClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
object SampleModuleRetrofitModule {

    private const val SAMPLE_BASE_URL = "https://dapi.kakao.com"

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
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(SAMPLE_BASE_URL)
            .build()
    }

    fun provideSampleRemoteDataSource(retrofit: Retrofit): SampleRemoteDataSource {
        return retrofit.create(SampleRemoteDataSource::class.java)
    }

}

