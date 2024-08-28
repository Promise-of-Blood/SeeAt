package com.pob.seeat.domain.usecase

import com.pob.seeat.domain.repository.RestroomApiRepository
import javax.inject.Inject

// TODO 나중에 지역이 늘어나면 여러 flow를 combine으로 합쳐봐야 할 듯
class RestroomApiUseCase @Inject constructor(
    seoulRestroomApiRepository: RestroomApiRepository,
    // TODO 서울 이외 가능한 추가
) {

}