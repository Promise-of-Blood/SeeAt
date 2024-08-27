package com.pob.seeat.di

import android.content.Context
import com.pob.seeat.data.repository.SampleRepositoryImpl
import com.pob.seeat.domain.repository.SampleRepository
import com.pob.seeat.network.AuthorizationInterceptor
import com.pob.seeat.network.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton


class Dummy(private val name: String) {
    fun log(): String {
        return name
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object FavoriteRepositoryModule {

    @ViewModelScoped
    @Provides
    fun provideFavoriteRepository(
        @ApplicationContext context: Context
    ) : SampleRepository = SampleRepositoryImpl(RetrofitClient.searchRemoteDataSource)
}

@Module
@InstallIn(SingletonComponent::class)
object YoutubeApiModule {

    private const val YOUTUBE_BASE_URL = "https://www.googleapis.com/youtube/v3/"

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor())
            .addNetworkInterceptor(interceptor)
            .build()
    }
}

