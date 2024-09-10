package com.pob.seeat.domain.repository

import com.pob.seeat.domain.model.SampleModel
import kotlinx.coroutines.flow.Flow

interface SampleRepository {
    suspend fun searchUserImageList(query: String): Flow<List<SampleModel>>
    suspend fun searchUserVideoList(query: String): Flow<List<SampleModel>>
}