package com.indexdev.partnerin.data.model.response


import com.google.gson.annotations.SerializedName

data class ResponseMarkerById(
    @SerializedName("code")
    val code: Int,
    @SerializedName("poi_by_id")
    val poiById: PoiById
)