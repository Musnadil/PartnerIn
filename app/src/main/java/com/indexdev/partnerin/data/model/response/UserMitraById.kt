package com.indexdev.partnerin.data.model.response


import com.google.gson.annotations.SerializedName

data class UserMitraById(
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
    @SerializedName("alamat")
    val alamat: Any?,
    @SerializedName("hari_buka")
    val hariBuka: Any?,
    @SerializedName("jam_buka")
    val jamBuka: Any?,
    @SerializedName("jam_tutup")
    val jamTutup: Any?,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("longi")
    val longi: String
)