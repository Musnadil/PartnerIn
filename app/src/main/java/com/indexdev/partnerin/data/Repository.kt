package com.indexdev.partnerin.data

import com.indexdev.partnerin.data.api.ApiHelper
import com.indexdev.partnerin.data.model.request.*
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

    suspend fun login(requestLogin: RequestLogin) = apiHelper.login(requestLogin)

    suspend fun getUserById(id: Int) = apiHelper.getUserById(id)

    suspend fun editAccount(id: Int, requestEditAccount: RequestEditAccount) =
        apiHelper.editAccount(id, requestEditAccount)

    suspend fun getAllUserPartner() = apiHelper.getAllUserPartner()

    suspend fun forgotPassword(email: RequestEmailCheck) = apiHelper.forgotPassword(email)

    suspend fun verifyOtp(requestVerifyOtp: RequestVerifyOtp) =
        apiHelper.verifyOtp(requestVerifyOtp)

    suspend fun newPassword(requestNewPassword: RequestNewPassword) =
        apiHelper.newPassword(requestNewPassword)

    suspend fun getProductByIdMitra(id: Int) = apiHelper.getProductByIdMitra(id)

    suspend fun addProduct(
        idMitra: Int,
        namaProduk: RequestBody,
        harga: RequestBody,
        satuan: RequestBody,
        deskripsi: RequestBody,
        file: MultipartBody.Part
    ) = apiHelper.addProduct(
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
    ) = apiHelper.editProduct(
        id = id,
        idMitra = idMitra,
        namaProduk = namaProduk,
        harga = harga,
        satuan = satuan,
        deskripsi = deskripsi,
        file = file,
        gambarLama = gambarLama
    )

    suspend fun getProductById(id: Int) = apiHelper.getProductById(id)

    suspend fun deleteProduct(id: Int) = apiHelper.deleteProduct(id)

}