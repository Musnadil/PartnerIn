package com.indexdev.partnerin.data

import com.indexdev.partnerin.data.api.ApiHelper
import com.indexdev.partnerin.data.model.request.RequestEmailCheck
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository(private val apiHelper: ApiHelper) {
    suspend fun register(
        kodeWisata: Int,
        namaPemilik: RequestBody,
        namaUsaha: RequestBody,
        emailPemilik: RequestBody,
        noPonsel: RequestBody,
        jenisUsaha: RequestBody,
        file: MultipartBody.Part,
        password: RequestBody,
        alamat: RequestBody,
        hariBuka: RequestBody,
        jamBuka: RequestBody,
        jamTutup: RequestBody,
        lat: Float,
        longi: Float,
        role: Int,
        status: RequestBody,
    ) = apiHelper.register(
        kodeWisata = kodeWisata,
        namaPemilik = namaPemilik,
        namaUsaha = namaUsaha,
        emailPemilik = emailPemilik,
        noPonsel = noPonsel,
        jenisUsaha = jenisUsaha,
        file = file,
        password = password,
        alamat = alamat,
        hariBuka = hariBuka,
        jamBuka = jamBuka,
        jamTutup = jamTutup,
        lat = lat,
        longi = longi,
        role = role,
        status = status
    )

    suspend fun emailCheck(email: RequestEmailCheck) = apiHelper.emailCheck(email)
}