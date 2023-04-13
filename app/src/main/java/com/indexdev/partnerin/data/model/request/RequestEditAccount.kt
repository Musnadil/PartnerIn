package com.indexdev.partnerin.data.model.request


import com.google.gson.annotations.SerializedName

data class RequestEditAccount(
    @SerializedName("kode_wisata")
    val kodeWisata: Int,
    @SerializedName("nama_usaha")
    val namaUsaha: String,
    @SerializedName("email_pemilik")
    val emailPemilik: String,
    @SerializedName("no_ponsel")
    val noPonsel: String,
    @SerializedName("alamat")
    val alamat: String?,
    @SerializedName("hari_buka")
    val hariBuka: String?,
    @SerializedName("jam_buka")
    val jamBuka: String?,
    @SerializedName("jam_tutup")
    val jamTutup: String?,
    @SerializedName("status")
    val status: String
)