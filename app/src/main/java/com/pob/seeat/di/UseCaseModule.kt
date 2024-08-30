package com.pob.seeat.di

import com.pob.seeat.data.remote.UserInfoSource
import com.pob.seeat.data.repository.UserInfoRepositoryImpl
import com.pob.seeat.data.remote.UserInfoSourceImpl
import com.pob.seeat.domain.repository.RestroomApiRepository
import com.pob.seeat.domain.repository.SampleRepository
import com.pob.seeat.domain.repository.UserInfoRepository
import com.pob.seeat.domain.usecase.CreateUserInfoUseCase
import com.pob.seeat.domain.usecase.DeleteUserInfoUseCase
import com.pob.seeat.domain.usecase.GetSampleImageListUseCase
import com.pob.seeat.domain.usecase.GetSampleVideoListUseCase
import com.pob.seeat.domain.usecase.GetUserInfoUseCase
import com.pob.seeat.domain.usecase.RestroomApiUseCase
import com.pob.seeat.domain.usecase.UpdateUserInfoUseCase
import com.pob.seeat.domain.usecase.UserInfoUseCases
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
    fun provideGetSampleVideoListUseCase(repository: SampleRepository): GetSampleVideoListUseCase{
        return GetSampleVideoListUseCase(repository)
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object RestroomUseCaseModule {
    @Provides
    fun provideRestroomUseCase(restroomApiRepository: RestroomApiRepository) : RestroomApiUseCase {
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
            getUserInfoUseCase = GetUserInfoUseCase(repository)
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