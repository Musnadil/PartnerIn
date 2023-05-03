package com.indexdev.partnerin.data.model.response


import com.google.gson.annotations.SerializedName

data class ResponseEditProduct(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String
)