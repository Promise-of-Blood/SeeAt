package com.pob.seeat.data.repository

import com.pob.seeat.data.remote.SeoulApiRemoteDataSource
import com.pob.seeat.data.remote.response.seoulrestroom.DATA
import com.pob.seeat.data.remote.response.test.Row
import com.pob.seeat.domain.model.RestroomModel
import com.pob.seeat.domain.repository.RestroomApiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeoulRestroomApiRepositoryImpl @Inject constructor(
    private val seoulApiRemoteDataSource: SeoulApiRemoteDataSource
) : RestroomApiRepository {
    override suspend fun getRestroomData(): Flow<List<RestroomModel>> {
        return flow { emit(seoulApiRemoteDataSource.getSeoulRestroom().geoInfoPublicToiletWGS?.row.toRestroomModel()) }
    }
}

fun List<Row?>?.toRestroomModel(): List<RestroomModel> {
    return this?.map {
        RestroomModel(
            it?.lAT,
            it?.lNG
        )
    } ?: listOf()
}