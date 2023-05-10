package com.indexdev.partnerin.data.api

import com.indexdev.partnerin.data.model.request.*
import com.indexdev.partnerin.data.model.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("registermitra")
    suspend fun register(
        @Part("kode_wisata") kodeWisata: Int,
        @Part("nama_pemilik") namaPemilik: RequestBody?,
        @Part("nama_usaha") namaUsaha: RequestBody?,
        @Part("email_pemilik") emailPemilik: RequestBody?,
        @Part("no_ponsel") noPonsel: RequestBody?,
        @Part("jenis_usaha") jenisUsaha: RequestBody?,
        @Part file: MultipartBody.Part,
        @Part("password") password: RequestBody?,
        @Part("alamat") alamat: RequestBody?,
        @Part("hari_buka") hariBuka: RequestBody?,
        @Part("jam_buka") jamBuka: RequestBody?,
        @Part("jam_tutup") jamTutup: RequestBody?,
        @Part("lat") lat: Float,
        @Part("longi") longi: Float,
        @Part("role") role: Int,
        @Part("status") status: RequestBody?,
    ): ResponseRegister

    @POST("emailcheck")
    suspend fun emailCheck(@Body email: RequestEmailCheck): ResponseEmailCheck

    @POST("loginmitra")
    suspend fun login(@Body requestLogin: RequestLogin): ResponseLogin

    @GET("usermitra/{id}")
    suspend fun getUserById(@Path("id") id: Int): ResponseUserMitraById

    @PUT("usermitra/{id}")
    suspend fun editAccount(
        @Path("id") id: Int,
        @Body requestEditAccount: RequestEditAccount
    ): ResponseEditAccount

    @GET("usermitra")
    suspend fun getAllUserPartner(): Response<List<ResponseGetAllUserPartnerItem>>

    @POST("forgotpassword")
    suspend fun forgotPassword(@Body email: RequestEmailCheck): ResponseForgotPassword

    @POST("verifyotp")
    suspend fun verifyOtp(@Body requestVerifyOtp: RequestVerifyOtp): ResponseVerifyOtp

    @POST("newpassword")
    suspend fun newPassword(@Body requestNewPassword: RequestNewPassword): ResponseNewPassword

    // product
    @GET("produkByIdMitra/{id}")
    suspend fun getProductByIdMitra(@Path("id") id: Int): Response<List<ResponseProductByIdMitra>>

    @Multipart
    @POST("produk")
    suspend fun addProduct(
        @Part("id_mitra") idMitra: Int,
        @Part("nama_produk") namaProduk: RequestBody?,
        @Part("harga") harga: RequestBody?,
        @Part("satuan") satuan: RequestBody?,
        @Part("deskripsi") deskripsi: RequestBody?,
        @Part file: MultipartBody.Part
    ): ResponseAddProduct

    @Multipart
    @POST("produk/{id}")
    suspend fun editProduct(
        @Path("id") id: Int,
        @Part("id_mitra") idMitra: Int,
        @Part("nama_produk") namaProduk: RequestBody?,
        @Part("harga") harga: RequestBody?,
        @Part("satuan") satuan: RequestBody?,
        @Part("deskripsi") deskripsi: RequestBody?,
        @Part file: MultipartBody.Part?,
        @Part("gambar_lama") gambarLama: RequestBody?
    ): ResponseEditProduct

    @GET("produkById/{id}")
    suspend fun getProductById(@Path("id") id: Int): ResponseProdukById

    @DELETE("produk/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): ResponseDeleteProduct

    // marker
    @GET("poi/{id}")
    suspend fun getMarkerByIdTour(@Path("id") id: Int): Response<List<ResponseGetMarkerItem>>

    @POST("poi")
    suspend fun addMarker(@Body requestAddMarker: RequestAddMarker): ResponseAddMarker
}