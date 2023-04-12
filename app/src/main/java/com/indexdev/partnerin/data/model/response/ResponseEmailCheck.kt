package com.indexdev.partnerin.data.model.response


import com.google.gson.annotations.SerializedName

data class ResponseEmailCheck(
    @SerializedName("message")
    val message: String
)