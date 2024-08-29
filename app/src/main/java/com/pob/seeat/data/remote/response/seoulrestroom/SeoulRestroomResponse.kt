package com.pob.seeat.data.remote.response.seoulrestroom


import com.google.gson.annotations.SerializedName

data class SeoulRestroomResponse(
    @SerializedName("GeoInfoPublicToiletWGS")
    val geoInfoPublicToiletWGS: GeoInfoPublicToiletWGS?
)