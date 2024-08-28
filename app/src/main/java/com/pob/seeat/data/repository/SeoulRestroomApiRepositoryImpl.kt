package com.pob.seeat.data.repository

import com.pob.seeat.data.remote.SeoulApiRemoteDataSource
import com.pob.seeat.domain.model.RestroomModel
import com.pob.seeat.domain.repository.RestroomApiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class SeoulRestroomApiRepositoryImpl @Inject constructor(
    seoulApiRemoteDataSource: SeoulApiRemoteDataSource
) : RestroomApiRepository {
    override suspend fun getRestroomData(): Flow<List<RestroomModel>> {
        return flow {  }
    }
}