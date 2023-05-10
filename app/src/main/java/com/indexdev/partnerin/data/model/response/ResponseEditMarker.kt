package com.indexdev.partnerin.data.model.response


import com.google.gson.annotations.SerializedName

data class ResponseEditMarker(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String
)