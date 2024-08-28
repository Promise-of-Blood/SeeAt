package com.pob.seeat.utils

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.pob.seeat.BuildConfig
import javax.inject.Inject

class SeeAtApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)

    }
}

