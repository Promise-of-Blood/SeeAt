package com.pob.seeat.di

import com.google.firebase.ktx.Firebase
import com.pob.seeat.data.remote.chat.ChatsRemote
import com.pob.seeat.data.remote.chat.MessagesRemote
import com.pob.seeat.data.remote.chat.UsersRemote
import com.pob.seeat.data.repository.ChatListRepositoryImpl
import com.pob.seeat.data.repository.ChatRepositoryImpl
import com.pob.seeat.data.repository.SampleRepositoryImpl
import com.pob.seeat.data.repository.SeoulRestroomApiRepositoryImpl
import com.pob.seeat.domain.repository.ChatListRepository
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
abstract class ChatListRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindChatRepository(
        chatListRepositoryImpl: ChatListRepositoryImpl
    ): ChatListRepository

}

@Module
@InstallIn(SingletonComponent::class)
object ChatsRemoteModule {

    @Singleton
    @Provides
    fun provideChatsRemote() : ChatsRemote {
        return ChatsRemote()
    }

    @Singleton
    @Provides
    fun provideUsersRemote() : UsersRemote {
        return UsersRemote()
    }

    @Singleton
    @Provides
    fun provideMessagesRemote() : MessagesRemote {
        return MessagesRemote()
    }

}