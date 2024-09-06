package com.pob.seeat.di

import com.google.firebase.ktx.Firebase
import com.pob.seeat.data.remote.ChatRemote
import com.pob.seeat.data.repository.ChatRepositoryImpl
import com.pob.seeat.data.repository.SampleRepositoryImpl
import com.pob.seeat.data.repository.SeoulRestroomApiRepositoryImpl
import com.pob.seeat.domain.repository.ChatRepository
import com.pob.seeat.domain.repository.RestroomApiRepository
import com.pob.seeat.domain.repository.SampleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChatRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository

}

@Module
@InstallIn(SingletonComponent::class)
object ChatRemoteModule {

    @Singleton
    @Provides
    fun provideChatRemote() : ChatRemote {
        return ChatRemote(firebase = Firebase)
    }

}