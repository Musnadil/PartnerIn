package com.indexdev.partnerin.data.api

import com.indexdev.partnerin.data.model.request.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ApiHelper(private val apiService: ApiService) {
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
    ) = apiService.register(
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

    suspend fun emailCheck(email: RequestEmailCheck) = apiService.emailCheck(email)

    suspend fun login(requestLogin: RequestLogin) = apiService.login(requestLogin)

    suspend fun getUserById(id: Int) = apiService.getUserById(id)

    suspend fun editAccount(id: Int, requestEditAccount: RequestEditAccount) =
        apiService.editAccount(id, requestEditAccount)

    suspend fun getAllUserPartner() = apiService.getAllUserPartner()

    suspend fun forgotPassword(email: RequestEmailCheck) = apiService.forgotPassword(email)

    suspend fun verifyOtp(requestVerifyOtp: RequestVerifyOtp) =
        apiService.verifyOtp(requestVerifyOtp)

    suspend fun newPassword(requestNewPassword: RequestNewPassword) =
        apiService.newPassword(requestNewPassword)

    suspend fun getProductByIdMitra(id: Int) = apiService.getProductByIdMitra(id)

    suspend fun addProduct(
        idMitra: Int,
        namaProduk: RequestBody,
        harga: RequestBody,
        satuan: RequestBody,
        deskripsi: RequestBody,
        file: MultipartBody.Part
    ) = apiService.addProduct(
        idMitra = idMitra,
        namaProduk = namaProduk,
        harga = harga,
        satuan = satuan,
        deskripsi = deskripsi,
        file = file
    )

    suspend fun editProduct(
        id: Int,
        idMitra: Int,
        namaProduk: RequestBody,
        harga: RequestBody,
        satuan: RequestBody,
        deskripsi: RequestBody,
        file: MultipartBody.Part?,
        gambarLama: RequestBody
    ) = apiService.editProduct(
        id = id,
        idMitra = idMitra,
        namaProduk = namaProduk,
        harga = harga,
        satuan = satuan,
        deskripsi = deskripsi,
        file = file,
        gambarLama = gambarLama
    )

    suspend fun getProductById(id: Int) = apiService.getProductById(id)

    suspend fun deleteProduct(id: Int) = apiService.deleteProduct(id)

    suspend fun getMarkerByIdTour(id: Int) = apiService.getMarkerByIdTour(id)

    suspend fun getMarkerById(id: Int) = apiService.getMarkerById(id)

    suspend fun addMarker(requestMarker: RequestMarker) = apiService.addMarker(requestMarker)

    suspend fun editMarker(id: Int, requestMarker: RequestMarker) =
        apiService.editMarker(id, requestMarker)

    suspend fun deleteMarker(id: Int) = apiService.deleteMarker(id)

}