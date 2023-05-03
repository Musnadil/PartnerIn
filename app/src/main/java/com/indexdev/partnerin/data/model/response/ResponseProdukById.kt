package com.indexdev.partnerin.data.model.response


import com.google.gson.annotations.SerializedName

data class ResponseProdukById(
    @SerializedName("id_produk")
    val idProduk: String,
    @SerializedName("id_mitra")
    val idMitra: String,
    @SerializedName("nama_produk")
    val namaProduk: String,
    @SerializedName("harga")
    val harga: String,
    @SerializedName("satuan")
    val satuan: String,
    @SerializedName("deskripsi")
    val deskripsi: String,
    @SerializedName("gambar")
    val gambar: String
)