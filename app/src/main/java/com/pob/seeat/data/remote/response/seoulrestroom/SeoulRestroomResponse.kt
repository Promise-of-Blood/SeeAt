package com.pob.seeat.data.remote.response.seoulrestroom


import com.google.gson.annotations.SerializedName

data class SeoulRestroomResponse(
    @SerializedName("DATA")
    val dATA: List<DATA?>?,
    @SerializedName("DESCRIPTION")
    val dESCRIPTION: DESCRIPTION?
)