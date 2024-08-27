package com.pob.seeat.data.repository

import com.pob.seeat.data.remote.SampleRemoteDataSource
import com.pob.seeat.domain.model.SampleModel
import com.pob.seeat.domain.model.toImageUser
import com.pob.seeat.domain.model.toVideoUser
import com.pob.seeat.domain.repository.SampleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SampleRepositoryImpl @Inject constructor(private val sampleRemoteDataSource: SampleRemoteDataSource) :
    SampleRepository {
    override suspend fun searchUserImageList(query: String): Flow<List<SampleModel>> {
        return flow { emit(toImageUser(sampleRemoteDataSource.getSearchImage(query).documents.orEmpty())) }
    }

    override suspend fun searchUserVideoList(query: String): Flow<List<SampleModel>> {
        return flow { emit(toVideoUser(sampleRemoteDataSource.getSearchVideo(query).documents.orEmpty())) }
    }
}