package com.indexdev.partnerin.data.model.response


import com.google.gson.annotations.SerializedName

data class ResponseLogin(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("id_mitra")
    val idMitra: String,
    @SerializedName("role")
    val role: String
)