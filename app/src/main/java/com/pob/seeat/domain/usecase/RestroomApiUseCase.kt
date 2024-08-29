package com.pob.seeat.domain.usecase

import com.pob.seeat.domain.model.RestroomModel
import com.pob.seeat.domain.repository.RestroomApiRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class RestroomApiUseCase @Inject constructor(
    private val seoulRestroomApiRepository: RestroomApiRepository,
    // TODO 서울 이외 가능한 추가
    // TODO 나중에 지역이 늘어나면 여러 flow를 combine으로 합쳐봐야 할 듯
    // TODO 로딩 시간 고려해서 자기 근처 위치만 찾을 수 있게 하는 방법도 고려
) {
    suspend operator fun invoke(): Flow<List<RestroomModel>> {
        return seoulRestroomApiRepository.getRestroomData()
    }
}