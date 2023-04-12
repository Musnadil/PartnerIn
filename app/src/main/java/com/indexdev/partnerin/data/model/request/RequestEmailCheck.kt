package com.indexdev.partnerin.data.model.request

import com.google.gson.annotations.SerializedName

data class RequestEmailCheck(
    @SerializedName("email_pemilik")
    val email: String
)