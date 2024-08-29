package com.pob.seeat.domain.repository

import com.pob.seeat.domain.model.RestroomModel
import kotlinx.coroutines.flow.Flow

interface RestroomApiRepository {
    suspend fun getRestroomData(): Flow<List<RestroomModel>>
}