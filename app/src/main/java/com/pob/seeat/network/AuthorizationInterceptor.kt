package com.pob.seeat.network

import com.pob.seeat.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val kakaoApiKey = BuildConfig.KAKAO_APP_KEY
        val newRequest = chain.request().newBuilder()
            .addHeader(
                "Authorization",
                "KakaoAK %s".format(kakaoApiKey),
            )
            .build()
        return chain.proceed(newRequest)
    }
}