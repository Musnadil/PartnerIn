package com.indexdev.partnerin.data.model.request


import com.google.gson.annotations.SerializedName

data class RequestLogin(
    @SerializedName("email_pemilik")
    val emailPemilik: String,
    @SerializedName("password")
    val password: String
)