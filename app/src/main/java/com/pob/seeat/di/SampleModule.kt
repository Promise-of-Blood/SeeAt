package com.pob.seeat.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
// 싱글톤의 경우(ex. )
@InstallIn(SingletonComponent::class)
object SampleModule {

    // 인터페이스에 추가 시
//    @Binds
    // 다른 클래스에 추가 시
    @Provides
    fun provide() : Int {
        return 0
    }
}