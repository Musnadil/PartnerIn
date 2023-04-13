package com.indexdev.partnerin.data.model.response


import com.google.gson.annotations.SerializedName

data class ResponseUserMitraById(
    @SerializedName("code")
    val code: Int,
    @SerializedName("user_mitra_by_id")
    val userMitraById: UserMitraById
)