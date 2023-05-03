package com.indexdev.partnerin.ui.editproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indexdev.partnerin.data.Repository
import com.indexdev.partnerin.data.api.Resource
import com.indexdev.partnerin.data.model.response.ResponseDeleteProduct
import com.indexdev.partnerin.data.model.response.ResponseEditProduct
import com.indexdev.partnerin.data.model.response.ResponseProdukById
import com.indexdev.partnerin.ui.reduceFileImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditProductViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _responseProductById = MutableLiveData<Resource<ResponseProdukById>>()
    val responseProductById: LiveData<Resource<ResponseProdukById>> get() = _responseProductById

    fun getProductById(id: Int) {
        viewModelScope.launch {
            _responseProductById.postValue(Resource.loading())
            try {
                _responseProductById.postValue(Resource.success(repository.getProductById(id)))
            } catch (e: Exception) {
                _responseProductById.postValue(
                    Resource.error(
                        e.localizedMessage ?: "Error occurred"
                    )
                )
            }
        }
    }


    private val _responseEditProduct = MutableLiveData<Resource<ResponseEditProduct>>()
    val responseEditProduct: LiveData<Resource<ResponseEditProduct>> get() = _responseEditProduct

    fun editProduct(
        idProduct: Int,
        idMitra: Int,
        namaProduk: String,
        harga: String,
        satuan: String,
        deskripsi: String,
        gambar: File?,
        gambarLama: String
    ) {
        val fileImage =
            gambar?.let { reduceFileImage(it).asRequestBody("image/jpg".toMediaTypeOrNull()) }
        val productImage =
            fileImage?.let { MultipartBody.Part.createFormData("gambar", gambar.name, it) }
        val productName = namaProduk.toRequestBody("text/plain".toMediaType())
        val price = harga.toRequestBody("text/plain".toMediaType())
        val unit = satuan.toRequestBody("text/plain".toMediaType())
        val description = deskripsi.toRequestBody("text/plain".toMediaType())
        val oldImage = gambarLama.toRequestBody("text/plain".toMediaType())

        viewModelScope.launch {
            _responseEditProduct.postValue(Resource.loading())
            try {
                _responseEditProduct.postValue(
                    Resource.success(
                        repository.editProduct(
                            id = idProduct,
                            idMitra = idMitra,
                            namaProduk = productName,
                            harga = price,
                            satuan = unit,
                            deskripsi = description,
                            file = productImage,
                            gambarLama = oldImage
                        )
                    )
                )
            } catch (e: Exception) {
                _responseEditProduct.postValue(
                    Resource.error(
                        e.localizedMessage ?: "Error Occurred"
                    )
                )
            }
        }
    }

    private val _responseDeleteProduct = MutableLiveData<Resource<ResponseDeleteProduct>>()
    val responseDeleteProduct: LiveData<Resource<ResponseDeleteProduct>> get() = _responseDeleteProduct

    fun deleteProduct(idProduct: Int) {
        viewModelScope.launch {
            _responseDeleteProduct.postValue(Resource.loading())
            try {
                _responseDeleteProduct.postValue(Resource.success(repository.deleteProduct(idProduct)))
            } catch (e: Exception) {
                _responseDeleteProduct.postValue(
                    Resource.error(
                        e.localizedMessage ?: "Error Occurred"
                    )
                )
            }
        }

    }
}