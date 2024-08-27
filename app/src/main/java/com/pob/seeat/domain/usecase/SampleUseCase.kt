package com.pob.seeat.domain.usecase

import com.pob.seeat.domain.model.SampleModel
import com.pob.seeat.domain.repository.SampleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSampleImageListUseCase @Inject constructor(private val repository: SampleRepository) {
    suspend fun execute(
        query: String
    ): Flow<List<SampleModel>> {
        return repository.searchUserImageList(query)
    }

    suspend operator fun invoke(
        query: String
    ): Flow<List<SampleModel>> {
        return repository.searchUserImageList(query)
    }
}

class GetSampleVideoListUseCase @Inject constructor(private val repository: SampleRepository) {
    suspend fun execute(
        query: String
    ): Flow<List<SampleModel>> {
        return repository.searchUserVideoList(query)
    }

    suspend operator fun invoke(
        query: String
    ): Flow<List<SampleModel>> {
        return repository.searchUserVideoList(query)
    }
}