package com.pob.seeat.data.remote.response.test


import com.google.gson.annotations.SerializedName

data class  GeoInfoPublicToiletWGS(
    @SerializedName("list_total_count")
    val listTotalCount: Int?,
    @SerializedName("RESULT")
    val rESULT: RESULT?,
    @SerializedName("row")
    val row: List<Row?>?
)