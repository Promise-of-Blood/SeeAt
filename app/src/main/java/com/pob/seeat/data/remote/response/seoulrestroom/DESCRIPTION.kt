package com.pob.seeat.data.remote.response.seoulrestroom


import com.google.gson.annotations.SerializedName

data class DESCRIPTION(
    @SerializedName("CREAT_DE")
    val cREATDE: String?,
    @SerializedName("GU_NM")
    val gUNM: String?,
    @SerializedName("HNR_NAM")
    val hNRNAM: String?,
    @SerializedName("LAT")
    val lAT: String?,
    @SerializedName("LNG")
    val lNG: String?,
    @SerializedName("MASTERNO")
    val mASTERNO: String?,
    @SerializedName("MTC_AT")
    val mTCAT: String?,
    @SerializedName("NEADRES_NM")
    val nEADRESNM: String?,
    @SerializedName("OBJECTID")
    val oBJECTID: String?,
    @SerializedName("SLAVENO")
    val sLAVENO: String?
)