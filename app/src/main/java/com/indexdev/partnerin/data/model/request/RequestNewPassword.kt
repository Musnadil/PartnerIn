package com.indexdev.partnerin.data.model.request


import com.google.gson.annotations.SerializedName

data class RequestNewPassword(
    @SerializedName("email_pemilik")
    val emailPemilik: String,
    @SerializedName("password")
    val password: String
)