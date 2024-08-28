package com.pob.seeat.data.remote.response.seoulrestroom


import com.google.gson.annotations.SerializedName

data class DATA(
    @SerializedName("creat_de")
    val creatDe: Long?,
    @SerializedName("gu_nm")
    val guNm: String?,
    @SerializedName("hnr_nam")
    val hnrNam: String?,
    @SerializedName("lat")
    val lat: String?,
    @SerializedName("lng")
    val lng: String?,
    @SerializedName("masterno")
    val masterno: String?,
    @SerializedName("mtc_at")
    val mtcAt: String?,
    @SerializedName("neadres_nm")
    val neadresNm: String?,
    @SerializedName("objectid")
    val objectid: Int?,
    @SerializedName("slaveno")
    val slaveno: String?
)