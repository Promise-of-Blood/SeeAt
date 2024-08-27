package com.pob.seeat

import com.pob.seeat.di.Dummy
import javax.inject.Inject

class PresentLogRepository @Inject constructor(
    private val dummy: Dummy
){
    val presentLogData = dummy.log()
}