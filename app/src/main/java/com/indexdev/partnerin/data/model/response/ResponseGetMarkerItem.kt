package com.indexdev.partnerin.data.model.response


import com.google.gson.annotations.SerializedName

data class ResponseGetMarkerItem(
    @SerializedName("id_poi")
    val idPoi: String,
    @SerializedName("kode_fasilitas")
    val kodeFasilitas: String,
    @SerializedName("kode_wisata")
    val kodeWisata: String,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("longi")
    val longi: String,
    @SerializedName("nama_fasilitas")
    val namaFasilitas: String
)