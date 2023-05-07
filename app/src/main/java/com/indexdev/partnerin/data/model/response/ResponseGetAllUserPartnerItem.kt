package com.indexdev.partnerin.data.model.response


import com.google.gson.annotations.SerializedName

data class ResponseGetAllUserPartnerItem(
    @SerializedName("id_mitra")
    val idMitra: String,
    @SerializedName("kode_wisata")
    val kodeWisata: String,
    @SerializedName("nama_pemilik")
    val namaPemilik: String,
    @SerializedName("nama_usaha")
    val namaUsaha: String,
    @SerializedName("email_pemilik")
    val emailPemilik: String,
    @SerializedName("no_ponsel")
    val noPonsel: String,
    @SerializedName("jenis_usaha")
    val jenisUsaha: String,
    @SerializedName("gambar")
    val gambar: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("alamat")
    val alamat: String?,
    @SerializedName("hari_buka")
    val hariBuka: String?,
    @SerializedName("jam_buka")
    val jamBuka: String?,
    @SerializedName("jam_tutup")
    val jamTutup: String?,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("longi")
    val longi: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("status")
    val status: String
)