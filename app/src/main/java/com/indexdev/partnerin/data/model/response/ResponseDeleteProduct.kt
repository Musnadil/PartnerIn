package com.indexdev.partnerin.data.model.response


import com.google.gson.annotations.SerializedName

data class ResponseDeleteProduct(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String
)