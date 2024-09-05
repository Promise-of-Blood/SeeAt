package com.pob.seeat.di

import com.pob.seeat.data.remote.CommentSource
import com.pob.seeat.data.remote.CommentSourceImpl
import com.pob.seeat.data.remote.UserInfoSource
import com.pob.seeat.data.remote.UserInfoSourceImpl
import com.pob.seeat.data.repository.CommentRepositoryImpl
import com.pob.seeat.data.repository.UserInfoRepositoryImpl
import com.pob.seeat.domain.repository.CommentRepository
import com.pob.seeat.domain.repository.RestroomApiRepository
import com.pob.seeat.domain.repository.SampleRepository
import com.pob.seeat.domain.repository.UserHistoryRepository
import com.pob.seeat.domain.repository.UserInfoRepository
import com.pob.seeat.domain.usecase.CommentUseCases
import com.pob.seeat.domain.usecase.CreateCommentUseCases
import com.pob.seeat.domain.usecase.CreateLikedFeed
import com.pob.seeat.domain.usecase.CreateUserInfoUseCase
import com.pob.seeat.domain.usecase.DeleteCommentUseCases
import com.pob.seeat.domain.usecase.DeleteUserInfoUseCase
import com.pob.seeat.domain.usecase.EditCommentUseCases
import com.pob.seeat.domain.usecase.GetCommentListUseCases
import com.pob.seeat.domain.usecase.GetSampleImageListUseCase
import com.pob.seeat.domain.usecase.GetSampleVideoListUseCase
import com.pob.seeat.domain.usecase.GetUserInfoByEmailUseCase
import com.pob.seeat.domain.usecase.GetUserInfoUseCase
import com.pob.seeat.domain.usecase.RestroomApiUseCase
import com.pob.seeat.domain.usecase.UpdateUserInfoUseCase
import com.pob.seeat.domain.usecase.UserCommentHistoryUseCase
import com.pob.seeat.domain.usecase.UserFeedHistoryUseCase
import com.pob.seeat.domain.usecase.UserHistoryUseCases
import com.pob.seeat.domain.usecase.UserInfoUseCases
import com.pob.seeat.domain.usecase.UserLikedHistoryUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetSampleImageListUseCase(repository: SampleRepository): GetSampleImageListUseCase {
        return GetSampleImageListUseCase(repository)
    }

    @Provides
    fun provideGetSampleVideoListUseCase(repository: SampleRepository): GetSampleVideoListUseCase {
        return GetSampleVideoListUseCase(repository)
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object RestroomUseCaseModule {
    @Provides
    fun provideRestroomUseCase(restroomApiRepository: RestroomApiRepository): RestroomApiUseCase {
        return RestroomApiUseCase(restroomApiRepository)
    }
}


@Module
@InstallIn(SingletonComponent::class)
object UserUseCaseModule {

    @Provides
    @Singleton
    fun provideUserInfoUseCases(repository: UserInfoRepository): UserInfoUseCases {
        return UserInfoUseCases(
            createUserInfoUseCase = CreateUserInfoUseCase(repository),
            updateUserInfoUseCase = UpdateUserInfoUseCase(repository),
            deleteUserInfoUseCase = DeleteUserInfoUseCase(repository),
            getUserInfoUseCase = GetUserInfoUseCase(repository),
            getUserInfoByEmailUseCase = GetUserInfoByEmailUseCase(repository),
            createLikedFeed = CreateLikedFeed(repository)
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserInfoRepository(
        userInfoRepositoryImpl: UserInfoRepositoryImpl
    ): UserInfoRepository
}


@Module
@InstallIn(SingletonComponent::class)
abstract class UserInfoSourceModule {

    @Binds
    @Singleton
    abstract fun bindUserInfoSource(
        userInfoSourceImpl: UserInfoSourceImpl
    ): UserInfoSource
}

@Module
@InstallIn(SingletonComponent::class)
object UserHistoryUseCaseModule {

    @Provides
    @Singleton
    fun provideUserInfoUseCases(
        userInfoRepository: UserInfoRepository,
        userHistoryRepository: UserHistoryRepository,
    ): UserHistoryUseCases {
        return UserHistoryUseCases(
            userFeedHistoryUseCase = UserFeedHistoryUseCase(
                userInfoRepository,
                userHistoryRepository
            ),
            userCommentHistoryUseCase = UserCommentHistoryUseCase(
                userInfoRepository,
                userHistoryRepository
            ),
            userLikedHistoryUseCase = UserLikedHistoryUseCase(
                userInfoRepository,
                userHistoryRepository
            )
        )
    }
}


@Module
@InstallIn(SingletonComponent::class)
abstract class CommentModule {

    // CommentRepository의 구현체로 CommentRepositoryImpl을 제공
    @Binds
    @Singleton
    abstract fun bindCommentRepository(
        commentRepositoryImpl: CommentRepositoryImpl
    ): CommentRepository

    // CommentSource의 구현체로 CommentSourceImpl을 제공
    @Binds
    @Singleton
    abstract fun bindCommentSource(
        commentSourceImpl: CommentSourceImpl
    ): CommentSource

    companion object {
        @Provides
        @Singleton
        fun provideCommentUseCases(repository: CommentRepository): CommentUseCases {
            return CommentUseCases(
                createCommentUseCases = CreateCommentUseCases(repository),
                getCommentListUsesCases = GetCommentListUseCases(repository),
                deleteCommentUseCases = DeleteCommentUseCases(repository),
                editCommentUseCases = EditCommentUseCases(repository)
            )
        }
    }
}

